package ui.edir;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import java.util.Map;
import service.EdirService;

public class EmergencyCasePanel extends JPanel {
    private final JPanel parentWrapper;
    private final EdirService edirService;
    private final String groupName;

    private JComboBox<String> cmbMembers;
    private JComboBox<String> cmbCaseType;
    private JTextField txtAmount;
    private JTextArea txtDescription;

    public EmergencyCasePanel(JPanel parentWrapper, EdirService edirService, String groupName) {
        this.parentWrapper = parentWrapper;
        this.edirService = edirService;
        this.groupName = groupName;

        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(253, 247, 237));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        initHeader();
        initForm();
    }

    private void initHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("File New Emergency Claim - " + groupName);
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

        // 1. Live Member Lookup dropdown populated directly from PostgreSQL profiles
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblMem = new JLabel("Select Affected Member:");
        lblMem.setFont(new Font("SansSerif", Font.BOLD, 14));
        formContainer.add(lblMem, gbc);

        cmbMembers = new JComboBox<>();
        cmbMembers.setPreferredSize(new Dimension(300, 35));
        List<Map<String, String>> membersList = edirService.getMembersByGroup(groupName);
        for (Map<String, String> member : membersList) {
            cmbMembers.addItem(member.get("full_name"));
        }
        gbc.gridx = 1;
        formContainer.add(cmbMembers, gbc);

        // 2. Incident Type Field
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblType = new JLabel("Emergency Severity Type:");
        lblType.setFont(new Font("SansSerif", Font.BOLD, 14));
        formContainer.add(lblType, gbc);

        cmbCaseType = new JComboBox<>(new String[]{"Funeral Assistance", "Critical Medical", "Property Loss Damage"});
        cmbCaseType.setPreferredSize(new Dimension(300, 35));
        gbc.gridx = 1;
        formContainer.add(cmbCaseType, gbc);

        // 3. Financial Request Sum Allocation Textbox
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblAmt = new JLabel("Requested Coverage Sum (ETB):");
        lblAmt.setFont(new Font("SansSerif", Font.BOLD, 14));
        formContainer.add(lblAmt, gbc);

        txtAmount = new JTextField();
        txtAmount.setPreferredSize(new Dimension(300, 35));
        gbc.gridx = 1;
        formContainer.add(txtAmount, gbc);

        // 4. Incident Narrative Logging Area
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel lblDesc = new JLabel("Incident Audit Narrative Log:");
        lblDesc.setFont(new Font("SansSerif", Font.BOLD, 14));
        formContainer.add(lblDesc, gbc);

        txtDescription = new JTextArea(4, 20);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(txtDescription);
        gbc.gridx = 1;
        formContainer.add(descScroll, gbc);

        // 5. Execution Buttons Panel Setup Row
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        btnPanel.setOpaque(false);

        JButton btnCancel = new JButton("Cancel");
        btnCancel.setPreferredSize(new Dimension(100, 38));
        btnCancel.addActionListener(e -> returnToDashboardView());

        JButton btnSubmit = new JButton("File Claim Record");
        btnSubmit.setPreferredSize(new Dimension(160, 38));
        btnSubmit.setBackground(new Color(197, 48, 48));
        btnSubmit.setForeground(Color.WHITE);

        btnSubmit.addActionListener(e -> {
            String selectedMember = (String) cmbMembers.getSelectedItem();
            String caseType = (String) cmbCaseType.getSelectedItem();
            String amountStr = txtAmount.getText().trim();
            String desc = txtDescription.getText().trim();

            if (selectedMember == null || amountStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please ensure a member is selected and specify the claim coverage value.", "Validation Alert", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                double parsedAmt = Double.parseDouble(amountStr);

                // Write transaction row cleanly to transactions table using our service layer
                boolean isSaved = edirService.registerEmergencyCase(groupName, selectedMember, caseType, parsedAmt, desc);
                if (isSaved) {
                    JOptionPane.showMessageDialog(this, "Emergency case logged inside transaction ledger ledger queue.");
                    returnToDashboardView();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to submit database entry.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please insert a valid numerical value into the coverage sum entry box.", "Parsing Format Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(btnCancel);
        btnPanel.add(btnSubmit);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.insets = new Insets(25, 15, 10, 15);
        formContainer.add(btnPanel, gbc);

        add(formContainer, BorderLayout.CENTER);
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