package dao.impl;

import dao.MemberDAO;
import model.Member;
import util.DBConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class MemberDAOImpl implements MemberDAO {
  
  private Connection connection;
  
  public MemberDAOImpl() {
    this.connection = DBConnection.getConnection();
  }
  
  @Override
  public void add(Member member) {
    String sql = "INSERT INTO members (first_name, last_name, email, phone, address, status, created_date) VALUES (?, ?, ?, ?, ?, ?, ?)";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, member.getFirstName());
      stmt.setString(2, member.getLastName());
      stmt.setString(3, member.getEmail());
      stmt.setString(4, member.getPhone());
      stmt.setString(5, member.getAddress());
      stmt.setString(6, member.getStatus());
      stmt.setTimestamp(7, Timestamp.valueOf(member.getCreatedDate()));
      stmt.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("Error adding member: " + e.getMessage(), e);
    }
  }
  
  @Override
  public void update(Member member) {
    String sql = "UPDATE members SET first_name=?, last_name=?, email=?, phone=?, address=?, status=?, updated_date=? WHERE id=?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, member.getFirstName());
      stmt.setString(2, member.getLastName());
      stmt.setString(3, member.getEmail());
      stmt.setString(4, member.getPhone());
      stmt.setString(5, member.getAddress());
      stmt.setString(6, member.getStatus());
      stmt.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
      stmt.setInt(8, member.getId());
      stmt.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("Error updating member: " + e.getMessage(), e);
    }
  }
  
  @Override
  public void delete(int memberId) {
    String sql = "DELETE FROM members WHERE id=?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setInt(1, memberId);
      stmt.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("Error deleting member: " + e.getMessage(), e);
    }
  }
  
  @Override
  public Member getById(int memberId) {
    String sql = "SELECT * FROM members WHERE id=?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setInt(1, memberId);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        return mapResultSetToMember(rs);
      }
    } catch (SQLException e) {
      throw new RuntimeException("Error getting member: " + e.getMessage(), e);
    }
    return null;
  }
  
  @Override
  public List<Member> getAll() {
    List<Member> members = new ArrayList<>();
    String sql = "SELECT * FROM members ORDER BY id";
    try (Statement stmt = connection.createStatement()) {
      ResultSet rs = stmt.executeQuery(sql);
      while (rs.next()) {
        members.add(mapResultSetToMember(rs));
      }
    } catch (SQLException e) {
      throw new RuntimeException("Error getting all members: " + e.getMessage(), e);
    }
    return members;
  }
  
  @Override
  public List<Member> search(String searchTerm) {
    List<Member> members = new ArrayList<>();
    String sql = "SELECT * FROM members WHERE first_name LIKE ? OR last_name LIKE ? OR email LIKE ? ORDER BY first_name";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      String pattern = "%" + searchTerm + "%";
      stmt.setString(1, pattern);
      stmt.setString(2, pattern);
      stmt.setString(3, pattern);
      ResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        members.add(mapResultSetToMember(rs));
      }
    } catch (SQLException e) {
      throw new RuntimeException("Error searching members: " + e.getMessage(), e);
    }
    return members;
  }
  
  @Override
  public List<Member> getByStatus(String status) {
    List<Member> members = new ArrayList<>();
    String sql = "SELECT * FROM members WHERE status=? ORDER BY first_name";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, status);
      ResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        members.add(mapResultSetToMember(rs));
      }
    } catch (SQLException e) {
      throw new RuntimeException("Error getting members by status: " + e.getMessage(), e);
    }
    return members;
  }
  
  @Override
  public int count() {
    String sql = "SELECT COUNT(*) FROM members";
    try (Statement stmt = connection.createStatement()) {
      ResultSet rs = stmt.executeQuery(sql);
      if (rs.next()) {
        return rs.getInt(1);
      }
    } catch (SQLException e) {
      throw new RuntimeException("Error counting members: " + e.getMessage(), e);
    }
    return 0;
  }
  
  @Override
  public boolean emailExists(String email) {
    String sql = "SELECT COUNT(*) FROM members WHERE email=?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, email);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        return rs.getInt(1) > 0;
      }
    } catch (SQLException e) {
      throw new RuntimeException("Error checking email: " + e.getMessage(), e);
    }
    return false;
  }
  
  @Override
  public List<Member> getMembersInGroup(int groupId) {
    List<Member> members = new ArrayList<>();
    String sql = "SELECT m.* FROM members m JOIN group_members gm ON m.id=gm.member_id WHERE gm.group_id=?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setInt(1, groupId);
      ResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        members.add(mapResultSetToMember(rs));
      }
    } catch (SQLException e) {
      throw new RuntimeException("Error getting group members: " + e.getMessage(), e);
    }
    return members;
  }
  
  private Member mapResultSetToMember(ResultSet rs) throws SQLException {
    Member member = new Member();
    member.setId(rs.getInt("id"));
    member.setFirstName(rs.getString("first_name"));
    member.setLastName(rs.getString("last_name"));
    member.setEmail(rs.getString("email"));
    member.setPhone(rs.getString("phone"));
    member.setAddress(rs.getString("address"));
    member.setStatus(rs.getString("status"));
    Timestamp createdTs = rs.getTimestamp("created_date");
    if (createdTs != null) {
      member.setCreatedDate(createdTs.toLocalDateTime());
    }
    Timestamp updatedTs = rs.getTimestamp("updated_date");
    if (updatedTs != null) {
      member.setUpdatedDate(updatedTs.toLocalDateTime());
    }
    return member;
  }
}
