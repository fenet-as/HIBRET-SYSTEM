package dao.impl;

import dao.EqubDAO;
import model.Group;
import model.Member;
import model.Transaction;
import util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EqubDAOImpl implements EqubDAO {

    private Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }

    @Override
    public List<Group> getEqubGroupsForUser(int userId) {
        List<Group> list = new ArrayList<>();
        // FIXED: Swapped 't3.date' for 't3.created_at' and filtered by g.created_by instead of m.user_id (which doesn't exist)
        String sql = "SELECT DISTINCT g.id, g.name, eq.contribution_amount, " +
                "  (SELECT COUNT(*) FROM group_members gm2 WHERE gm2.group_id = g.id) as active_members, " +
                "  COALESCE((SELECT SUM(t2.amount) FROM transactions t2 WHERE t2.group_id = g.id AND UPPER(t2.type) IN ('PAYMENT', 'CONTRIBUTION')), 0) as total_coll, " +
                "  (SELECT m2.full_name FROM transactions t3 JOIN members m2 ON t3.member_id = m2.id " +
                "   WHERE t3.group_id = g.id AND UPPER(t3.type) = 'PAYOUT' ORDER BY t3.created_at DESC LIMIT 1) as next_payout " +
                "FROM groups g " +
                "JOIN equb_groups eq ON g.id = eq.id " +
                "JOIN group_members gm ON g.id = gm.group_id " +
                "JOIN members m ON gm.member_id = m.id " +
                "WHERE g.type = 'EQUB' AND g.created_by = ? " +
                "ORDER BY g.name ASC";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Group g = new Group();
                    g.setId(rs.getInt("id"));
                    g.setName(rs.getString("name"));
                    g.setContributionAmount(rs.getDouble("contribution_amount"));
                    g.setActiveMemberCount(rs.getInt("active_members"));
                    g.setTotalCollectedCalculated(rs.getDouble("total_coll"));
                    g.setNextPayoutMemberName(rs.getString("next_payout"));
                    g.setMissedPaymentsCount(0);
                    list.add(g);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }




    @Override
    public void createEqubGroup(String name, double contributionAmount, int creatorUserId) {
        // VALIDATION CHECK QUERY: Prevent duplicates by the same user
        String sqlCheckDuplicate = "SELECT COUNT(*) FROM groups WHERE UPPER(TRIM(name)) = UPPER(TRIM(?)) AND type = 'EQUB' AND created_by = ?";

        String sqlGroups = "INSERT INTO groups (name, type, created_by, contribution_amount, created_at) VALUES (?, 'EQUB', ?, ?, CURRENT_TIMESTAMP) RETURNING id";
        String sqlEqub = "INSERT INTO equb_groups (id, name, contribution_amount, created_at) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";

        String sqlFetchUserName = "SELECT full_name FROM users WHERE id = ?";
        String sqlFindMemberByName = "SELECT id FROM members WHERE UPPER(TRIM(full_name)) = UPPER(TRIM(?)) LIMIT 1";
        String sqlFindMemberByPhone = "SELECT id FROM members WHERE phone = 'N/A' LIMIT 1";

        String sqlCreateMember = "INSERT INTO members (full_name, phone) VALUES (?, 'N/A') RETURNING id";
        String sqlLinkCreator = "INSERT INTO group_members (group_id, member_id) VALUES (?, ?)";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            // 1. DUPLICATE CHECK: Verify if this user already created a group with this name
            try (PreparedStatement psCheck = conn.prepareStatement(sqlCheckDuplicate)) {
                psCheck.setString(1, name);
                psCheck.setInt(2, creatorUserId);
                try (ResultSet rsCheck = psCheck.executeQuery()) {
                    if (rsCheck.next() && rsCheck.getInt(1) > 0) {
                        System.out.println("⚠️ Aborting group creation: An EQUB group named '" + name + "' already exists for this user.");
                        conn.rollback();
                        return; // Exit out of the method gracefully without inserting anything
                    }
                }
            }

            // 2. Fetch Creator's Name using their User ID
            String creatorName = "Equb Administrator";
            try (PreparedStatement psName = conn.prepareStatement(sqlFetchUserName)) {
                psName.setInt(1, creatorUserId);
                try (ResultSet rs = psName.executeQuery()) {
                    if (rs.next() && rs.getString("full_name") != null) {
                        creatorName = rs.getString("full_name");
                    }
                }
            }

            int generatedGroupId = -1;

            // 3. Insert into the core groups table
            try (PreparedStatement stmtG = conn.prepareStatement(sqlGroups)) {
                stmtG.setString(1, name);
                stmtG.setInt(2, creatorUserId);
                stmtG.setDouble(3, contributionAmount);
                try (ResultSet rs = stmtG.executeQuery()) {
                    if (rs.next()) {
                        generatedGroupId = rs.getInt(1);
                    }
                }
            }

            if (generatedGroupId == -1) {
                conn.rollback();
                return;
            }

            // 4. Insert into the equb_groups subtype table
            try (PreparedStatement stmtE = conn.prepareStatement(sqlEqub)) {
                stmtE.setInt(1, generatedGroupId);
                stmtE.setString(2, name);
                stmtE.setDouble(3, contributionAmount);
                stmtE.executeUpdate();
            }

            // 5. Find matching Member ID by the extracted name
            int memberId = -1;
            try (PreparedStatement psFindM = conn.prepareStatement(sqlFindMemberByName)) {
                psFindM.setString(1, creatorName);
                try (ResultSet rs = psFindM.executeQuery()) {
                    if (rs.next()) {
                        memberId = rs.getInt("id");
                    }
                }
            }

            // Unique Constraint Safety Net
            if (memberId == -1) {
                try (PreparedStatement psFindPhone = conn.prepareStatement(sqlFindMemberByPhone)) {
                    try (ResultSet rs = psFindPhone.executeQuery()) {
                        if (rs.next()) {
                            memberId = rs.getInt("id");
                        }
                    }
                }
            }

            // 6. If no member record exists, create it safely
            if (memberId == -1) {
                try (PreparedStatement psNewM = conn.prepareStatement(sqlCreateMember, Statement.RETURN_GENERATED_KEYS)) {
                    psNewM.setString(1, creatorName);
                    psNewM.executeUpdate();
                    try (ResultSet rs = psNewM.getGeneratedKeys()) {
                        if (rs.next()) {
                            memberId = rs.getInt(1);
                        }
                    }
                }
            }

            // 7. Link group and member records together
            if (memberId != -1) {
                try (PreparedStatement psLink = conn.prepareStatement(sqlLinkCreator)) {
                    psLink.setInt(1, generatedGroupId);
                    psLink.setInt(2, memberId);
                    psLink.executeUpdate();
                }
            }

            conn.commit();
            System.out.println("--> EQUB Group created cleanly via Creator User ID: " + creatorUserId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Group> getAllEqubGroups() {
        List<Group> list = new ArrayList<>();
        // FIXED: Swapped 't.date' to 't.created_at' inside structural layout lookup queries
        String sql = "SELECT g.id, g.name, eq.contribution_amount, " +
                "COALESCE((SELECT COUNT(*) FROM group_members WHERE group_id = g.id), 0) as active_members, " +
                "COALESCE((SELECT SUM(amount) FROM transactions WHERE group_id = g.id AND UPPER(type) IN ('PAYMENT', 'CONTRIBUTION')), 0) as total_coll, " +
                "(SELECT m.full_name FROM transactions t JOIN members m ON t.member_id = m.id WHERE t.group_id = g.id AND UPPER(t.type) = 'PAYOUT' ORDER BY t.created_at DESC LIMIT 1) as next_payout " +
                "FROM groups g JOIN equb_groups eq ON g.id = eq.id WHERE g.type = 'EQUB' ORDER BY g.name ASC";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Group g = new Group();
                g.setId(rs.getInt("id"));
                g.setName(rs.getString("name"));
                g.setContributionAmount(rs.getDouble("contribution_amount"));
                g.setActiveMemberCount(rs.getInt("active_members"));
                g.setTotalCollectedCalculated(rs.getDouble("total_coll"));
                g.setNextPayoutMemberName(rs.getString("next_payout"));
                g.setMissedPaymentsCount(0);
                list.add(g);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void deleteEqubGroup(int groupId) {
        String sqlTx = "DELETE FROM transactions WHERE group_id = ?";
        String sqlMembers = "DELETE FROM group_members WHERE group_id = ?";
        String sqlEqub = "DELETE FROM equb_groups WHERE id = ?";
        String sqlGroup = "DELETE FROM groups WHERE id = ?";
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement sTx = conn.prepareStatement(sqlTx);
                 PreparedStatement sMem = conn.prepareStatement(sqlMembers);
                 PreparedStatement sEq = conn.prepareStatement(sqlEqub);
                 PreparedStatement sGr = conn.prepareStatement(sqlGroup)) {

                sTx.setInt(1, groupId);
                sTx.executeUpdate();

                sMem.setInt(1, groupId);
                sMem.executeUpdate();

                sEq.setInt(1, groupId);
                sEq.executeUpdate();

                sGr.setInt(1, groupId);
                sGr.executeUpdate();

                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Member> getMembersInGroup(int groupId) {
        List<Member> list = new ArrayList<>();
        String sql = "SELECT m.id, m.full_name, m.phone FROM members m " +
                "JOIN group_members gm ON gm.member_id = m.id WHERE gm.group_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, groupId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Member m = new Member();
                    m.setId(rs.getInt("id"));
                    m.setFullName(rs.getString("full_name"));
                    m.setPhone(rs.getString("phone"));
                    list.add(m);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void addMemberToGroup(int groupId, int memberId) {
        String sql = "INSERT INTO group_members (group_id, member_id) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, groupId);
            stmt.setInt(2, memberId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void recordPayment(int groupId, int memberId, double amount, String date, String note) {
        // FIXED: Altered column name to track timestamp into 'created_at' instead of missing 'date' column
        String sql = "INSERT INTO transactions (member_id, group_id, amount, type, description, created_at) " +
                "VALUES (?, ?, ?, 'PAYMENT', ?, CURRENT_TIMESTAMP)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, memberId);
            stmt.setInt(2, groupId);
            stmt.setDouble(3, amount);
            stmt.setString(4, (note == null || note.trim().isEmpty()) ? "Equb Contribution Cycle Payment" : note);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void recordPayout(int groupId, int memberId, double amount, String date, String description) {
        // FIXED: Altered column parameter assignment from 'date' to 'created_at'
        String sql = "INSERT INTO transactions (member_id, group_id, amount, type, description, created_at) " +
                "VALUES (?, ?, ?, 'PAYOUT', ?, CURRENT_TIMESTAMP)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, memberId);
            stmt.setInt(2, groupId);
            stmt.setDouble(3, amount);
            stmt.setString(4, description);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Transaction> getRecentPaymentsForGroup(int groupId) {
        List<Transaction> list = new ArrayList<>();
        // FIXED: Changed 't.date' selection properties to track 't.created_at'
        String sql = "SELECT t.id, t.amount, t.created_at, t.type, m.full_name " +
                "FROM transactions t JOIN members m ON t.member_id = m.id " +
                "WHERE t.group_id = ? AND UPPER(t.type) IN ('PAYMENT', 'CONTRIBUTION', 'PAYOUT') ORDER BY t.id DESC";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, groupId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Transaction tx = new Transaction();
                    tx.setId(rs.getInt("id"));
                    tx.setAmount(rs.getDouble("amount"));
                    Timestamp ts = rs.getTimestamp("created_at");
                    if (ts != null) {
                        tx.setDate(new java.util.Date(ts.getTime()));
                    }
                    String rawType = rs.getString("type");
                    tx.setType(rawType);
                    tx.setMemberName(rs.getString("full_name"));
                    tx.setStatus(rawType.equalsIgnoreCase("PAYOUT") ? "Paid Out" : "Paid");
                    list.add(tx);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public int createNewSystemMember(Member member) {
        String sql = "INSERT INTO members (full_name, phone) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, member.getFullName());
            stmt.setString(2, member.getPhone());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public List<Member> getAllSystemMembers() {
        List<Member> list = new ArrayList<>();
        String sql = "SELECT id, full_name, phone FROM members ORDER BY full_name ASC";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Member m = new Member();
                m.setId(rs.getInt("id"));
                m.setFullName(rs.getString("full_name"));
                m.setPhone(rs.getString("phone"));
                list.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public int getCompletedRoundsCount(int groupId) {
        String sql = "SELECT COUNT(*) FROM transactions WHERE group_id = ? AND UPPER(type) = 'PAYOUT'";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, groupId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public double getActualAvailableRoundPool(int groupId) {
        String sql = "SELECT " +
                "  (SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE group_id = ? AND UPPER(type) IN ('CONTRIBUTION', 'PAYMENT')) as total_contributions, " +
                "  (SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE group_id = ? AND UPPER(type) = 'PAYOUT') as total_payouts";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, groupId);
            ps.setInt(2, groupId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double contributions = rs.getDouble("total_contributions");
                    double payouts = rs.getDouble("total_payouts");
                    return contributions - payouts;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    @Override
    public boolean reverseTransaction(int transactionId) {
        String sql = "DELETE FROM transactions WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, transactionId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean haveAllMembersPaidCurrentRound(int groupId) {
        String sqlMembers = "SELECT COUNT(*) FROM group_members WHERE group_id = ?";
        String sqlCurrentPayments = "SELECT COUNT(DISTINCT member_id) FROM transactions " +
                "WHERE group_id = ? AND UPPER(type) IN ('PAYMENT', 'CONTRIBUTION') " +
                "AND id > COALESCE((SELECT MAX(id) FROM transactions WHERE group_id = ? AND UPPER(type) = 'PAYOUT'), 0)";

        try (Connection conn = getConnection();
             PreparedStatement ps1 = conn.prepareStatement(sqlMembers);
             PreparedStatement ps2 = conn.prepareStatement(sqlCurrentPayments)) {

            ps1.setInt(1, groupId);
            int totalMembers = 0;
            try (ResultSet rs1 = ps1.executeQuery()) {
                if (rs1.next()) totalMembers = rs1.getInt(1);
            }

            if (totalMembers == 0) return false;

            ps2.setInt(1, groupId);
            ps2.setInt(2, groupId);
            int uniquePayeesThisRound = 0;
            try (ResultSet rs2 = ps2.executeQuery()) {
                if (rs2.next()) uniquePayeesThisRound = rs2.getInt(1);
            }

            return uniquePayeesThisRound >= totalMembers;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean clearAllTransactionsForGroup(int groupId) {
        String sql = "DELETE FROM transactions WHERE group_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, groupId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Member triggerRandomRotationalDraw(int groupId) {
        int completedRounds = getCompletedRoundsCount(groupId);
        String sqlMembersCount = "SELECT COUNT(*) FROM group_members WHERE group_id = ?";

        String sqlDraw = "SELECT m.id, m.full_name FROM members m " +
                "JOIN group_members gm ON gm.member_id = m.id " +
                "WHERE gm.group_id = ? AND " +
                "(SELECT COUNT(*) FROM transactions t WHERE t.group_id = ? AND t.member_id = m.id AND UPPER(t.type) = 'PAYOUT') < ? " +
                "ORDER BY RANDOM() LIMIT 1";

        try (Connection conn = getConnection();
             PreparedStatement psCount = conn.prepareStatement(sqlMembersCount)) {

            psCount.setInt(1, groupId);
            int totalMembers = 0;
            try (ResultSet rs = psCount.executeQuery()) {
                if (rs.next()) totalMembers = rs.getInt(1);
            }

            if (totalMembers == 0) return null;

            int currentCycleTarget = (completedRounds / totalMembers) + 1;

            try (PreparedStatement stmt = conn.prepareStatement(sqlDraw)) {
                stmt.setInt(1, groupId);
                stmt.setInt(2, groupId);
                stmt.setInt(3, currentCycleTarget);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Member m = new Member();
                        m.setId(rs.getInt("id"));
                        m.setFullName(rs.getString("full_name"));
                        return m;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean hasEligibleUnpaidMembers(int groupId) {
        String sql = "SELECT COUNT(*) FROM group_members WHERE group_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, groupId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean removeMemberFromGroup(int groupId, int memberId) {
        String sqlCheckTransactions = "SELECT COUNT(*) FROM transactions WHERE group_id = ? AND member_id = ?";
        String sqlDeleteMember = "DELETE FROM group_members WHERE group_id = ? AND member_id = ?";

        try (Connection conn = getConnection()) {
            try (PreparedStatement checkStmt = conn.prepareStatement(sqlCheckTransactions)) {
                checkStmt.setInt(1, groupId);
                checkStmt.setInt(2, memberId);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        return false;
                    }
                }
            }
            try (PreparedStatement deleteStmt = conn.prepareStatement(sqlDeleteMember)) {
                deleteStmt.setInt(1, groupId);
                deleteStmt.setInt(2, memberId);
                return deleteStmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}