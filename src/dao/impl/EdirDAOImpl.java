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

    // ✅ FIXED: Scoped explicitly to groups created/managed by the logged-in user, calculated strictly by primary key ID
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
        // ✅ USER-SCOPED HARDENING: Bound to creators' own account profile scope
        String sqlCheckName = "SELECT COUNT(*) FROM groups WHERE UPPER(TRIM(name)) = UPPER(TRIM(?)) AND type = 'EDIR' AND created_by = ?";

        String sqlGroup = "INSERT INTO groups (name, type, created_by) VALUES (?, 'EDIR', ?) RETURNING id";
        String sqlEdirSettings = "INSERT INTO edir_groups (id, name, contribution_amount) VALUES (?, ?, ?)";
        String sqlFindMember = "SELECT id FROM members WHERE user_id = ?";
        String sqlCreateMember = "INSERT INTO members (user_id, full_name, phone) VALUES (?, ?, 'N/A') RETURNING id";
        String sqlLinkCreator = "INSERT INTO group_members (group_id, member_id) VALUES (?, ?)";
        String sqlInitialTransaction = "INSERT INTO transactions (group_id, member_id, amount, type, description) VALUES (?, ?, ?, 'CONTRIBUTION', 'Initial reserves deposit pool')";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            // Step 0: Block duplicate group names for THIS user context
            try (PreparedStatement psCheck = conn.prepareStatement(sqlCheckName)) {
                psCheck.setString(1, groupName);
                psCheck.setInt(2, creatorUserId);
                try (ResultSet rsCheck = psCheck.executeQuery()) {
                    if (rsCheck.next() && rsCheck.getInt(1) > 0) {
                        System.err.println("⚠️ Validation Abort: Edir Group name '" + groupName + "' already exists for User ID: " + creatorUserId);
                        conn.rollback();
                        return false;
                    }
                }
            }

            int generatedGroupId = -1;

            // Step 1: Insert the master group recording WHO created it
            try (PreparedStatement psGroup = conn.prepareStatement(sqlGroup)) {
                psGroup.setString(1, groupName);
                psGroup.setInt(2, creatorUserId);
                try (ResultSet rs = psGroup.executeQuery()) {
                    if (rs.next()) generatedGroupId = rs.getInt(1);
                }
            }

            if (generatedGroupId == -1) {
                conn.rollback();
                return false;
            }

            // Step 2: Insert Edir extension details
            try (PreparedStatement psEdir = conn.prepareStatement(sqlEdirSettings)) {
                psEdir.setInt(1, generatedGroupId);
                psEdir.setString(2, groupName);
                psEdir.setDouble(3, monthlyFee);
                psEdir.executeUpdate();
            }

            // Step 3: Find or auto-generate the structural member_id for the creator
            int memberId = -1;
            try (PreparedStatement psFindM = conn.prepareStatement(sqlFindMember)) {
                psFindM.setInt(1, creatorUserId);
                try (ResultSet rs = psFindM.executeQuery()) {
                    if (rs.next()) memberId = rs.getInt("id");
                }
            }

            if (memberId == -1) {
                String fetchUserName = "SELECT full_name FROM users WHERE id = ?";
                String creatorName = "Group Administrator";
                try (PreparedStatement psName = conn.prepareStatement(fetchUserName)) {
                    psName.setInt(1, creatorUserId);
                    try (ResultSet rs = psName.executeQuery()) {
                        if (rs.next()) creatorName = rs.getString("full_name");
                    }
                }
                try (PreparedStatement psNewM = conn.prepareStatement(sqlCreateMember)) {
                    psNewM.setInt(1, creatorUserId);
                    psNewM.setString(2, creatorName);
                    try (ResultSet rs = psNewM.executeQuery()) {
                        if (rs.next()) memberId = rs.getInt(1);
                    }
                }
            }

            // Step 4: Link the creator to the group via group_members so it displays in their dashboard
            try (PreparedStatement psLink = conn.prepareStatement(sqlLinkCreator)) {
                psLink.setInt(1, generatedGroupId);
                psLink.setInt(2, memberId);
                psLink.executeUpdate();
            }

            // Step 5: Record the initial deposit ledger transaction assigned to this member
            if (initialPool > 0) {
                try (PreparedStatement psTx = conn.prepareStatement(sqlInitialTransaction)) {
                    psTx.setInt(1, generatedGroupId);
                    psTx.setInt(2, memberId);
                    psTx.setDouble(3, initialPool);
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

    // ✅ FIXED: Scoped strictly to ID constraint parameters
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

    // ✅ FIXED: Calculates and scopes strictly by Group ID
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
        String sql = "SELECT m.id, m.full_name, m.phone " +
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
    public boolean addMemberToGroup(int groupId, String fullName, String phone) {
        String sqlInsertMember = "INSERT INTO members (full_name, phone, user_id) VALUES (?, ?, null) RETURNING id";
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
        String sqlInsertTx = "INSERT INTO transactions (member_id, group_id, amount, type, description) VALUES (?, ?, ?, 'CONTRIBUTION', ?)";

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
        String sqlInsertCase = "INSERT INTO emergency_cases (group_id, member_id, title, description, requested_amount, status) VALUES (?, ?, ?, ?, ?, 'PENDING')";

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
        String sql = "SELECT ec.id, m.full_name, ec.requested_amount, ec.description " +
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
                    map.put("amount", String.valueOf(rs.getDouble("requested_amount")));
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
        String sqlInsertPayout = "INSERT INTO transactions (member_id, group_id, amount, type, description) VALUES " +
                "((SELECT member_id FROM emergency_cases WHERE id = ?), ?, ?, 'PAYOUT', ?)";

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

    // ✅ FIXED: Now computes balances precisely tracking unique Group ID records
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