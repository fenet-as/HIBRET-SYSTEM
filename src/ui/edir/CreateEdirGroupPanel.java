package ui.edir;

import javax.swing.*;
import java.awt.*;
import service.EdirService;
import service.impl.EdirServiceImpl;

public class CreateEdirGroupPanel extends JPanel {
    private final JPanel containerPanel;
    private final EdirHomePanel homePanel; // ⭐ Added reference to trigger live screen updates
    private final EdirService edirService = new EdirServiceImpl();
    private JTextField txtName;
    private JTextField txtContribution;

    // ⭐ Updated Constructor to accept the parent EdirHomePanel
    public CreateEdirGroupPanel(JPanel containerPanel, EdirHomePanel homePanel) {
        this.containerPanel = containerPanel;
        this.homePanel = homePanel;

        setOpaque(false);
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("🆕 Initialize New Edir Association");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(new Color(101, 53, 15));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1; gbc.gridx = 0;
        add(new JLabel("Group Name:"), gbc);
        txtName = new JTextField(20);
        gbc.gridx = 1;
        add(txtName, gbc);

        gbc.gridy = 2; gbc.gridx = 0;
        add(new JLabel("Monthly Fixed Contribution (Birr):"), gbc);
        txtContribution = new JTextField(20);
        gbc.gridx = 1;
        add(txtContribution, gbc);

        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionRow.setOpaque(false);

        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> ((CardLayout) containerPanel.getLayout()).show(containerPanel, "EdirHome"));

        JButton btnSave = new JButton("Save Registry");
        btnSave.addActionListener(e -> {
            try {
                String name = txtName.getText().trim();
                String contribText = txtContribution.getText().trim();

                if (name.isEmpty() || name.equals("  Enter username")) {
                    JOptionPane.showMessageDialog(this, "Group Name cannot be empty!", "Input Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                double contrib = Double.parseDouble(contribText);
                if (contrib < 0) {
                    JOptionPane.showMessageDialog(this, "Contribution must be a positive number.", "Input Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // 1. Write straight to MySQL database via Service layer
                edirService.createEdirGroup(name, contrib);

                // 2. Clear out form inputs for future additions
                txtName.setText("");
                txtContribution.setText("");

                // 3. ⭐ CRITICAL: Force the main grid panel to pull fresh data from the database right now!
                homePanel.loadGroups();

                JOptionPane.showMessageDialog(this, "Edir Group Configured Successfully!");

                // 4. Route back home to view the updated table layout
                ((CardLayout) containerPanel.getLayout()).show(containerPanel, "EdirHome");

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid numeric value for the contribution fee.", "Format Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Database error: Unable to save group. Check your server logs.", "Persistence Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        actionRow.add(btnCancel);
        actionRow.add(btnSave);
        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2;
        add(actionRow, gbc);
    }
}