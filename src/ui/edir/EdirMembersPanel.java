package ui.edir;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import util.DBConnection;

public class EdirMembersPanel extends JPanel {
    private final JPanel containerPanel;
    private final int edirGroupId;
    private JTable table;
    private DefaultTableModel model;

    public EdirMembersPanel(JPanel containerPanel, int edirGroupId) {
        this.containerPanel = containerPanel;
        this.edirGroupId = edirGroupId;

        setOpaque(false);
        setLayout(new BorderLayout(0, 15));
        setBorder(BorderFactory.createEmptyBorder(25, 35, 30, 35));

        // Header Panel
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("👤 Edir General Members Registry");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(new Color(101, 53, 15));
        header.add(title, BorderLayout.WEST);

        JButton btnBack = new JButton("⬅ Back");
        btnBack.addActionListener(e -> ((CardLayout) containerPanel.getLayout()).show(containerPanel, "EdirGroupDetail"));
        header.add(btnBack, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Members Table
        model = new DefaultTableModel(new String[]{"Member ID", "Full Name", "Phone Number"}, 0);
        table = new JTable(model);
        table.setRowHeight(30);
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadMembersFromDatabase();
    }

    private void loadMembersFromDatabase() {
        model.setRowCount(0);
        String sql = "SELECT id, full_name, phone FROM members ORDER BY full_name ASC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                model.addRow(new Object[]{rs.getInt("id"), rs.getString("full_name"), rs.getString("phone")});
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }
}