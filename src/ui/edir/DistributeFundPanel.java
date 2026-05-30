package ui.edir;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import service.EdirService;

public class DistributeFundPanel extends JPanel {
    private final JPanel parentWrapper;
    private final EdirService edirService;

    private final int groupId;
    private String groupDisplayName = "Loading Group...";

    private JComboBox<ClaimItem> cmbClaims;
    private JTextArea txtCaseDetailsDisplay;
    private JTextField txtDisbursedSum;
    private JTextField txtApprovedBy;
    private JTextArea txtNotes;

    private double currentAvailableBalance = 0.0;
    private List<Map<String, String>> pendingClaimsData;

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

    public DistributeFundPanel(JPanel parentWrapper, EdirService edirService, int groupId) {
        this.parentWrapper = parentWrapper;
        this.edirService = edirService;
        this.groupId = groupId;

        setLayout(new BorderLayout(25, 25));
        setBackground(new Color(253, 247, 237));
        setBorder(BorderFactory.createEmptyBorder(35, 35, 35, 35));

        fetchVaultStatus();
        initHeader();
        initFormLayout();

        updateCaseDetailsDisplay();
    }

    private void fetchVaultStatus() {
        Map<String, String> details = edirService.getGroupDetails(this.groupId);
        if (details != null) {
            this.groupDisplayName = details.getOrDefault("name", "Edir Group");
            if (details.containsKey("fund_balance")) {
                try {
                    currentAvailableBalance = Double.parseDouble(details.get("fund_balance"));
                } catch (NumberFormatException e) {
                    currentAvailableBalance = 0.0;
                }
            }
        }
    }

    private void initHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Distribute Emergency Payout Funds");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitle.setForeground(new Color(101, 31, 16));

        String dynamicReserves = String.format("%,.2f", currentAvailableBalance);
        JLabel lblLimit = new JLabel("Available Vault Balance Limit: " + dynamicReserves + " ETB");
        lblLimit.setFont(new Font("SansSerif", Font.BOLD, 14));
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
        form.add(createFormLabel("Select Active Emergency Claim Case File"), gbc);

        cmbClaims = new JComboBox<>();
        cmbClaims.setFont(new Font("SansSerif", Font.PLAIN, 13));
        cmbClaims.setPreferredSize(new Dimension(400, 35));

        Object renderer = cmbClaims.getRenderer();
        if (renderer instanceof JComponent) {
            ((JComponent) renderer).setFont(new Font("SansSerif", Font.PLAIN, 13));
        }

        try {
            java.lang.reflect.Method m = edirService.getClass().getMethod("getPendingClaimsByGroup", int.class);
            pendingClaimsData = (List<Map<String, String>>) m.invoke(edirService, this.groupId);
        } catch (Exception e) {
            pendingClaimsData = edirService.getPendingClaimsByGroup(this.groupId);
        }

        if (pendingClaimsData != null && !pendingClaimsData.isEmpty()) {
            for (Map<String, String> claim : pendingClaimsData) {
                double reqAmt = Double.parseDouble(claim.getOrDefault("amount", "0"));
                String id = claim.get("tx_id");
                String member = claim.get("member_name");
                String desc = claim.getOrDefault("description", "No case context recorded.");

                String shortDisplay = "Case #" + id + " : filed by " + member;
                cmbClaims.addItem(new ClaimItem(id, shortDisplay, member, desc, reqAmt));
            }
        } else {
            cmbClaims.addItem(new ClaimItem("-1", "-- No outstanding claims found --", "N/A", "N/A", 0));
        }

        gbc.gridx = 1;
        form.add(cmbClaims, gbc);

        // 1b. LIVE CASE DETAILS INSPECTOR CARD
        gbc.gridx = 0; gbc.gridy = 1;
        form.add(createFormLabel("Case Details Overview Context"), gbc);

        txtCaseDetailsDisplay = new JTextArea(5, 25);
        txtCaseDetailsDisplay.setFont(new Font("SansSerif", Font.PLAIN, 12));
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
        form.add(createFormLabel("Approved Payout Allocation Amount (ETB)"), gbc);
        txtDisbursedSum = new JTextField();
        txtDisbursedSum.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtDisbursedSum.setPreferredSize(new Dimension(400, 35));
        gbc.gridx = 1;
        form.add(txtDisbursedSum, gbc);

