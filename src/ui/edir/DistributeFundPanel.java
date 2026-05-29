package ui.edir;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import service.EdirService;

public class DistributeFundPanel extends JPanel {
    private final JPanel parentWrapper;
    private final EdirService edirService;
    private final String groupName;

    private JComboBox<ClaimItem> cmbClaims;
    private JTextField txtDisbursedSum;
    private JTextField txtApprovedBy;
    private JTextArea txtNotes;

    private double currentAvailableBalance = 0.0;
    private List<Map<String, String>> pendingClaimsData;

    // Simple helper class to store both internal DB IDs and viewable Text descriptions inside JComboBox components
    private static class ClaimItem {
        String id;
        String displayText;
        double requestedAmount;

        public ClaimItem(String id, String displayText, double requestedAmount) {
            this.id = id;
            this.displayText = displayText;
            this.requestedAmount = requestedAmount;
        }
        @Override
        public String toString() { return displayText; }
    }

    public DistributeFundPanel(JPanel parentWrapper, EdirService edirService, String groupName) {
        this.parentWrapper = parentWrapper;
        this.edirService = edirService;
        this.groupName = groupName;

        setLayout(new BorderLayout(25, 25));
        setBackground(new Color(253, 247, 237));
        setBorder(BorderFactory.createEmptyBorder(35, 35, 35, 35));

        fetchVaultStatus();
        initHeader();
        initFormLayout();
    }

    private void fetchVaultStatus() {
        Map<String, String> details = edirService.getGroupDetails(groupName);
        if (details != null && details.containsKey("fund_balance")) {
            try {
                currentAvailableBalance = Double.parseDouble(details.get("fund_balance"));
            } catch (NumberFormatException e) {
                currentAvailableBalance = 0.0;
            }
        }
    }

    private void initHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Authorize Fund Capital Payout Distribution");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitle.setForeground(new Color(101, 31, 16));

