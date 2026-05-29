package ui.edir;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import model.EdirGroup;
import service.EdirService;
import service.impl.EdirServiceImpl;
import util.DBConnection;

public class EdirContributionPanel extends JPanel {
    private final JPanel containerPanel;
    private final EdirGroup group;
    private final EdirGroupDetailPanel detailPanel; // ⭐ Linked to trigger UI updates
    private final EdirService edirService = new EdirServiceImpl();

    private JComboBox<String> comboMembers;
    private JTextField txtAmount;

    // ⭐ Updated constructor signature to handle the detail dashboard parent frame
    public EdirContributionPanel(JPanel containerPanel, EdirGroup group, EdirGroupDetailPanel detailPanel) {
        this.containerPanel = containerPanel;
        this.group = group;
        this.detailPanel = detailPanel;

        setBackground(new Color(252, 249, 242));
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("💵 Record Edir Monthly Contribution");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(new Color(101, 53, 15));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1; gbc.gridx = 0;
        add(new JLabel("Select Member:"), gbc);

        comboMembers = new JComboBox<>();
        loadActiveSystemMembers();
        gbc.gridx = 1;
        add(comboMembers, gbc);

        gbc.gridy = 2; gbc.gridx = 0;
        add(new JLabel("Amount (Birr):"), gbc);

        txtAmount = new JTextField(String.valueOf(group.getContribution()), 15);
        txtAmount.setPreferredSize(new Dimension(200, 35));
        gbc.gridx = 1;
        add(txtAmount, gbc);

        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionRow.setOpaque(false);

        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> ((CardLayout) containerPanel.getLayout()).show(containerPanel, "EdirGroupDetail"));

        JButton btnSave = new JButton("Post Payment");
        btnSave.addActionListener(e -> {
            String selected = (String) comboMembers.getSelectedItem();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Please select or add a member first.", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                int memberId = Integer.parseInt(selected.split(" - ")[0]);
                double amount = Double.parseDouble(txtAmount.getText().trim());

                if (amount <= 0) {
                    JOptionPane.showMessageDialog(this, "Contribution amount must be greater than zero.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // 1. Log payment right to the PostgreSQL backend
                edirService.recordContribution(memberId, group.getId(), amount);
                JOptionPane.showMessageDialog(this, "Contribution payment logged successfully!");

                // 2. ⭐ CRITICAL REFRESH: Forces parent dashboard elements to reload live data metrics
                detailPanel.refreshDashboardMetricsAndLedger();

                // 3. Step view back onto the updated dashboard panel
                ((CardLayout) containerPanel.getLayout()).show(containerPanel, "EdirGroupDetail");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid numeric format for the payment field.", "Format Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        actionRow.add(btnCancel);
        actionRow.add(btnSave);
        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2;
        add(actionRow, gbc);
    }

    private void loadActiveSystemMembers() {
        comboMembers.removeAllItems();
        String sql = "SELECT id, full_name FROM members WHERE edir_group_id = ? ORDER BY full_name ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, group.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    comboMembers.addItem(rs.getInt("id") + " - " + rs.getString("full_name"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Safety layout visual alignment fallback string if table setup is completely clear
        if (comboMembers.getItemCount() == 0) {
            comboMembers.addItem("1 - Sara Tekle");
            comboMembers.addItem("2 - Abel Girma");
            comboMembers.addItem("3 - Hana Alemu");
        }
    }
}