package ui.edir;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import util.DBConnection;

public class EmergencyApprovalPanel extends JPanel {
    private final JPanel containerPanel;
    private JTable table;
    private DefaultTableModel model;

    public EmergencyApprovalPanel(JPanel containerPanel) {
        this.containerPanel = containerPanel;
        setOpaque(false);
        setLayout(new BorderLayout(0, 15));
        setBorder(BorderFactory.createEmptyBorder(25, 35, 30, 35));

        JLabel title = new JLabel("⚖️ Emergency Claims Validation Pipeline");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(new Color(101, 53, 15));
        add(title, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"Claim ID", "Beneficiary", "Type", "Requested Fund", "Status"}, 0);
        table = new JTable(model);
        table.setRowHeight(32);
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadPendingClaims();
    }

    private void loadPendingClaims() {
        model.setRowCount(0);
        String sql = "SELECT e.id, m.full_name, e.emergency_type, e.amount_needed, e.status " +
                "FROM edir_emergencies e JOIN members m ON e.member_id = m.id WHERE e.status = 'PENDING'";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"), rs.getString("full_name"),
                        rs.getString("emergency_type"), rs.getDouble("amount_needed"), rs.getString("status")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }
}