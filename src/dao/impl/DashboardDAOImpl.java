package dao.impl;

import dao.DashboardDAO;
import model.DashboardMetrics;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DashboardDAOImpl implements DashboardDAO {

    @Override
    public DashboardMetrics getSystemSummary(int userId) {
        DashboardMetrics metrics = new DashboardMetrics();

        String sql = "SELECT " +
                "  (SELECT COUNT(*) FROM groups WHERE created_by = ? AND type = 'EDIR') as edir_count, " +
                "  (SELECT COUNT(*) FROM groups WHERE created_by = ? AND type = 'EQUB') as equb_count, " +
                "  (SELECT COUNT(*) FROM group_members gm " +
                "   JOIN groups g ON gm.group_id = g.id " +
                "   WHERE g.created_by = ? AND g.type = 'EDIR') as edir_member_count, " +

                // 🤝 EDIR: Permanent Remaining Ledger Balance (Contributions Pool minus absolute Payouts)
                "  (COALESCE((SELECT SUM(t.amount) FROM transactions t " +
                "             JOIN groups g ON t.group_id = g.id " +
                "             WHERE g.created_by = ? AND g.type = 'EDIR' AND t.type = 'CONTRIBUTION'), 0) - " +
                "   COALESCE((SELECT SUM(t.amount) FROM transactions t " +
                "             JOIN groups g ON t.group_id = g.id " +
                "             WHERE g.created_by = ? AND g.type = 'EDIR' AND t.type = 'PAYOUT'), 0)) as edir_remaining_fund, " +

                // 🔄 EQUB: Current Vault Capital Available (Money sitting in active rounds not yet claimed/paid out)
                "  COALESCE(( " +
                "    SELECT SUM(t.amount) FROM transactions t " +
                "    JOIN groups g ON t.group_id = g.id " +
                "    WHERE g.created_by = ? " +
                "      AND g.type = 'EQUB' " +
                "      AND t.type = 'CONTRIBUTION' " +
                "      AND t.description NOT LIKE '%Settled%' " +
                "      AND t.description NOT LIKE '%Paid Out%' " +
                "  ), 0) as equb_vault_capital";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Bind the parameters to the query
            ps.setInt(1, userId); // edir_count
            ps.setInt(2, userId); // equb_count
            ps.setInt(3, userId); // edir_member_count

            ps.setInt(4, userId); // edir contributions
            ps.setInt(5, userId); // edir payouts deduction

            ps.setInt(6, userId); // equb unpayout vault capital allocation

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    metrics.setTotalEdirGroups(rs.getInt("edir_count"));
                    metrics.setTotalEqubCircles(rs.getInt("equb_count"));
                    metrics.setTotalEdirMembers(rs.getInt("edir_member_count"));

                    // Pass the metrics straight into your Dashboard model layer
                    metrics.setTotalEdirVaultBalance(rs.getDouble("edir_remaining_fund"));
                    metrics.setTotalEqubVaultBalance(rs.getDouble("equb_vault_capital"));
                }
            }
        } catch (Exception e) {
            System.err.println("CRITICAL ERROR: Failed to aggregate dashboard metrics summary for user: " + userId);
            e.printStackTrace();
        }

        return metrics;
    }
}