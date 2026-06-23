package dao.impl;

import dao.EdirDAO;
import util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EdirDAOImpl implements EdirDAO {

    public EdirDAOImpl() {
        // Core DB Initialization Hook
    }

    @Override
    public List<Map<String, String>> getEdirGroupsForUser(int userId) {
        List<Map<String, String>> list = new ArrayList<>();
        String sql = "SELECT g.id, g.name, eg.contribution_amount AS group_fee, " +
                "  (SELECT COUNT(*) FROM group_members gm2 WHERE gm2.group_id = g.id) AS member_count, " +
                "  (COALESCE((SELECT SUM(t.amount) FROM transactions t WHERE t.group_id = g.id AND t.type = 'CONTRIBUTION'), 0) - " +
                "   COALESCE((SELECT SUM(t.amount) FROM transactions t WHERE t.group_id = g.id AND t.type = 'PAYOUT'), 0)) AS fund_balance " +
                "FROM groups g " +
                "JOIN edir_groups eg ON g.id = eg.id " +
                "WHERE g.type = 'EDIR' AND g.created_by = ? " +
                "ORDER BY g.name ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> map = new HashMap<>();
                    map.put("id", String.valueOf(rs.getInt("id")));
                    map.put("name", rs.getString("name"));
                    map.put("monthly_fee", String.valueOf(rs.getDouble("group_fee")));
                    map.put("fund_balance", String.valueOf(rs.getDouble("fund_balance")));
                    map.put("member_count", String.valueOf(rs.getInt("member_count")));
                    list.add(map);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Map<String, String>> getAllGroups() {
        List<Map<String, String>> list = new ArrayList<>();
        String sql = "SELECT g.id, g.name, eg.contribution_amount AS group_fee, " +
                "  (SELECT COUNT(*) FROM group_members gm WHERE gm.group_id = g.id) AS member_count, " +
                "  (COALESCE((SELECT SUM(t.amount) FROM transactions t WHERE t.group_id = g.id AND t.type = 'CONTRIBUTION'), 0) - " +
                "   COALESCE((SELECT SUM(t.amount) FROM transactions t WHERE t.group_id = g.id AND t.type = 'PAYOUT'), 0)) AS fund_balance " +
                "FROM groups g " +
                "JOIN edir_groups eg ON g.id = eg.id " +
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
    public boolean createGroup(String groupName, double monthlyFee, double initialPool, String rules, int creatorUserId) {
        String sqlCheckName = "SELECT COUNT(*) FROM groups WHERE UPPER(TRIM(name)) = UPPER(TRIM(?)) AND type = 'EDIR' AND created_by = ?";
        String sqlGroup = "INSERT INTO groups (name, type, created_by, contribution_amount, created_at) VALUES (?, 'EDIR', ?, ?, CURRENT_TIMESTAMP) RETURNING id";
        String sqlEdirSettings = "INSERT INTO edir_groups (id, name, contribution_amount, created_at) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";

        // FIXED UNIFORM MATCHING: Query name using user ID first, clean comparisons, and add safe phone backup checkpoints
        String sqlFetchUserName = "SELECT full_name FROM users WHERE id = ?";
        String sqlFindMemberByName = "SELECT id FROM members WHERE UPPER(TRIM(full_name)) = UPPER(TRIM(?)) LIMIT 1";
        String sqlFindMemberByPhone = "SELECT id FROM members WHERE phone = 'N/A' LIMIT 1";

        String sqlCreateMember = "INSERT INTO members (full_name, phone, status) VALUES (?, 'N/A', 'ACTIVE') RETURNING id";
        String sqlLinkCreator = "INSERT INTO group_members (group_id, member_id) VALUES (?, ?)";
        String sqlInitialTransaction = "INSERT INTO transactions (group_id, member_id, amount, type, description, created_at) VALUES (?, ?, ?, 'CONTRIBUTION', 'Initial reserves deposit pool', CURRENT_TIMESTAMP)";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            // 1. Double check name availability to prevent collisions
            try (PreparedStatement psCheck = conn.prepareStatement(sqlCheckName)) {
                psCheck.setString(1, groupName);
                psCheck.setInt(2, creatorUserId);
                try (ResultSet rsCheck = psCheck.executeQuery()) {
                    if (rsCheck.next() && rsCheck.getInt(1) > 0) {
                        conn.rollback();
                        return false;
                    }
                }
            }

            // 2. Extract Creator's full name from users via User ID
            String creatorName = "Group Administrator";
            try (PreparedStatement psName = conn.prepareStatement(sqlFetchUserName)) {
                psName.setInt(1, creatorUserId);
                try (ResultSet rs = psName.executeQuery()) {
                    if (rs.next() && rs.getString("full_name") != null) {
                        creatorName = rs.getString("full_name");
                    }
                }
            }

            int generatedGroupId = -1;

            // 3. Create core group record
            try (PreparedStatement psGroup = conn.prepareStatement(sqlGroup)) {
                psGroup.setString(1, groupName);
                psGroup.setInt(2, creatorUserId);
                psGroup.setDouble(3, monthlyFee);
                try (ResultSet rs = psGroup.executeQuery()) {
                    if (rs.next()) generatedGroupId = rs.getInt(1);
                }
            }

            if (generatedGroupId == -1) {
                conn.rollback();
                return false;
            }

            // 4. Create Edir specific subtype record
            try (PreparedStatement psEdir = conn.prepareStatement(sqlEdirSettings)) {
                psEdir.setInt(1, generatedGroupId);
                psEdir.setString(2, groupName);
                psEdir.setDouble(3, monthlyFee);
                psEdir.executeUpdate();
            }

            // 5. Query matching member record by full name
            int memberId = -1;
            try (PreparedStatement psFindM = conn.prepareStatement(sqlFindMemberByName)) {
                psFindM.setString(1, creatorName);
                try (ResultSet rs = psFindM.executeQuery()) {
                    if (rs.next()) memberId = rs.getInt("id");
                }
            }

            // Fallback checkpoint: If name matches failed, identify any existing placeholder member using phone 'N/A'
            if (memberId == -1) {
                try (PreparedStatement psFindPhone = conn.prepareStatement(sqlFindMemberByPhone)) {
                    try (ResultSet rs = psFindPhone.executeQuery()) {
                        if (rs.next()) memberId = rs.getInt("id");
                    }
                }
            }

            // 6. Only insert a new member if absolutely no existing record matches
            if (memberId == -1) {
                try (PreparedStatement psNewM = conn.prepareStatement(sqlCreateMember, Statement.RETURN_GENERATED_KEYS)) {
                    psNewM.setString(1, creatorName);
                    psNewM.executeUpdate();
                    try (ResultSet rs = psNewM.getGeneratedKeys()) {
                        if (rs.next()) memberId = rs.getInt(1);
                    }
                }
            }

            // 7. Establish linkage inside junction layout
            try (PreparedStatement psLink = conn.prepareStatement(sqlLinkCreator)) {
                psLink.setInt(1, generatedGroupId);
                psLink.setInt(2, memberId);
                psLink.executeUpdate();
            }

            // 8. Inject initial financial reserves if provided
            if (initialPool > 0) {
                try (PreparedStatement psTx = conn.prepareStatement(sqlInitialTransaction)) {
                    psTx.setInt(1, generatedGroupId);
                    psTx.setInt(2, memberId);
                    psTx.setDouble(3, initialPool);
                    psTx.executeUpdate();
                }
            }

            conn.commit();
            System.out.println("--> EDIR Group created cleanly via Creator User ID: " + creatorUserId);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteGroup(int groupId) {
        String sqlDelTx = "DELETE FROM transactions WHERE group_id = ?";
        String sqlDelMembers = "DELETE FROM group_members WHERE group_id = ?";
        String sqlDelEdir = "DELETE FROM edir_groups WHERE id = ?";
        String sqlDelGroup = "DELETE FROM groups WHERE id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement psTx = conn.prepareStatement(sqlDelTx)) {
                psTx.setInt(1, groupId);
                psTx.executeUpdate();
            }
            try (PreparedStatement psMem = conn.prepareStatement(sqlDelMembers)) {
                psMem.setInt(1, groupId);
                psMem.executeUpdate();
            }
            try (PreparedStatement psEdir = conn.prepareStatement(sqlDelEdir)) {
                psEdir.setInt(1, groupId);
                psEdir.executeUpdate();
            }
            try (PreparedStatement psGrp = conn.prepareStatement(sqlDelGroup)) {
                psGrp.setInt(1, groupId);
                psGrp.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Map<String, String> getGroupDetails(int groupId) {
        Map<String, String> map = new HashMap<>();
        String sql = "SELECT g.id, g.name, " +
                "COALESCE((SELECT SUM(amount) FROM transactions WHERE group_id = g.id AND type = 'CONTRIBUTION'), 0) - " +
                "COALESCE((SELECT SUM(amount) FROM transactions WHERE group_id = g.id AND type = 'PAYOUT'), 0) as fund_balance, " +
                "(SELECT COUNT(*) FROM group_members WHERE group_id = g.id) as total_members, " +
                "(SELECT COUNT(*) FROM emergency_cases WHERE group_id = g.id AND UPPER(TRIM(status)) = 'PENDING') as active_cases " +
                "FROM groups g WHERE g.id = ? AND g.type = 'EDIR'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, groupId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    map.put("id", String.valueOf(rs.getInt("id")));
                    map.put("name", rs.getString("name"));
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
    public List<Map<String, String>> getMembersByGroup(int groupId) {
        List<Map<String, String>> list = new ArrayList<>();
        String sql = "SELECT m.id, m.full_name, m.phone, m.status " +
                "FROM members m " +
                "JOIN group_members gm ON m.id = gm.member_id " +
                "JOIN groups g ON gm.group_id = g.id " +
                "WHERE g.id = ? AND g.type = 'EDIR'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, groupId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> map = new HashMap<>();
                    map.put("id", String.valueOf(rs.getInt("id")));
                    map.put("full_name", rs.getString("full_name"));
                    map.put("phone", rs.getString("phone"));
                    String status = rs.getString("status");
                    map.put("status", status != null ? status : "Active");
                    list.add(map);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean addMemberToGroup(int groupId, String fullName, String phone) {
        // FIXED: Removed non-existent user_id property parameter reference
        String sqlInsertMember = "INSERT INTO members (full_name, phone, status) VALUES (?, ?, 'ACTIVE') RETURNING id";
        String sqlLinkMember = "INSERT INTO group_members (group_id, member_id) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            int memberId = -1;

            try (PreparedStatement psM = conn.prepareStatement(sqlInsertMember)) {
                psM.setString(1, fullName);
                psM.setString(2, phone);
                try (ResultSet rs = psM.executeQuery()) {
                    if (rs.next()) memberId = rs.getInt(1);
                }
            }

            try (PreparedStatement psLink = conn.prepareStatement(sqlLinkMember)) {
                psLink.setInt(1, groupId);
                psLink.setInt(2, memberId);
                psLink.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean recordContribution(int groupId, String memberName, String month, double amount, String receiptNo) {
        String sqlFindMember = "SELECT id FROM members WHERE full_name = ? LIMIT 1";
        // FIXED: Linked insertion payload sequence tracking into schema-defined created_at
        String sqlInsertTx = "INSERT INTO transactions (member_id, group_id, amount, type, description, created_at) VALUES (?, ?, ?, 'CONTRIBUTION', ?, CURRENT_TIMESTAMP)";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            int memberId = -1;

            try (PreparedStatement psM = conn.prepareStatement(sqlFindMember)) {
                psM.setString(1, memberName);
                try (ResultSet rs = psM.executeQuery()) { if (rs.next()) memberId = rs.getInt("id"); }
            }

            if (memberId == -1) { conn.rollback(); return false; }

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
    public List<Map<String, String>> getRecentContributions(int groupId) {
        return getGroupTransactionLedger(groupId);
    }

    @Override
    public List<Map<String, String>> getGroupTransactionLedger(int groupId) {
        List<Map<String, String>> list = new ArrayList<>();
        String sql = "SELECT COALESCE(m.full_name, 'SYSTEM/OFFICER') as party_name, t.amount, t.type, t.description " +
                "FROM transactions t " +
                "LEFT JOIN members m ON t.member_id = m.id " +
                "WHERE t.group_id = ? AND t.type IN ('CONTRIBUTION', 'PAYOUT', 'REGISTRATION') " +
                "ORDER BY t.id DESC LIMIT 15";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, groupId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> map = new HashMap<>();
                    String partyName = rs.getString("party_name");
                    String desc = rs.getString("description");

                    map.put("party_name", partyName);
                    map.put("member_name", partyName);
                    map.put("amount", String.valueOf(rs.getDouble("amount")));
                    map.put("type", rs.getString("type"));
                    map.put("description", desc);
                    map.put("month", desc);
                    list.add(map);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean registerEmergencyCase(int groupId, String memberName, String type, double amount, String description) {
        String sqlFindMember = "SELECT id FROM members WHERE full_name = ? LIMIT 1";
        // FIXED: Swapped title -> emergency_type, requested_amount -> amount_needed
        String sqlInsertCase = "INSERT INTO emergency_cases (group_id, member_id, emergency_type, description, amount_needed, status, created_at) VALUES (?, ?, ?, ?, ?, 'PENDING', CURRENT_TIMESTAMP)";

        try (Connection conn = DBConnection.getConnection()) {
            int memberId = -1;
            try (PreparedStatement psM = conn.prepareStatement(sqlFindMember)) {
                psM.setString(1, memberName);
                try (ResultSet rs = psM.executeQuery()) { if (rs.next()) memberId = rs.getInt("id"); }
            }
            if (memberId == -1) return false;

            try (PreparedStatement psCase = conn.prepareStatement(sqlInsertCase)) {
                psCase.setInt(1, groupId);
                psCase.setInt(2, memberId);
                psCase.setString(3, type);
                psCase.setString(4, description);
                psCase.setDouble(5, amount);
                return psCase.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Map<String, String>> getPendingClaimsByGroup(int groupId) {
        List<Map<String, String>> list = new ArrayList<>();
        // FIXED: Swapped out non-existent requested_amount field for amount_needed
        String sql = "SELECT ec.id, m.full_name, ec.amount_needed, ec.description " +
                "FROM emergency_cases ec " +
                "JOIN members m ON ec.member_id = m.id " +
                "WHERE ec.group_id = ? AND UPPER(TRIM(ec.status)) = 'PENDING' " +
                "ORDER BY ec.id ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, groupId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> map = new HashMap<>();
                    map.put("tx_id", String.valueOf(rs.getInt("id")));
                    map.put("member_name", rs.getString("full_name"));
                    map.put("amount", String.valueOf(rs.getDouble("amount_needed")));
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
    public boolean authorizePayout(int groupId, String caseTxId, double amount, String approvedBy, String notes) {
        String sqlUpdateCase = "UPDATE emergency_cases SET status = 'APPROVED' WHERE id = ?";
        // FIXED: Map to created_at instead of missing date tracker parameters
        String sqlInsertPayout = "INSERT INTO transactions (member_id, group_id, amount, type, description, created_at) VALUES " +
                "((SELECT member_id FROM emergency_cases WHERE id = ?), ?, ?, 'PAYOUT', ?, CURRENT_TIMESTAMP)";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            int caseId = Integer.parseInt(caseTxId);

            try (PreparedStatement psUp = conn.prepareStatement(sqlUpdateCase)) {
                psUp.setInt(1, caseId);
                psUp.executeUpdate();
            }

            try (PreparedStatement psTx = conn.prepareStatement(sqlInsertPayout)) {
                psTx.setInt(1, caseId);
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

    @Override
    public double getActualAvailableRoundPool(int groupId) {
        String sql = "SELECT " +
                " (SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE group_id = ? AND type = 'CONTRIBUTION') as total_in, " +
                " (SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE group_id = ? AND type = 'PAYOUT') as total_out";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, groupId);
            ps.setInt(2, groupId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total_in") - rs.getDouble("total_out");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    @Override
    public double getGroupBalance(int groupId) {
        String sql = "SELECT " +
                "  (SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE group_id = ? AND type = 'CONTRIBUTION') as total_contributions, " +
                "  (SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE group_id = ? AND type = 'PAYOUT') as total_payouts";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, groupId);
            ps.setInt(2, groupId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total_contributions") - rs.getDouble("total_payouts");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    @Override
    public void clearGroupTransactions(int groupId) {
        String sql = "DELETE FROM transactions WHERE group_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, groupId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error occurred while clearing logs: " + e.getMessage());
        }
    }

    @Override
    public boolean removeMemberFromGroup(int groupId, String memberName) {
        String sqlFindMember = "SELECT id FROM members WHERE full_name = ? LIMIT 1";
        String sqlUnlinkMember = "DELETE FROM group_members WHERE group_id = ? AND member_id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            int memberId = -1;

            try (PreparedStatement psM = conn.prepareStatement(sqlFindMember)) {
                psM.setString(1, memberName);
                try (ResultSet rs = psM.executeQuery()) {
                    if (rs.next()) memberId = rs.getInt("id");
                }
            }

            if (memberId == -1) {
                conn.rollback();
                return false;
            }

            int rowsDeleted = 0;
            try (PreparedStatement psDel = conn.prepareStatement(sqlUnlinkMember)) {
                psDel.setInt(1, groupId);
                psDel.setInt(2, memberId);
                rowsDeleted = psDel.executeUpdate();
            }

            conn.commit();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}