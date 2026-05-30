package ui.edir;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import service.EdirService;

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

        lblTitle = new JLabel("File Emergency Claim for " + groupDisplayName);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
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
        JLabel lblMem = new JLabel("Select Affected Group Member:");
        lblMem.setFont(new Font("SansSerif", Font.BOLD, 14));
        formContainer.add(lblMem, gbc);

        cmbMembers = new JComboBox<>();
        cmbMembers.setFont(new Font("SansSerif", Font.PLAIN, 13));
        cmbMembers.setPreferredSize(new Dimension(300, 35));
        gbc.gridx = 1;
        formContainer.add(cmbMembers, gbc);

        // 2. Financial Request Sum Allocation Textbox
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblAmt = new JLabel("Requested Coverage Amount (ETB):");
        lblAmt.setFont(new Font("SansSerif", Font.BOLD, 14));
        formContainer.add(lblAmt, gbc);

        txtAmount = new JTextField();
        txtAmount.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtAmount.setPreferredSize(new Dimension(300, 35));
        gbc.gridx = 1;
        formContainer.add(txtAmount, gbc);

        // 3. Incident Narrative Logging Area
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblDesc = new JLabel("Incident Narrative / Justification Summary:");
        lblDesc.setFont(new Font("SansSerif", Font.BOLD, 14));
        formContainer.add(lblDesc, gbc);

        txtDescription = new JTextArea(4, 20);
        txtDescription.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(txtDescription);
        gbc.gridx = 1;
        formContainer.add(descScroll, gbc);

        // 4. Execution Buttons Panel Setup Row
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        btnPanel.setOpaque(false);

        JButton btnCancel = new JButton("Cancel");
        btnCancel.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnCancel.setPreferredSize(new Dimension(100, 38));
        btnCancel.addActionListener(e -> returnToDashboardView());

        JButton btnSubmit = new JButton("Submit Claim Request");
        btnSubmit.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnSubmit.setPreferredSize(new Dimension(160, 38));
        btnSubmit.setBackground(new Color(197, 48, 48));
        btnSubmit.setForeground(Color.WHITE);

        btnSubmit.addActionListener(e -> {
            UIManager.put("OptionPane.messageFont", new Font("SansSerif", Font.PLAIN, 14));
            UIManager.put("OptionPane.buttonFont", new Font("SansSerif", Font.PLAIN, 13));

            String selectedMember = (String) cmbMembers.getSelectedItem();
            String amountStr = txtAmount.getText().trim();
            String desc = txtDescription.getText().trim();

            if (selectedMember == null || amountStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a member and state the requested fund allocation total.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                double parsedAmt = Double.parseDouble(amountStr);
                String fallbackCaseType = "General Emergency";

                boolean isSaved = edirService.registerEmergencyCase(this.groupId, selectedMember, fallbackCaseType, parsedAmt, desc);
                if (isSaved) {
                    JOptionPane.showMessageDialog(this, "Emergency financial coverage claim filed successfully and queued for operational processing.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    returnToDashboardView();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to write structural parameters into system data files.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "The numerical values assigned to financial request properties could not be formatted cleanly.", "Format Parsing Error", JOptionPane.ERROR_MESSAGE);
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
            lblTitle.setText("File Emergency Claim for " + this.groupDisplayName);
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