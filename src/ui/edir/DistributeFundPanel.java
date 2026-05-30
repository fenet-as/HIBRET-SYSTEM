package ui.edir;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import service.EdirService;
import util.LanguageManager;
import util.FontManager; // ✅ Imported FontManager

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

        // ✅ Localized Header Title with FontManager Mapping
        JLabel lblTitle = new JLabel(LanguageManager.getString("edir.distribute.title"));
        lblTitle.setFont(FontManager.getBoldFont(22));
        lblTitle.setForeground(new Color(101, 31, 16));

        // ✅ Localized Available Reserves Format Tracker Label with FontManager Mapping
        String dynamicReserves = String.format("%,.2f", currentAvailableBalance);
        JLabel lblLimit = new JLabel(LanguageManager.getFormattedString("edir.distribute.max_reserves", dynamicReserves));
        lblLimit.setFont(FontManager.getBoldFont(14));
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

        // 1. SELECT TARGET EMERGENCY CASE FROM DATABASE - Localized Form Label with dynamic font
        gbc.gridx = 0; gbc.gridy = 0;
        form.add(createFormLabel(LanguageManager.getString("edir.distribute.lbl_select_claim")), gbc);

        cmbClaims = new JComboBox<>();
        cmbClaims.setFont(FontManager.getPlainFont(13));
        cmbClaims.setPreferredSize(new Dimension(400, 35));

        // Ensure the internal dropdown overlay list elements render the Amharic texts safely
        Object renderer = cmbClaims.getRenderer();
        if (renderer instanceof JComponent) {
            ((JComponent) renderer).setFont(FontManager.getPlainFont(13));
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
            // ✅ Localized Fallback Item Entry
            cmbClaims.addItem(new ClaimItem("-1", LanguageManager.getString("edir.distribute.no_claims"), "N/A", "N/A", 0));
        }

        gbc.gridx = 1;
        form.add(cmbClaims, gbc);

        // 1b. LIVE CASE DETAILS INSPECTOR CARD - Localized Form Label
        gbc.gridx = 0; gbc.gridy = 1;
        form.add(createFormLabel(LanguageManager.getString("edir.distribute.lbl_context")), gbc);

        txtCaseDetailsDisplay = new JTextArea(5, 25);
        txtCaseDetailsDisplay.setFont(FontManager.getPlainFont(12)); // ✅ Replaced hardcoded Font with fallback configuration
        txtCaseDetailsDisplay.setBackground(new Color(245, 240, 230));
        txtCaseDetailsDisplay.setEditable(false);
        txtCaseDetailsDisplay.setLineWrap(true);
        txtCaseDetailsDisplay.setWrapStyleWord(true);
        txtCaseDetailsDisplay.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JScrollPane caseDetailsScroll = new JScrollPane(txtCaseDetailsDisplay);
        caseDetailsScroll.setPreferredSize(new Dimension(400, 100));
        gbc.gridx = 1;
        form.add(caseDetailsScroll, gbc);

        // 2. DISBURSED PAYOUT AMOUNT FIELD - Localized Form Label
        gbc.gridx = 0; gbc.gridy = 2;
        form.add(createFormLabel(LanguageManager.getString("edir.distribute.lbl_amount")), gbc);
        txtDisbursedSum = new JTextField();
        txtDisbursedSum.setFont(FontManager.getPlainFont(14));
        txtDisbursedSum.setPreferredSize(new Dimension(400, 35));
        gbc.gridx = 1;
        form.add(txtDisbursedSum, gbc);

        cmbClaims.addActionListener(e -> updateCaseDetailsDisplay());

        // 3. AUTHORIZING OFFICER INPUT - Localized Form Label
        gbc.gridx = 0; gbc.gridy = 3;
        form.add(createFormLabel(LanguageManager.getString("edir.distribute.lbl_approver")), gbc);
        txtApprovedBy = new JTextField();
        txtApprovedBy.setFont(FontManager.getPlainFont(14));
        txtApprovedBy.setPreferredSize(new Dimension(400, 35));
        gbc.gridx = 1;
        form.add(txtApprovedBy, gbc);

        // 4. PAYOUT DESCRIPTIONS AND AUDIT LOG NOTES - Localized Form Label
        gbc.gridx = 0; gbc.gridy = 4;
        form.add(createFormLabel(LanguageManager.getString("edir.distribute.lbl_notes")), gbc);
        txtNotes = new JTextArea(3, 20);
        txtNotes.setFont(FontManager.getPlainFont(13));
        txtNotes.setLineWrap(true);
        txtNotes.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(txtNotes);
        scroll.setPreferredSize(new Dimension(400, 70));
        gbc.gridx = 1;
        form.add(scroll, gbc);

        // 5. ACTION BUTTON EXECUTION LAYOUT ROW - Localized Form Targets
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        actions.setOpaque(false);

        JButton btnCancel = new JButton(LanguageManager.getString("edir.distribute.btn_cancel"));
        btnCancel.setFont(FontManager.getBoldFont(13));
        btnCancel.setPreferredSize(new Dimension(100, 38));
        btnCancel.addActionListener(e -> returnToDashboardView());

        JButton btnConfirm = new JButton(LanguageManager.getString("edir.distribute.btn_confirm"));
        btnConfirm.setFont(FontManager.getBoldFont(13));
        btnConfirm.setPreferredSize(new Dimension(180, 38));
        btnConfirm.setBackground(new Color(46, 117, 89));
        btnConfirm.setForeground(Color.WHITE);

        btnConfirm.addActionListener(e -> {
            // Apply dynamic validation popup box configurations explicitly
            UIManager.put("OptionPane.messageFont", FontManager.getPlainFont(14));
            UIManager.put("OptionPane.buttonFont", FontManager.getPlainFont(13));

            ClaimItem selectedClaim = (ClaimItem) cmbClaims.getSelectedItem();
            String sumStr = txtDisbursedSum.getText().trim();
            String officer = txtApprovedBy.getText().trim();
            String notes = txtNotes.getText().trim();

            if (selectedClaim == null || selectedClaim.id.equals("-1")) {
                JOptionPane.showMessageDialog(this, LanguageManager.getString("edir.distribute.err.invalid_claim"), LanguageManager.getString("msg.error"), JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (sumStr.isEmpty() || officer.isEmpty()) {
                JOptionPane.showMessageDialog(this, LanguageManager.getString("edir.distribute.err.required"), LanguageManager.getString("msg.error"), JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                double payoutValue = Double.parseDouble(sumStr);

                if (payoutValue > currentAvailableBalance) {
                    JOptionPane.showMessageDialog(this, LanguageManager.getString("edir.distribute.err.overdraft"), LanguageManager.getString("msg.error"), JOptionPane.ERROR_MESSAGE);
                    return;
                }

                boolean success = edirService.authorizePayout(this.groupId, selectedClaim.id, payoutValue, officer, notes);
                if (success) {
                    JOptionPane.showMessageDialog(this, LanguageManager.getString("edir.distribute.success"), LanguageManager.getString("msg.success"), JOptionPane.INFORMATION_MESSAGE);
                    returnToDashboardView();
                } else {
                    JOptionPane.showMessageDialog(this, LanguageManager.getString("edir.distribute.err.db"), LanguageManager.getString("msg.error"), JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, LanguageManager.getString("edir.distribute.err.number"), LanguageManager.getString("msg.error"), JOptionPane.ERROR_MESSAGE);
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
            sb.append(LanguageManager.getFormattedString("edir.distribute.card.id", selected.id)).append("\n");
            sb.append(LanguageManager.getFormattedString("edir.distribute.card.member", selected.memberName)).append("\n");
            String formattedAmt = String.format("%,.2f", selected.requestedAmount);
            sb.append(LanguageManager.getFormattedString("edir.distribute.card.requested", formattedAmt)).append("\n");
            sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            sb.append(LanguageManager.getString("edir.distribute.card.incident")).append(" ").append(selected.fullDescription);

            txtCaseDetailsDisplay.setText(sb.toString());
            txtCaseDetailsDisplay.setCaretPosition(0);
        } else {
            txtCaseDetailsDisplay.setText(LanguageManager.getString("edir.distribute.no_selection"));
            txtDisbursedSum.setText("");
        }
    }

    private JLabel createFormLabel(String labelText) {
        JLabel label = new JLabel(labelText);
        label.setFont(FontManager.getBoldFont(13));
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