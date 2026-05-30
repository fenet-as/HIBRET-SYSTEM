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
    private JTextArea txtCaseDetailsDisplay; // ✅ New UI component to clearly read details
    private JTextField txtDisbursedSum;
    private JTextField txtApprovedBy;
    private JTextArea txtNotes;

    private double currentAvailableBalance = 0.0;
    private List<Map<String, String>> pendingClaimsData;

    // Enhanced helper class to pass raw data details cleanly down to our display viewer card
    private static class ClaimItem {
        String id;
        String displayText;
        String fullDescription;
        String memberName;
        double requestedAmount;

        public ClaimItem(String id, String displayText, String memberName, String fullDescription, double requestedAmount) {
            this.id = id;
            this.displayText = displayText;
            this.memberName = memberName;
            this.fullDescription = fullDescription;
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

        // Trigger initial selection setup on load
        updateCaseDetailsDisplay();
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

    @SuppressWarnings("unchecked")
    private void initFormLayout() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 12, 10, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 1. SELECT TARGET EMERGENCY CASE FROM DATABASE
        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("Select Emergency Claim Case:"), gbc);

        cmbClaims = new JComboBox<>();
        cmbClaims.setPreferredSize(new Dimension(400, 35));

        try {
            java.lang.reflect.Method m = edirService.getClass().getMethod("getPendingClaimsByGroup", String.class);
            pendingClaimsData = (List<Map<String, String>>) m.invoke(edirService, groupName);
        } catch (Exception e) {
            pendingClaimsData = edirService.getPendingClaimsByGroup(groupName);
        }

        if (pendingClaimsData != null && !pendingClaimsData.isEmpty()) {
            for (Map<String, String> claim : pendingClaimsData) {
                double reqAmt = Double.parseDouble(claim.getOrDefault("amount", "0"));
                String id = claim.get("tx_id");
                String member = claim.get("member_name");
                String desc = claim.getOrDefault("description", "No case context recorded.");

                // Dropdown text stays clean and un-cluttered
                String shortDisplay = "Case #" + id + " : filed by " + member;
                cmbClaims.addItem(new ClaimItem(id, shortDisplay, member, desc, reqAmt));
            }
        } else {
            cmbClaims.addItem(new ClaimItem("-1", "⚠️ No unresolved emergency cases found", "N/A", "N/A", 0));
        }

        gbc.gridx = 1;
        form.add(cmbClaims, gbc);

        // 1b. ✅ LIVE CASE DETAILS INSPECTOR CARD (Fills the readability requirement)
        gbc.gridx = 0; gbc.gridy = 1;
        form.add(new JLabel("Selected Case Context Audit:"), gbc);

        txtCaseDetailsDisplay = new JTextArea(5, 25);
        txtCaseDetailsDisplay.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtCaseDetailsDisplay.setBackground(new Color(245, 240, 230));
        txtCaseDetailsDisplay.setEditable(false);
        txtCaseDetailsDisplay.setLineWrap(true);
        txtCaseDetailsDisplay.setWrapStyleWord(true);
        txtCaseDetailsDisplay.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JScrollPane caseDetailsScroll = new JScrollPane(txtCaseDetailsDisplay);
        caseDetailsScroll.setPreferredSize(new Dimension(400, 100));
        gbc.gridx = 1;
        form.add(caseDetailsScroll, gbc);

        // 2. DISBURSED PAYOUT AMOUNT FIELD
        gbc.gridx = 0; gbc.gridy = 2;
        form.add(new JLabel("Amount to Distribute (ETB):"), gbc);
        txtDisbursedSum = new JTextField();
        txtDisbursedSum.setPreferredSize(new Dimension(400, 35));
        gbc.gridx = 1;
        form.add(txtDisbursedSum, gbc);

        // COMBINED CHANGE LISTENER HOOK: Update details text view and auto-fill distribution sum box layout
        cmbClaims.addActionListener(e -> updateCaseDetailsDisplay());

        // 3. AUTHORIZING OFFICER INPUT
        gbc.gridx = 0; gbc.gridy = 3;
        form.add(new JLabel("Authorized Approver Name:"), gbc);
        txtApprovedBy = new JTextField();
        txtApprovedBy.setPreferredSize(new Dimension(400, 35));
        gbc.gridx = 1;
        form.add(txtApprovedBy, gbc);

        // 4. PAYOUT DESCRIPTIONS AND AUDIT LOG NOTES
        gbc.gridx = 0; gbc.gridy = 4;
        form.add(new JLabel("Distribution Audit Notes:"), gbc);
        txtNotes = new JTextArea(3, 20);
        txtNotes.setLineWrap(true);
        txtNotes.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(txtNotes);
        scroll.setPreferredSize(new Dimension(400, 70));
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
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 12, 12, 12);
        form.add(actions, gbc);

        add(form, BorderLayout.CENTER);
    }

    // ✅ HELPER: Extracts layout tracking fields dynamically to refresh the details card text viewport buffer layout
    private void updateCaseDetailsDisplay() {
        if (cmbClaims == null || txtCaseDetailsDisplay == null) return;

        ClaimItem selected = (ClaimItem) cmbClaims.getSelectedItem();
        if (selected != null && !selected.id.equals("-1")) {
            txtDisbursedSum.setText(String.valueOf(selected.requestedAmount));

            StringBuilder sb = new StringBuilder();
            sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            sb.append(" CASE TRACKING ID : ").append(selected.id).append("\n");
            sb.append(" FILING MEMBER    : ").append(selected.memberName).append("\n");
            sb.append(" REQUESTED COV.   : ").append(String.format("%,.2f ETB", selected.requestedAmount)).append("\n");
            sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            sb.append(" INCIDENT LOG DETAILS:\n ").append(selected.fullDescription);

            txtCaseDetailsDisplay.setText(sb.toString());
            txtCaseDetailsDisplay.setCaretPosition(0); // Scroll view back to top
        } else {
            txtCaseDetailsDisplay.setText("\n\n   No emergency claims selected.");
            txtDisbursedSum.setText("");
        }
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