        JLabel lblLimit = new JLabel(String.format("Max Available Reserves: %,.2f ETB", currentAvailableBalance));
        lblLimit.setFont(new Font("SansSerif", Font.ITALIC | Font.BOLD, 14));
        lblLimit.setForeground(new Color(46, 117, 89));

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(lblLimit, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);
    }

    private void initFormLayout() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 1. SELECT TARGET EMERGENCY CASE FROM DATABASE
        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("Select Linked Emergency Claim Case:"), gbc);

        cmbClaims = new JComboBox<>();
        cmbClaims.setPreferredSize(new Dimension(350, 35));

        // Load pending emergency files directly from PostgreSQL transactions
        // Crucial cast: edirService must contain your implementation model rules
        try {
            // If your service layer wraps your DAO, cast or invoke directly
            java.lang.reflect.Method m = edirService.getClass().getMethod("getPendingClaimsByGroup", String.class);
            pendingClaimsData = (List<Map<String, String>>) m.invoke(edirService, groupName);
        } catch (Exception e) {
            // Direct Fallback if you mapped it cleanly into your standard EdirService interface layout
            pendingClaimsData = edirService.getPendingClaimsByGroup(groupName);
        }

        if (pendingClaimsData != null && !pendingClaimsData.isEmpty()) {
            for (Map<String, String> claim : pendingClaimsData) {
                double reqAmt = Double.parseDouble(claim.getOrDefault("amount", "0"));
                String display = "Claim #" + claim.get("tx_id") + " - " + claim.get("member_name") + " (" + claim.get("description") + ")";
                cmbClaims.addItem(new ClaimItem(claim.get("tx_id"), display, reqAmt));
            }
        } else {
            cmbClaims.addItem(new ClaimItem("-1", "⚠️ No unresolved emergency cases found in database", 0));
        }

        gbc.gridx = 1;
        form.add(cmbClaims, gbc);

        // 2. DISBURSED PAYOUT AMOUNT FIELD
        gbc.gridx = 0; gbc.gridy = 1;
        form.add(new JLabel("Amount to Distribute (ETB):"), gbc);
        txtDisbursedSum = new JTextField();
        txtDisbursedSum.setPreferredSize(new Dimension(350, 35));
        gbc.gridx = 1;
        form.add(txtDisbursedSum, gbc);

        // AUTOMATED AUTO-FILL HOOK: When an emergency item is picked, pre-populate its exact recorded coverage sum
        cmbClaims.addActionListener(e -> {
            ClaimItem selected = (ClaimItem) cmbClaims.getSelectedItem();
            if (selected != null && !selected.id.equals("-1")) {
                txtDisbursedSum.setText(String.valueOf(selected.requestedAmount));
            }
        });

        // 3. AUTHORIZING OFFICER INPUT
        gbc.gridx = 0; gbc.gridy = 2;
        form.add(new JLabel("Authorized Approver Name:"), gbc);
        txtApprovedBy = new JTextField();
        txtApprovedBy.setPreferredSize(new Dimension(350, 35));
        gbc.gridx = 1;
        form.add(txtApprovedBy, gbc);

        // 4. PAYOUT DESCRIPTIONS AND AUDIT LOG NOTES
        gbc.gridx = 0; gbc.gridy = 3;
        form.add(new JLabel("Distribution Audit Notes:"), gbc);
        txtNotes = new JTextArea(4, 20);
        txtNotes.setLineWrap(true);
        txtNotes.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(txtNotes);
        gbc.gridx = 1;
        form.add(scroll, gbc);

        // 5. ACTION BUTTON EXECUTION LAYOUT ROW
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        actions.setOpaque(false);

        JButton btnCancel = new JButton("Cancel");
        btnCancel.setPreferredSize(new Dimension(100, 38));
        btnCancel.addActionListener(e -> returnToDashboardView());

        JButton btnConfirm = new JButton("Approve & Disburse");
        btnConfirm.setPreferredSize(new Dimension(180, 38));
        btnConfirm.setBackground(new Color(46, 117, 89));
        btnConfirm.setForeground(Color.WHITE);

        btnConfirm.addActionListener(e -> {
            ClaimItem selectedClaim = (ClaimItem) cmbClaims.getSelectedItem();
            String sumStr = txtDisbursedSum.getText().trim();
            String officer = txtApprovedBy.getText().trim();
            String notes = txtNotes.getText().trim();

            if (selectedClaim == null || selectedClaim.id.equals("-1")) {
                JOptionPane.showMessageDialog(this, "Payout requires a valid linked pending emergency case profile.", "Execution Blocked", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (sumStr.isEmpty() || officer.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please verify both the distribution value and authorizing signatory data.", "Execution Alert", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                double payoutValue = Double.parseDouble(sumStr);

                if (payoutValue > currentAvailableBalance) {
                    JOptionPane.showMessageDialog(this, "Transaction Rejected. Insufficient reserve capitals in group vault pool.", "Overdraft Limit Protection", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Pass the specific case execution transaction tracking ID cleanly to database layer
                boolean success = edirService.authorizePayout(groupName, selectedClaim.id, payoutValue, officer, notes);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Financial payout successfully authorized. Linked emergency case has been closed.");
                    returnToDashboardView();
                } else {
                    JOptionPane.showMessageDialog(this, "An unexpected database synchronization issue occurred.", "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid numeric amount inside the distribution field.", "Format Mismatch", JOptionPane.ERROR_MESSAGE);
            }
        });

        actions.add(btnCancel);
        actions.add(btnConfirm);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 12, 12, 12);
        form.add(actions, gbc);

        add(form, BorderLayout.CENTER);
    }

    private void returnToDashboardView() {
        for (Component comp : parentWrapper.getComponents()) {
            if (comp instanceof EdirGroupDetailPanel) {
                ((EdirGroupDetailPanel) comp).refreshDashboardMetricsAndLedger();
            }
        }
        CardLayout cl = (CardLayout) parentWrapper.getLayout();
        cl.show(parentWrapper, "EdirDetail");
    }
}