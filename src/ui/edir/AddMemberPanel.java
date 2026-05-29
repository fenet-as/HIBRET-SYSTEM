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

        gbc.gridwidth = 1;
        gbc.gridy = 1; gbc.gridx = 0;
        add(new JLabel("Full Name:"), gbc);

        txtFullName = new JTextField(20);
        txtFullName.setPreferredSize(new Dimension(200, 35));
        gbc.gridx = 1;
        add(txtFullName, gbc);

        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionRow.setOpaque(false);

        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> ((CardLayout) containerPanel.getLayout()).show(containerPanel, "EdirGroupDetail"));

        JButton btnSave = new JButton("Add Registry");
        btnSave.addActionListener(e -> {
            String fullName = txtFullName.getText().trim();
            if (fullName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Member name cannot be blank.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Write to service layer database framework
            edirService.addMemberToGroup(fullName, group.getId());
            JOptionPane.showMessageDialog(this, fullName + " has been successfully enrolled!");

            // Force the metrics panel card layout to refresh numbers instantly
            detailPanel.refreshDashboardMetricsAndLedger();

            ((CardLayout) containerPanel.getLayout()).show(containerPanel, "EdirGroupDetail");
        });

        actionRow.add(btnCancel);
        actionRow.add(btnSave);
        gbc.gridy = 2; gbc.gridx = 0; gbc.gridwidth = 2;
        add(actionRow, gbc);
    }




}