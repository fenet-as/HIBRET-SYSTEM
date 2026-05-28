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
    public void createEqubGroup(String name, double contributionAmount) {
        String sqlGroups = "INSERT INTO groups (name, type) VALUES (?, 'EQUB') RETURNING id";
        String sqlEqub = "INSERT INTO equb_groups (id, name, contribution_amount) VALUES (?, ?, ?)";
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmtG = conn.prepareStatement(sqlGroups)) {
                stmtG.setString(1, name);
                try (ResultSet rs = stmtG.executeQuery()) {
                    if (rs.next()) {
                        int generatedGroupId = rs.getInt(1);
                        try (PreparedStatement stmtE = conn.prepareStatement(sqlEqub)) {
                            stmtE.setInt(1, generatedGroupId);
                            stmtE.setString(2, name);
                            stmtE.setDouble(3, contributionAmount);
                            stmtE.executeUpdate();
                        }
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Group> getAllEqubGroups() {
        List<Group> list = new ArrayList<>();
        // 🎯 FIXED: Mapped to your correct plural database table name: 'transactions'
        String sql = "SELECT eg.id, eg.name, eg.contribution_amount, " +
                "COALESCE((SELECT COUNT(DISTINCT member_id) FROM transactions WHERE group_id = eg.id), 0) as active_members, " +
                "COALESCE((SELECT SUM(amount) FROM transactions WHERE group_id = eg.id AND type = 'PAYMENT'), 0) as total_coll, " +
                "(SELECT m.full_name FROM transactions t JOIN members m ON t.member_id = m.id WHERE t.group_id = eg.id AND t.type = 'PAYOUT' ORDER BY t.date DESC LIMIT 1) as next_payout " +
                "FROM equb_groups eg ORDER BY eg.name ASC";

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
        // 🎯 FIXED: Mapped to your correct plural database table name: 'transactions'
        String sqlTx = "DELETE FROM transactions WHERE group_id = ?";
        String sqlEqub = "DELETE FROM equb_groups WHERE id = ?";
        String sqlGroup = "DELETE FROM groups WHERE id = ?";
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement sTx = conn.prepareStatement(sqlTx);
                 PreparedStatement sEq = conn.prepareStatement(sqlEqub);
                 PreparedStatement sGr = conn.prepareStatement(sqlGroup)) {

                sTx.setInt(1, groupId);
                sTx.executeUpdate();

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
        // 🎯 FIXED: Mapped to your correct plural database table name: 'transactions'
        String sql = "SELECT DISTINCT m.id, m.full_name, m.phone FROM members m " +
                "JOIN transactions t ON t.member_id = m.id WHERE t.group_id = ?";
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
        // 🎯 FIXED: Mapped to your correct plural database table name: 'transactions'
        String sql = "INSERT INTO transactions (member_id, group_id, group_type, amount, type, date, description) " +
                "VALUES (?, ?, 'EQUB', 0, 'REGISTRATION', CURRENT_TIMESTAMP, 'Joined Equb Group Pool Ledger Index')";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, memberId);
            stmt.setInt(2, groupId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void recordPayment(int groupId, int memberId, double amount, String date, String note) {
        // 🎯 FIXED: Mapped to your correct plural database table name: 'transactions'
        String sql = "INSERT INTO transactions (member_id, group_id, group_type, amount, type, date, description) " +
                "VALUES (?, ?, 'EQUB', ?, 'PAYMENT', CURRENT_TIMESTAMP, ?)";
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
        // 🎯 FIXED: Mapped to your correct plural database table name: 'transactions'
        String sql = "INSERT INTO transactions (member_id, group_id, group_type, amount, type, date, description) " +
                "VALUES (?, ?, 'EQUB', ?, 'PAYOUT', CURRENT_TIMESTAMP, ?)";
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
        // 🎯 FIXED: Mapped to your correct plural database table name: 'transactions'
        String sql = "SELECT t.id, t.amount, t.date, t.type, m.full_name " +
                "FROM transactions t JOIN members m ON t.member_id = m.id " +
                "WHERE t.group_id = ? AND t.type IN ('PAYMENT', 'PAYOUT') ORDER BY t.date DESC";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, groupId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Transaction tx = new Transaction();
                    tx.setId(rs.getInt("id"));
                    tx.setAmount(rs.getDouble("amount"));
                    Timestamp ts = rs.getTimestamp("date");
                    if (ts != null) {
                        tx.setDate(new java.util.Date(ts.getTime()));
                    }
                    tx.setType(rs.getString("type"));
                    tx.setMemberName(rs.getString("full_name"));
                    tx.setStatus(rs.getString("type").equalsIgnoreCase("PAYMENT") ? "Paid" : "Paid Out");
                    list.add(tx);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Member triggerRandomRotationalDraw(int groupId) {
        // 🎯 FIXED: Mapped to your correct plural database table name: 'transactions'
        String sql = "SELECT DISTINCT m.id, m.full_name FROM members m " +
                "JOIN transactions t ON t.member_id = m.id " +
                "WHERE t.group_id = ? AND m.id NOT IN " +
                "(SELECT member_id FROM transactions WHERE group_id = ? AND type = 'PAYOUT') " +
                "ORDER BY RANDOM() LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, groupId);
            stmt.setInt(2, groupId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Member m = new Member();
                    m.setId(rs.getInt("id"));
                    m.setFullName(rs.getString("full_name"));
                    return m;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
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
}