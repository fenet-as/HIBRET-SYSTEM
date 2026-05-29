package dao;

import util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EdirDAOImpl implements EdirDAO {

    public EdirDAOImpl() {
        // Core initialization interacting with your live PostgreSQL instance
    }

    @Override
    public List<Map<String, String>> getAllGroups() {
        List<Map<String, String>> list = new ArrayList<>();
        // Fixed to reference contribution_amount column
        String sql = "SELECT g.id, g.name, eg.contribution_amount AS group_fee, " +
                "(SELECT COUNT(DISTINCT t.member_id) FROM transactions t WHERE t.group_id = g.id) AS member_count, " +
                "COALESCE((SELECT SUM(amount) FROM transactions WHERE group_id = g.id AND type = 'CONTRIBUTION'), 0) - " +
                "COALESCE((SELECT SUM(amount) FROM transactions WHERE group_id = g.id AND type = 'PAYOUT'), 0) AS fund_balance " +
                "FROM groups g " +
                "JOIN edir_groups eg ON g.name = eg.name " +
                "WHERE g.type = 'EDIR'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Map<String, String> map = new HashMap<>();
                map.put("id", String.valueOf(rs.getInt("id")));
                map.put("name", rs.getString("name"));
                map.put("monthly_fee", String.valueOf(rs.getDouble("group_fee")));
                map.put("fund_balance", String.valueOf(rs.getDouble("fund_balance")));
                map.put("member_count", String.valueOf(rs.getInt("member_count")));
                list.add(map);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean createGroup(String groupName, double monthlyFee, double initialPool, String rules) {
        String sqlGroup = "INSERT INTO groups (name, type, created_by) VALUES (?, 'EDIR', null) RETURNING id";
        String sqlEdirSettings = "INSERT INTO edir_groups (name, contribution_amount) VALUES (?, ?)";
        String sqlInitialTransaction = "INSERT INTO transactions (group_id, group_type, amount, type, description) VALUES (?, 'EDIR', ?, 'CONTRIBUTION', 'Initial reserves deposit pool')";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false); // Begin ACID Transaction Block

            int generatedGroupId = -1;
            try (PreparedStatement psGroup = conn.prepareStatement(sqlGroup)) {
                psGroup.setString(1, groupName);
                try (ResultSet rs = psGroup.executeQuery()) {
                    if (rs.next()) {
                        generatedGroupId = rs.getInt(1);
                    }
                }
            }

            if (generatedGroupId == -1) {
                conn.rollback();
                return false;
            }

            try (PreparedStatement psEdir = conn.prepareStatement(sqlEdirSettings)) {
                psEdir.setString(1, groupName);
                psEdir.setDouble(2, monthlyFee);
                psEdir.executeUpdate();
            }

            if (initialPool > 0) {
                try (PreparedStatement psTx = conn.prepareStatement(sqlInitialTransaction)) {
                    psTx.setInt(1, generatedGroupId);
                    psTx.setDouble(2, initialPool);
                    psTx.executeUpdate();
                }
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteGroup(String groupName) {
        String sqlGetId = "SELECT id FROM groups WHERE name = ? AND type = 'EDIR'";
        String sqlDelTx = "DELETE FROM transactions WHERE group_id = ?";
        String sqlDelEdir = "DELETE FROM edir_groups WHERE name = ?";
        String sqlDelGroup = "DELETE FROM groups WHERE id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            int groupId = -1;

            try (PreparedStatement psId = conn.prepareStatement(sqlGetId)) {
                psId.setString(1, groupName);
                try (ResultSet rs = psId.executeQuery()) {
                    if (rs.next()) groupId = rs.getInt("id");
                }
            }

            if (groupId != -1) {
                try (PreparedStatement psDelTx = conn.prepareStatement(sqlDelTx)) {
                    psDelTx.setInt(1, groupId);
                    psDelTx.executeUpdate();
                }
                try (PreparedStatement psDelGroup = conn.prepareStatement(sqlDelGroup)) {
                    psDelGroup.setInt(1, groupId);
                    psDelGroup.executeUpdate();
                }
            }

            try (PreparedStatement psDelEdir = conn.prepareStatement(sqlDelEdir)) {
                psDelEdir.setString(1, groupName);
                psDelEdir.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Map<String, String> getGroupDetails(String groupName) {
        Map<String, String> map = new HashMap<>();
        // Fully updated to count active 'PENDING_CLAIM' configurations dynamically
        String sql = "SELECT g.id, " +
                "COALESCE((SELECT SUM(amount) FROM transactions WHERE group_id = g.id AND type = 'CONTRIBUTION'), 0) - " +
                "COALESCE((SELECT SUM(amount) FROM transactions WHERE group_id = g.id AND type = 'PAYOUT'), 0) as fund_balance, " +
                "(SELECT COUNT(DISTINCT member_id) FROM transactions WHERE group_id = g.id) as total_members, " +
                "(SELECT COUNT(*) FROM transactions WHERE group_id = g.id AND type = 'PENDING_CLAIM') as active_cases " +
                "FROM groups g WHERE g.name = ? AND g.type = 'EDIR'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, groupName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    map.put("id", String.valueOf(rs.getInt("id")));
                    map.put("fund_balance", String.format("%.2f", rs.getDouble("fund_balance")));
                    map.put("total_members", String.valueOf(rs.getInt("total_members")));
                    map.put("active_cases", String.valueOf(rs.getInt("active_cases")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public List<Map<String, String>> getMembersByGroup(String groupName) {
        List<Map<String, String>> list = new ArrayList<>();
        String sql = "SELECT DISTINCT m.id, m.full_name, m.phone " +
                "FROM members m " +
                "JOIN transactions t ON m.id = t.member_id " +
                "JOIN groups g ON t.group_id = g.id " +
                "WHERE g.name = ? AND g.type = 'EDIR'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, groupName);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> map = new HashMap<>();
                    map.put("id", String.valueOf(rs.getInt("id")));
                    map.put("full_name", rs.getString("full_name"));
                    map.put("phone", rs.getString("phone"));
                    map.put("status", "Active");
                    list.add(map);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean addMemberToGroup(String groupName, String fullName, String phone) {
        String sqlFindGroup = "SELECT id FROM groups WHERE name = ? AND type = 'EDIR'";
        String sqlInsertMember = "INSERT INTO members (full_name, phone, user_id) VALUES (?, ?, null) RETURNING id";
        String sqlLinkTx = "INSERT INTO transactions (member_id, group_id, group_type, amount, type, description) VALUES (?, ?, 'EDIR', 0, 'REGISTRATION', 'Member joined group profile entry')";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            int groupId = -1;
            try (PreparedStatement psG = conn.prepareStatement(sqlFindGroup)) {
                psG.setString(1, groupName);
                try (ResultSet rs = psG.executeQuery()) {
                    if (rs.next()) groupId = rs.getInt("id");
                }
            }

            if (groupId == -1) {
                conn.rollback();
                return false;
            }

            int memberId = -1;
            try (PreparedStatement psM = conn.prepareStatement(sqlInsertMember)) {
                psM.setString(1, fullName);
                psM.setString(2, phone);
                try (ResultSet rs = psM.executeQuery()) {
                    if (rs.next()) memberId = rs.getInt(1);
                }
            }

            try (PreparedStatement psTx = conn.prepareStatement(sqlLinkTx)) {
                psTx.setInt(1, memberId);
                psTx.setInt(2, groupId);
                psTx.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean recordContribution(String groupName, String memberName, String month, double amount, String receiptNo) {
        String sqlFindGroup = "SELECT id FROM groups WHERE name = ? AND type = 'EDIR'";
        String sqlFindMember = "SELECT id FROM members WHERE full_name = ? LIMIT 1";
        String sqlInsertTx = "INSERT INTO transactions (member_id, group_id, group_type, amount, type, description) VALUES (?, ?, 'EDIR', ?, 'CONTRIBUTION', ?)";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            int groupId = -1, memberId = -1;

            try (PreparedStatement psG = conn.prepareStatement(sqlFindGroup)) {
                psG.setString(1, groupName);
                try (ResultSet rs = psG.executeQuery()) { if (rs.next()) groupId = rs.getInt("id"); }
            }
            try (PreparedStatement psM = conn.prepareStatement(sqlFindMember)) {
                psM.setString(1, memberName);
                try (ResultSet rs = psM.executeQuery()) { if (rs.next()) memberId = rs.getInt("id"); }
            }

            if (groupId == -1 || memberId == -1) { conn.rollback(); return false; }

            try (PreparedStatement psTx = conn.prepareStatement(sqlInsertTx)) {
                psTx.setInt(1, memberId);
                psTx.setInt(2, groupId);
                psTx.setDouble(3, amount);
                psTx.setString(4, "Month: " + month + " | Receipt: " + receiptNo);
                psTx.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Map<String, String>> getRecentContributions(String groupName) {
        List<Map<String, String>> list = new ArrayList<>();
        String sql = "SELECT m.full_name, t.amount, t.description " +
                "FROM transactions t " +
                "JOIN members m ON t.member_id = m.id " +
                "JOIN groups g ON t.group_id = g.id " +
                "WHERE g.name = ? AND t.type = 'CONTRIBUTION' " +
                "ORDER BY t.id DESC LIMIT 5";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, groupName);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> map = new HashMap<>();
                    map.put("member_name", rs.getString("full_name"));
                    map.put("amount", String.valueOf(rs.getDouble("amount")));
                    map.put("month", rs.getString("description"));
                    list.add(map);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean registerEmergencyCase(String groupName, String memberName, String type, double amount, String description) {
        String sqlFindGroup = "SELECT id FROM groups WHERE name = ? AND type = 'EDIR'";
        String sqlFindMember = "SELECT id FROM members WHERE full_name = ? LIMIT 1";
        String sqlInsertTx = "INSERT INTO transactions (member_id, group_id, group_type, amount, type, description) VALUES (?, ?, 'EDIR', ?, 'PENDING_CLAIM', ?)";

        try (Connection conn = DBConnection.getConnection()) {
            int groupId = -1, memberId = -1;
            try (PreparedStatement psG = conn.prepareStatement(sqlFindGroup)) {
                psG.setString(1, groupName);
                try (ResultSet rs = psG.executeQuery()) { if (rs.next()) groupId = rs.getInt("id"); }
            }
            try (PreparedStatement psM = conn.prepareStatement(sqlFindMember)) {
                psM.setString(1, memberName);
                try (ResultSet rs = psM.executeQuery()) { if (rs.next()) memberId = rs.getInt("id"); }
            }
            if (groupId == -1 || memberId == -1) return false;

            try (PreparedStatement psTx = conn.prepareStatement(sqlInsertTx)) {
                psTx.setInt(1, memberId);
                psTx.setInt(2, groupId);
                psTx.setDouble(3, amount);
                psTx.setString(4, "Type: " + type + " | Details: " + description);
                return psTx.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // New Data Acquisition API Hook specifically mapping pending cases to the distribution layout
    public List<Map<String, String>> getPendingClaimsByGroup(String groupName) {
        List<Map<String, String>> list = new ArrayList<>();
        String sql = "SELECT t.id, m.full_name, t.amount, t.description " +
                "FROM transactions t " +
                "JOIN groups g ON t.group_id = g.id " +
                "JOIN members m ON t.member_id = m.id " +
                "WHERE g.name = ? AND g.type = 'EDIR' AND t.type = 'PENDING_CLAIM' " +
                "ORDER BY t.id ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, groupName);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> map = new HashMap<>();
                    map.put("tx_id", String.valueOf(rs.getInt("id")));
                    map.put("member_name", rs.getString("full_name"));
                    map.put("amount", String.valueOf(rs.getDouble("amount")));
                    map.put("description", rs.getString("description"));
                    list.add(map);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean authorizePayout(String groupName, String caseTxId, double amount, String approvedBy, String notes) {
        String sqlFindGroup = "SELECT id FROM groups WHERE name = ? AND type = 'EDIR'";
        String sqlUpdateClaim = "UPDATE transactions SET type = 'APPROVED_CLAIM' WHERE id = ?";
        String sqlInsertPayout = "INSERT INTO transactions (member_id, group_id, group_type, amount, type, description) VALUES " +
                "((SELECT member_id FROM transactions WHERE id = ?), ?, 'EDIR', ?, 'PAYOUT', ?)";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false); // Begin multi-step structural commit transaction
            int groupId = -1;
            try (PreparedStatement psG = conn.prepareStatement(sqlFindGroup)) {
                psG.setString(1, groupName);
                try (ResultSet rs = psG.executeQuery()) { if (rs.next()) groupId = rs.getInt("id"); }
            }
            if (groupId == -1) return false;

            int claimId = Integer.parseInt(caseTxId);

            // 1. Move old state from pending claim status out of the queue
            try (PreparedStatement psUp = conn.prepareStatement(sqlUpdateClaim)) {
                psUp.setInt(1, claimId);
                psUp.executeUpdate();
            }

            // 2. Insert absolute deduction debit entry line row
            try (PreparedStatement psTx = conn.prepareStatement(sqlInsertPayout)) {
                psTx.setInt(1, claimId);
                psTx.setInt(2, groupId);
                psTx.setDouble(3, amount);
                psTx.setString(4, "Approved By: " + approvedBy + " | Notes: " + notes);
                psTx.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
    }
}