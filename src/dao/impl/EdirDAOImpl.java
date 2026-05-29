package dao.impl;

import dao.EdirDAO;
import model.EdirGroup;
import model.EmergencyCase;
import model.Transaction;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EdirDAOImpl implements EdirDAO {

    @Override
    public void createEdirGroup(String name, double contribution) {
        // Changing the column name to contribution_amount here 🎯
        String sql = "INSERT INTO edir_groups (name, contribution_amount, created_at) VALUES (?, ?, CURRENT_TIMESTAMP)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setDouble(2, contribution);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<EdirGroup> getAllEdirGroups() {
        List<EdirGroup> groups = new ArrayList<>();
        // Changing the column name to contribution_amount here as well 🎯
        String sql = "SELECT id, name, contribution_amount, created_at FROM edir_groups ORDER BY id DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                EdirGroup group = new EdirGroup();
                group.setId(rs.getInt("id"));
                group.setName(rs.getString("name"));
                group.setContribution(rs.getDouble("contribution_amount")); // Read correctly
                group.setCreatedAt(rs.getTimestamp("created_at"));
                groups.add(group);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return groups;
    }

    @Override
    public void deleteEdirGroup(int id) {
        String sql = "DELETE FROM edir_groups WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object[] getEdirGroupMetrics(int edirGroupId) {
        String sql = "SELECT " +
                "  (SELECT COUNT(*) FROM members) AS total_m, " +
                "  COALESCE((SELECT SUM(amount) FROM transaction WHERE group_id = ? AND group_type = 'EDIR' AND type = 'CONTRIBUTION'), 0) - " +
                "  COALESCE((SELECT SUM(amount) FROM transaction WHERE group_id = ? AND group_type = 'EDIR' AND type = 'PAYOUT'), 0) AS balance, " +
                "  (SELECT COUNT(*) FROM edir_emergencies WHERE group_id = ? AND status = 'PENDING') AS active_cases";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, edirGroupId);
            stmt.setInt(2, edirGroupId);
            stmt.setInt(3, edirGroupId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Object[]{
                            rs.getInt("total_m"),
                            rs.getDouble("balance"),
                            rs.getInt("active_cases")
                    };
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new Object[]{0, 0.0, 0};
    }

    @Override
    public void recordEdirContribution(int memberId, int groupId, double amount) {
        String sql = "INSERT INTO transaction (member_id, group_id, group_type, amount, type, date, description) " +
                "VALUES (?, ?, 'EDIR', ?, 'CONTRIBUTION', CURRENT_TIMESTAMP, 'Monthly Edir Fee Contribution Payment')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, memberId);
            stmt.setInt(2, groupId);
            stmt.setDouble(3, amount);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Transaction> getRecentContributionsForGroup(int groupId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT id, member_id, group_id, group_type, amount, type, date, description " +
                "FROM transaction WHERE group_id = ? AND group_type = 'EDIR' ORDER BY date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, groupId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Transaction t = new Transaction();
                    t.setId(rs.getInt("id"));
                    t.setMemberId(rs.getInt("member_id"));
                    t.setGroupId(rs.getInt("group_id"));
                    t.setGroupType(rs.getString("group_type"));
                    t.setAmount(rs.getDouble("amount"));
                    t.setType(rs.getString("type"));
                    t.setDate(rs.getTimestamp("date"));
                    t.setDescription(rs.getString("description"));
                    transactions.add(t);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    @Override
    public void registerEmergencyCase(int memberId, int groupId, String type, double amount, String description) {
        String sql = "INSERT INTO edir_emergencies (member_id, group_id, emergency_type, amount_needed, description, status, date) " +
                "VALUES (?, ?, ?, ?, ?, 'PENDING', CURRENT_TIMESTAMP)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, memberId);
            stmt.setInt(2, groupId);
            stmt.setString(3, type);
            stmt.setDouble(4, amount);
            stmt.setString(5, description);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<EmergencyCase> getPendingPayoutCasesForGroup(int groupId) {
        List<EmergencyCase> list = new ArrayList<>();
        String sql = "SELECT e.*, m.full_name FROM edir_emergencies e " +
                "JOIN members m ON e.member_id = m.id " +
                "WHERE e.group_id = ? AND e.status = 'PENDING' ORDER BY e.date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, groupId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    EmergencyCase ec = new EmergencyCase();
                    ec.setId(rs.getInt("id"));
                    ec.setMemberId(rs.getInt("member_id"));
                    ec.setMemberName(rs.getString("full_name"));
                    ec.setGroupId(rs.getInt("group_id"));
                    ec.setEmergencyType(rs.getString("emergency_type"));
                    ec.setAmountNeeded(rs.getDouble("amount_needed"));
                    ec.setDescription(rs.getString("description"));
                    ec.setStatus(rs.getString("status"));
                    ec.setDate(rs.getTimestamp("date"));
                    list.add(ec);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void disburseEmergencyFunds(EmergencyCase ec) {
        String insertTxSql = "INSERT INTO transaction (member_id, group_id, group_type, amount, type, date, description) " +
                "VALUES (?, ?, 'EDIR', ?, 'PAYOUT', CURRENT_TIMESTAMP, ?)";
        String updateEmergencySql = "UPDATE edir_emergencies SET status = 'DISBURSED' WHERE id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement stmt1 = conn.prepareStatement(insertTxSql)) {
                    stmt1.setInt(1, ec.getMemberId());
                    stmt1.setInt(2, ec.getGroupId());
                    stmt1.setDouble(3, ec.getAmountNeeded());
                    stmt1.setString(4, "Emergency assistance payout for: " + ec.getEmergencyType());
                    stmt1.executeUpdate();
                }

                try (PreparedStatement stmt2 = conn.prepareStatement(updateEmergencySql)) {
                    stmt2.setInt(1, ec.getId());
                    stmt2.executeUpdate();
                }

                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}