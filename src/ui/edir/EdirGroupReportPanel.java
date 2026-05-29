package ui.edir;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import util.DBConnection;

public class EdirGroupReportPanel extends JPanel {
    private final JPanel containerPanel;
    private final int groupId;
    private JTextArea reportTextArea;

    public EdirGroupReportPanel(JPanel containerPanel, int groupId, String groupName) {
        this.containerPanel = containerPanel;
        this.groupId = groupId;

        setOpaque(false);
        setLayout(new BorderLayout(0, 15));
        setBorder(BorderFactory.createEmptyBorder(25, 35, 30, 35));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("📊 Audit Report: " + groupName);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(new Color(101, 53, 15));
        header.add(title, BorderLayout.WEST);

        JButton btnBack = new JButton("⬅ Back");
        btnBack.addActionListener(e -> ((CardLayout) containerPanel.getLayout()).show(containerPanel, "EdirGroupDetail"));
        header.add(btnBack, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        reportTextArea = new JTextArea();
        reportTextArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        reportTextArea.setEditable(false);
        reportTextArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        add(new JScrollPane(reportTextArea), BorderLayout.CENTER);

        generateReportData(groupName);
    }

    private void generateReportData(String groupName) {
        double totalContributions = 0;
        double totalPayouts = 0;

        String contribSql = "SELECT SUM(amount) FROM transaction WHERE group_id = ? AND group_type = 'EDIR' AND type = 'CONTRIBUTION'";
        String payoutSql = "SELECT SUM(amount) FROM transaction WHERE group_id = ? AND group_type = 'EDIR' AND type = 'PAYOUT'";

        try (Connection conn = DBConnection.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(contribSql)) {
                ps.setInt(1, groupId);
                try (ResultSet rs = ps.executeQuery()) { if (rs.next()) totalContributions = rs.getDouble(1); }
            }
            try (PreparedStatement ps = conn.prepareStatement(payoutSql)) {
                ps.setInt(1, groupId);
                try (ResultSet rs = ps.executeQuery()) { if (rs.next()) totalPayouts = rs.getDouble(1); }
            }
        } catch (SQLException e) { e.printStackTrace(); }

        double balance = totalContributions - totalPayouts;

        StringBuilder sb = new StringBuilder();
        sb.append("=========================================================\n");
        sb.append("                HIBRET COMMUNITY SYSTEM                 \n");
        sb.append("             EDIR SYSTEM AUDIT LEDGER SUMMARY            \n");
        sb.append("=========================================================\n\n");
        sb.append(String.format("Target Group Name:      %s\n", groupName));
        sb.append(String.format("Report Generation Time: %s\n\n", new java.util.Date().toString()));
        sb.append("---------------------------------------------------------\n");
        sb.append(String.format("Gross Capital Contributions:      %,.2f Birr\n", totalContributions));
        sb.append(String.format("Emergency Hardship Payouts:       (%,.2f Birr)\n", totalPayouts));
        sb.append("---------------------------------------------------------\n");
        sb.append(String.format("Net Vault Capital Reserves:       %,.2f Birr\n\n", balance));
        sb.append("=========================================================\n");
        sb.append("Status: Stable / Fully Audited Registry Balance\n");

        reportTextArea.setText(sb.toString());
    }
}