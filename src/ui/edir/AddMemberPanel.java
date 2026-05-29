package ui.edir;

import javax.swing.*;
import java.awt.*;
import model.EdirGroup;
import service.EdirService;
import service.impl.EdirServiceImpl;

public class AddMemberPanel extends JPanel {
    private final JPanel containerPanel;
    private final EdirGroup group;
    private final EdirGroupDetailPanel detailPanel;
    private final EdirService edirService = new EdirServiceImpl();

    private JTextField txtFullName;
    private JTextField txtPhone; // Added phone field variable

    public AddMemberPanel(JPanel containerPanel, EdirGroup group, EdirGroupDetailPanel detailPanel) {
        this.containerPanel = containerPanel;
        this.group = group;
        this.detailPanel = detailPanel;

        setBackground(new Color(252, 249, 242));
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("👤 Register New Member to " + group.getName());
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(new Color(101, 53, 15));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(title, gbc);

        // --- Full Name Input ---
        gbc.gridwidth = 1;
        gbc.gridy = 1; gbc.gridx = 0;
        add(new JLabel("Full Name:"), gbc);

        txtFullName = new JTextField(20);
        txtFullName.setPreferredSize(new Dimension(200, 35));
        gbc.gridx = 1;
        add(txtFullName, gbc);

        // --- Phone Number Input (Added to match your database schema) ---
        gbc.gridy = 2; gbc.gridx = 0;
        add(new JLabel("Phone Number:"), gbc);

        txtPhone = new JTextField(20);
        txtPhone.setPreferredSize(new Dimension(200, 35));
        gbc.gridx = 1;
        add(txtPhone, gbc);

        // --- Actions Layout ---
        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionRow.setOpaque(false);

        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> ((CardLayout) containerPanel.getLayout()).show(containerPanel, "EdirGroupDetail"));

        JButton btnSave = new JButton("Add Registry");

        btnSave.addActionListener(e -> {
            String fullName = txtFullName.getText().trim();
            String phone = txtPhone.getText().trim(); // Capture phone input text

            if (fullName.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All input fields are required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // ✅ FIX: Pass exactly three strings: groupName (String), fullName (String), phone (String)
            boolean success = edirService.addMemberToGroup(group.getName(), fullName, phone);

            if (success) {
                JOptionPane.showMessageDialog(this, fullName + " has been successfully enrolled!");

                // Clear inputs for next time
                txtFullName.setText("");
                txtPhone.setText("");

                // Force the metrics panel card layout to refresh numbers instantly
                detailPanel.refreshDashboardMetricsAndLedger();

                ((CardLayout) containerPanel.getLayout()).show(containerPanel, "EdirGroupDetail");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save record to the database.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        actionRow.add(btnCancel);
        actionRow.add(btnSave);

        gbc.gridy = 3; // Shifted down because of the new input field row
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        add(actionRow, gbc);
    }
}