        cmbClaims.addActionListener(e -> updateCaseDetailsDisplay());

        // 3. AUTHORIZING OFFICER INPUT
        gbc.gridx = 0; gbc.gridy = 3;
        form.add(createFormLabel("Authorizing Executive Officer Name"), gbc);
        txtApprovedBy = new JTextField();
        txtApprovedBy.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtApprovedBy.setPreferredSize(new Dimension(400, 35));
        gbc.gridx = 1;
        form.add(txtApprovedBy, gbc);

        // 4. PAYOUT DESCRIPTIONS AND AUDIT LOG NOTES
        gbc.gridx = 0; gbc.gridy = 4;
        form.add(createFormLabel("Audit Ledger Comments and Documentation Notes"), gbc);
        txtNotes = new JTextArea(3, 20);
        txtNotes.setFont(new Font("SansSerif", Font.PLAIN, 13));
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
        btnCancel.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnCancel.setPreferredSize(new Dimension(100, 38));
        btnCancel.addActionListener(e -> returnToDashboardView());

        JButton btnConfirm = new JButton("Authorize Distribution");
        btnConfirm.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnConfirm.setPreferredSize(new Dimension(180, 38));
        btnConfirm.setBackground(new Color(46, 117, 89));
        btnConfirm.setForeground(Color.WHITE);

        btnConfirm.addActionListener(e -> {
            UIManager.put("OptionPane.messageFont", new Font("SansSerif", Font.PLAIN, 14));
            UIManager.put("OptionPane.buttonFont", new Font("SansSerif", Font.PLAIN, 13));

            ClaimItem selectedClaim = (ClaimItem) cmbClaims.getSelectedItem();
            String sumStr = txtDisbursedSum.getText().trim();
            String officer = txtApprovedBy.getText().trim();
            String notes = txtNotes.getText().trim();

            if (selectedClaim == null || selectedClaim.id.equals("-1")) {
                JOptionPane.showMessageDialog(this, "Please select a valid pending emergency claim case profile.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (sumStr.isEmpty() || officer.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all required operational input properties.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                double payoutValue = Double.parseDouble(sumStr);

                if (payoutValue > currentAvailableBalance) {
                    JOptionPane.showMessageDialog(this, "Transaction rejected: Allocation amount exceeds group available reserves vault balance.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                boolean success = edirService.authorizePayout(this.groupId, selectedClaim.id, payoutValue, officer, notes);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Emergency payout authorization completed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    returnToDashboardView();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update financial ledger. Please check database logs.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a completely valid decimal number for the payout sum.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        actions.add(btnCancel);
        actions.add(btnConfirm);
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 12, 12, 12);
        form.add(actions, gbc);

        add(form, BorderLayout.CENTER);
    }

    private void updateCaseDetailsDisplay() {
        if (cmbClaims == null || txtCaseDetailsDisplay == null) return;

        ClaimItem selected = (ClaimItem) cmbClaims.getSelectedItem();
        if (selected != null && !selected.id.equals("-1")) {
            txtDisbursedSum.setText(String.valueOf(selected.requestedAmount));

            StringBuilder sb = new StringBuilder();
            sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            sb.append("Claim ID Target: ").append(selected.id).append("\n");
            sb.append("Filing Member Profile: ").append(selected.memberName).append("\n");
            String formattedAmt = String.format("%,.2f", selected.requestedAmount);
            sb.append("Requested Payout Amount: ").append(formattedAmt).append(" ETB\n");
            sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            sb.append("Incident Context Description: ").append(selected.fullDescription);

            txtCaseDetailsDisplay.setText(sb.toString());
            txtCaseDetailsDisplay.setCaretPosition(0);
        } else {
            txtCaseDetailsDisplay.setText("-- No specific case profile currently highlighted --");
            txtDisbursedSum.setText("");
        }
    }

    private JLabel createFormLabel(String labelText) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        label.setForeground(Color.DARK_GRAY);
        return label;
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