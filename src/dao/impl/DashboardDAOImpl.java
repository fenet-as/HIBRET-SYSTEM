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

        // UPDATED: Re-architected to fetch total distinct member headcount across ALL group types (EDIR & EQUB)
        String sql = "SELECT " +
                "  (SELECT COUNT(*) FROM groups WHERE created_by = ? AND type = 'EDIR') as edir_count, " +
                "  (SELECT COUNT(*) FROM groups WHERE created_by = ? AND type = 'EQUB') as equb_count, " +

                // 👥 COMBINED: Total unique members across both Edir and Equb groups managed by this user
                "  (SELECT COUNT(DISTINCT gm.member_id) FROM group_members gm " +
                "   JOIN groups g ON gm.group_id = g.id " +
                "   WHERE g.created_by = ? AND g.type IN ('EDIR', 'EQUB')) as total_member_count, " +

                // 🤝 EDIR: Permanent Remaining Ledger Balance (Contributions Pool minus absolute Payouts)
                "  (COALESCE((SELECT SUM(t.amount) FROM transactions t " +
                "             JOIN groups g ON t.group_id = g.id " +
                "             WHERE g.created_by = ? AND g.type = 'EDIR' AND UPPER(t.type) = 'CONTRIBUTION'), 0) - " +
                "   COALESCE((SELECT SUM(t.amount) FROM transactions t " +
                "             JOIN groups g ON t.group_id = g.id " +
                "             WHERE g.created_by = ? AND g.type = 'EDIR' AND UPPER(t.type) = 'PAYOUT'), 0)) as edir_remaining_fund, " +

                // 🔄 EQUB: Current Closed-Loop Vault Capital Available (Total Incoming Money minus Paid Out Money)
                "  (COALESCE((SELECT SUM(t.amount) FROM transactions t " +
                "             JOIN groups g ON t.group_id = g.id " +
                "             WHERE g.created_by = ? AND g.type = 'EQUB' AND UPPER(t.type) IN ('CONTRIBUTION', 'PAYMENT')), 0) - " +
                "   COALESCE((SELECT SUM(t.amount) FROM transactions t " +
                "             JOIN groups g ON t.group_id = g.id " +
                "             WHERE g.created_by = ? AND g.type = 'EQUB' AND UPPER(t.type) = 'PAYOUT'), 0)) as equb_vault_capital";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Bind parameters to the query positions
            ps.setInt(1, userId); // edir_count
            ps.setInt(2, userId); // equb_count
            ps.setInt(3, userId); // total_member_count (Combined EDIR & EQUB)

            ps.setInt(4, userId); // edir contributions
            ps.setInt(5, userId); // edir payouts deduction

            ps.setInt(6, userId); // equb incoming collections (payments/contributions)
            ps.setInt(7, userId); // equb outgoing distributions (payouts)

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    metrics.setTotalEdirGroups(rs.getInt("edir_count"));
                    metrics.setTotalEqubCircles(rs.getInt("equb_count"));

                    // Remapped to use the updated combined data column
                    // Note: If your DashboardMetrics model still uses setTotalEdirMembers, consider renaming it
                    // in that class to something like setTotalMembers() to match this new logic.
                    metrics.setTotalEdirMembers(rs.getInt("total_member_count"));

                    // Map values cleanly to your dashboard metric objects
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