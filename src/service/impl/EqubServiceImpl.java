package service.impl;

import service.EqubService;
import dao.EqubDAO;
import dao.impl.EqubDAOImpl;
import model.Group;
import model.Member;
import model.Transaction;
import util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EqubServiceImpl implements EqubService {
    private final EqubDAO equbDAO = new EqubDAOImpl();

    @Override public void createEqubGroup(String name, double amt) { equbDAO.createEqubGroup(name, amt); }
    @Override public List<Group> getAllEqubGroups() { return equbDAO.getAllEqubGroups(); }
    @Override public void deleteGroup(int id) { equbDAO.deleteEqubGroup(id); }
    @Override public List<Member> getMembersInGroup(int id) { return equbDAO.getMembersInGroup(id); }
    @Override public void addMemberToGroup(int gId, int mId) { equbDAO.addMemberToGroup(gId, mId); }
    @Override public void recordPayment(int gId, int mId, double a, String d, String n) { equbDAO.recordPayment(gId, mId, a, d, n); }
    @Override public void recordPayout(int gId, int mId, double a, String d, String ds) { equbDAO.recordPayout(gId, mId, a, d, ds); }
    @Override public List<Transaction> getRecentPaymentsForGroup(int id) { return equbDAO.getRecentPaymentsForGroup(id); }
    @Override public Member triggerRandomRotationalDraw(int id) { return equbDAO.triggerRandomRotationalDraw(id); }

    @Override
    public List<Member> getAllSystemMembers() {
        List<Member> list = new ArrayList<>();
        String sql = "SELECT id, full_name, phone FROM members ORDER BY full_name ASC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Member m = new Member();
                m.setId(rs.getInt("id"));
                m.setFullName(rs.getString("full_name"));
                m.setPhone(rs.getString("phone"));
                list.add(m);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }


    // Example SQL Implementation to put inside your Member/Equb DAO:
    public int createNewSystemMember(Member member) {
        // ❌ REMOVED: "email" column and its corresponding '?' placeholder
        String sql = "INSERT INTO members (full_name, phone) VALUES (?, ?) RETURNING id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Match the two remaining placeholders perfectly
            stmt.setString(1, member.getFullName());
            stmt.setString(2, member.getPhone());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1); // Safely returns the newly created ID
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}