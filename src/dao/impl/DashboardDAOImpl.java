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

        // ✅ CLEANED SQL: Dropped the pending claims sub-query sequence entirely
        String sql = "SELECT " +
                "  (SELECT COUNT(*) FROM groups WHERE created_by = ? AND type = 'EDIR') as edir_count, " +
                "  (SELECT COUNT(*) FROM groups WHERE created_by = ? AND type = 'EQUB') as equb_count, " +
                "  (SELECT COUNT(*) FROM group_members gm " +
                "   JOIN groups g ON gm.group_id = g.id " +
                "   WHERE g.created_by = ? AND g.type = 'EDIR') as edir_member_count, " +
                "  (SELECT COALESCE(SUM(amount), 0) FROM transactions t " +
                "   JOIN groups g ON t.group_id = g.id " +
                "   WHERE g.created_by = ? AND g.type = 'EDIR' AND t.type = 'CONTRIBUTION') as edir_funds, " +
                "  (SELECT COALESCE(SUM(amount), 0) FROM transactions t " +
                "   JOIN groups g ON t.group_id = g.id " +
                "   WHERE g.created_by = ? AND g.type = 'EQUB' AND t.type = 'CONTRIBUTION') as equb_funds";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // ✅ Only 5 parameters needed now instead of 6
            ps.setInt(1, userId);
            ps.setInt(2, userId);
            ps.setInt(3, userId);
            ps.setInt(4, userId);
            ps.setInt(5, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    metrics.setTotalEdirGroups(rs.getInt("edir_count"));
                    metrics.setTotalEqubCircles(rs.getInt("equb_count"));
                    metrics.setTotalEdirMembers(rs.getInt("edir_member_count"));
                    metrics.setTotalEdirVaultBalance(rs.getDouble("edir_funds"));
                    metrics.setTotalEqubVaultBalance(rs.getDouble("equb_funds"));
                }
            }
        } catch (Exception e) {
            System.err.println("CRITICAL ERROR: Failed to aggregate dashboard metrics summary for user: " + userId);
            e.printStackTrace();
        }

        return metrics;
    }
}