package ui.edir;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import service.EdirService;
import util.LanguageManager;
import util.FontManager; // ✅ Imported FontManager

public class EmergencyCasePanel extends JPanel {
    private final JPanel parentWrapper;
    private final EdirService edirService;

    private final int groupId;
    private String groupDisplayName = "Loading Group...";

    private JLabel lblTitle;
    private JComboBox<String> cmbMembers;
    private JTextField txtAmount;
    private JTextArea txtDescription;

    public EmergencyCasePanel(JPanel parentWrapper, EdirService edirService, int groupId) {
        this.parentWrapper = parentWrapper;
        this.edirService = edirService;
        this.groupId = groupId;

        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(253, 247, 237));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        initHeader();
        initForm();
        loadGroupMetadataAndMembers();
    }

    private void initHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        // ✅ Localized Panel Header String Builder Formatter Context
        lblTitle = new JLabel(LanguageManager.getFormattedString("edir.emergency.title", groupDisplayName));
        lblTitle.setFont(FontManager.getBoldFont(22));
        lblTitle.setForeground(new Color(101, 31, 16));

        headerPanel.add(lblTitle, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);
    }

    private void initForm() {
        JPanel formContainer = new JPanel(new GridBagLayout());
        formContainer.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 1. Affected Member Combo Field
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblMem = new JLabel(LanguageManager.getString("edir.emergency.lbl.select_member"));
        lblMem.setFont(FontManager.getBoldFont(14));
        formContainer.add(lblMem, gbc);

        cmbMembers = new JComboBox<>();
        cmbMembers.setFont(FontManager.getPlainFont(13));
        cmbMembers.setPreferredSize(new Dimension(300, 35));
        gbc.gridx = 1;
        formContainer.add(cmbMembers, gbc);

        // 2. Financial Request Sum Allocation Textbox (Shifted grid coordinates up to gridy = 1)
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblAmt = new JLabel(LanguageManager.getString("edir.emergency.lbl.coverage_sum"));
        lblAmt.setFont(FontManager.getBoldFont(14));
        formContainer.add(lblAmt, gbc);

        txtAmount = new JTextField();
        txtAmount.setFont(FontManager.getPlainFont(14));
        txtAmount.setPreferredSize(new Dimension(300, 35));
        gbc.gridx = 1;
        formContainer.add(txtAmount, gbc);

        // 3. Incident Narrative Logging Area (Shifted grid coordinates up to gridy = 2)
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblDesc = new JLabel(LanguageManager.getString("edir.emergency.lbl.narrative"));
        lblDesc.setFont(FontManager.getBoldFont(14));
        formContainer.add(lblDesc, gbc);

        txtDescription = new JTextArea(4, 20);
        txtDescription.setFont(FontManager.getPlainFont(14));
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(txtDescription);
        gbc.gridx = 1;
        formContainer.add(descScroll, gbc);

        // 4. Execution Buttons Panel Setup Row (Shifted grid coordinates up to gridy = 3)
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        btnPanel.setOpaque(false);

        JButton btnCancel = new JButton(LanguageManager.getString("edir.emergency.btn.cancel"));
        btnCancel.setFont(FontManager.getBoldFont(13));
        btnCancel.setPreferredSize(new Dimension(100, 38));
        btnCancel.addActionListener(e -> returnToDashboardView());

        JButton btnSubmit = new JButton(LanguageManager.getString("edir.emergency.btn.submit"));
        btnSubmit.setFont(FontManager.getBoldFont(13));
        btnSubmit.setPreferredSize(new Dimension(160, 38));
        btnSubmit.setBackground(new Color(197, 48, 48));
        btnSubmit.setForeground(Color.WHITE);

        btnSubmit.addActionListener(e -> {
            UIManager.put("OptionPane.messageFont", FontManager.getPlainFont(14));
            UIManager.put("OptionPane.buttonFont", FontManager.getPlainFont(13));

            String selectedMember = (String) cmbMembers.getSelectedItem();
            String amountStr = txtAmount.getText().trim();
            String desc = txtDescription.getText().trim();

            if (selectedMember == null || amountStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, LanguageManager.getString("edir.emergency.err.validation"), LanguageManager.getString("msg.validation_error"), JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                double parsedAmt = Double.parseDouble(amountStr);

                // ✅ SEVERITY COMBO REMOVED: Passes a standard default background type string
                // to fulfill backend tracking variables securely without triggering UI translation layout drops
                String fallbackCaseType = "General Emergency";

                boolean isSaved = edirService.registerEmergencyCase(this.groupId, selectedMember, fallbackCaseType, parsedAmt, desc);
                if (isSaved) {
                    JOptionPane.showMessageDialog(this, LanguageManager.getString("edir.emergency.success"), LanguageManager.getString("msg.success"), JOptionPane.INFORMATION_MESSAGE);
                    returnToDashboardView();
                } else {
                    JOptionPane.showMessageDialog(this, LanguageManager.getString("edir.emergency.err.database"), LanguageManager.getString("msg.error"), JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, LanguageManager.getString("edir.emergency.err.parsing"), LanguageManager.getString("msg.parsing_error"), JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(btnCancel);
        btnPanel.add(btnSubmit);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.insets = new Insets(25, 15, 10, 15);
        formContainer.add(btnPanel, gbc);

        add(formContainer, BorderLayout.CENTER);
    }

    private void loadGroupMetadataAndMembers() {
        Map<String, String> details = edirService.getGroupDetails(this.groupId);
        if (details != null && !details.isEmpty()) {
            this.groupDisplayName = details.getOrDefault("name", "Edir Group");
            lblTitle.setText(LanguageManager.getFormattedString("edir.emergency.title", this.groupDisplayName));
        }

        cmbMembers.removeAllItems();
        List<Map<String, String>> membersList = edirService.getMembersByGroup(this.groupId);
        for (Map<String, String> member : membersList) {
            cmbMembers.addItem(member.get("full_name"));
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