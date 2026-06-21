package dao.impl;

import dao.TransactionDAO;
import model.Transaction;
import util.DBConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.math.BigDecimal;

public class TransactionDAOImpl implements TransactionDAO {
  
  private Connection connection;
  
  public TransactionDAOImpl() {
    this.connection = DBConnection.getConnection();
  }
  
  @Override
  public void add(Transaction transaction) {
    String sql = "INSERT INTO transactions (group_id, member_id, type, amount, description, status, transaction_date, created_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setInt(1, transaction.getGroupId());
      stmt.setInt(2, transaction.getMemberId());
      stmt.setString(3, transaction.getTransactionType());
      stmt.setBigDecimal(4, transaction.getAmount());
      stmt.setString(5, transaction.getDescription());
      stmt.setString(6, transaction.getStatus());
      stmt.setTimestamp(7, Timestamp.valueOf(transaction.getTransactionDate()));
      stmt.setTimestamp(8, Timestamp.valueOf(transaction.getCreatedDate()));
      stmt.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("Error adding transaction: " + e.getMessage(), e);
    }
  }
  
  @Override
  public void update(Transaction transaction) {
    String sql = "UPDATE transactions SET group_id=?, member_id=?, type=?, amount=?, description=?, status=?, transaction_date=? WHERE id=?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setInt(1, transaction.getGroupId());
      stmt.setInt(2, transaction.getMemberId());
      stmt.setString(3, transaction.getTransactionType());
      stmt.setBigDecimal(4, transaction.getAmount());
      stmt.setString(5, transaction.getDescription());
      stmt.setString(6, transaction.getStatus());
      stmt.setTimestamp(7, Timestamp.valueOf(transaction.getTransactionDate()));
      stmt.setInt(8, transaction.getId());
      stmt.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("Error updating transaction: " + e.getMessage(), e);
    }
  }
  
  @Override
  public void delete(int transactionId) {
    String sql = "DELETE FROM transactions WHERE id=?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setInt(1, transactionId);
      stmt.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("Error deleting transaction: " + e.getMessage(), e);
    }
  }
  
  @Override
  public Transaction getById(int transactionId) {
    String sql = "SELECT * FROM transactions WHERE id=?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setInt(1, transactionId);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        return mapResultSetToTransaction(rs);
      }
    } catch (SQLException e) {
      throw new RuntimeException("Error getting transaction: " + e.getMessage(), e);
    }
    return null;
  }
  
  @Override
  public List<Transaction> getAll() {
    List<Transaction> transactions = new ArrayList<>();
    String sql = "SELECT * FROM transactions ORDER BY transaction_date DESC";
    try (Statement stmt = connection.createStatement()) {
      ResultSet rs = stmt.executeQuery(sql);
      while (rs.next()) {
        transactions.add(mapResultSetToTransaction(rs));
      }
    } catch (SQLException e) {
      throw new RuntimeException("Error getting all transactions: " + e.getMessage(), e);
    }
    return transactions;
  }
  
  @Override
  public List<Transaction> getByGroupId(int groupId) {
    List<Transaction> transactions = new ArrayList<>();
    String sql = "SELECT * FROM transactions WHERE group_id=? ORDER BY transaction_date DESC";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setInt(1, groupId);
      ResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        transactions.add(mapResultSetToTransaction(rs));
      }
    } catch (SQLException e) {
      throw new RuntimeException("Error getting transactions by group: " + e.getMessage(), e);
    }
    return transactions;
  }
  
  @Override
  public List<Transaction> getByMemberId(int memberId) {
    List<Transaction> transactions = new ArrayList<>();
    String sql = "SELECT * FROM transactions WHERE member_id=? ORDER BY transaction_date DESC";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setInt(1, memberId);
      ResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        transactions.add(mapResultSetToTransaction(rs));
      }
    } catch (SQLException e) {
      throw new RuntimeException("Error getting transactions by member: " + e.getMessage(), e);
    }
    return transactions;
  }
  
  @Override
  public List<Transaction> getByType(String type) {
    List<Transaction> transactions = new ArrayList<>();
    String sql = "SELECT * FROM transactions WHERE type=? ORDER BY transaction_date DESC";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, type);
      ResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        transactions.add(mapResultSetToTransaction(rs));
      }
    } catch (SQLException e) {
      throw new RuntimeException("Error getting transactions by type: " + e.getMessage(), e);
    }
    return transactions;
  }
  
  @Override
  public List<Transaction> getByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
    List<Transaction> transactions = new ArrayList<>();
    String sql = "SELECT * FROM transactions WHERE transaction_date BETWEEN ? AND ? ORDER BY transaction_date DESC";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setTimestamp(1, Timestamp.valueOf(startDate));
      stmt.setTimestamp(2, Timestamp.valueOf(endDate));
      ResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        transactions.add(mapResultSetToTransaction(rs));
      }
    } catch (SQLException e) {
      throw new RuntimeException("Error getting transactions by date range: " + e.getMessage(), e);
    }
    return transactions;
  }
  
  @Override
  public BigDecimal getTotalAmount(int groupId) {
    String sql = "SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE group_id=?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setInt(1, groupId);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        return rs.getBigDecimal(1);
      }
    } catch (SQLException e) {
      throw new RuntimeException("Error getting total amount: " + e.getMessage(), e);
    }
    return BigDecimal.ZERO;
  }
  
  @Override
  public int countByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
    String sql = "SELECT COUNT(*) FROM transactions WHERE transaction_date BETWEEN ? AND ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setTimestamp(1, Timestamp.valueOf(startDate));
      stmt.setTimestamp(2, Timestamp.valueOf(endDate));
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        return rs.getInt(1);
      }
    } catch (SQLException e) {
      throw new RuntimeException("Error counting transactions: " + e.getMessage(), e);
    }
    return 0;
  }
  
  @Override
  public int count() {
    String sql = "SELECT COUNT(*) FROM transactions";
    try (Statement stmt = connection.createStatement()) {
      ResultSet rs = stmt.executeQuery(sql);
      if (rs.next()) {
        return rs.getInt(1);
      }
    } catch (SQLException e) {
      throw new RuntimeException("Error counting transactions: " + e.getMessage(), e);
    }
    return 0;
  }
  
  private Transaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
    Transaction transaction = new Transaction();
    transaction.setId(rs.getInt("id"));
    transaction.setGroupId(rs.getInt("group_id"));
    transaction.setMemberId(rs.getInt("member_id"));
    transaction.setTransactionType(rs.getString("type"));
    transaction.setAmount(rs.getBigDecimal("amount"));
    transaction.setDescription(rs.getString("description"));
    transaction.setStatus(rs.getString("status"));
    Timestamp txnTs = rs.getTimestamp("transaction_date");
    if (txnTs != null) {
      transaction.setTransactionDate(txnTs.toLocalDateTime());
    }
    Timestamp createdTs = rs.getTimestamp("created_date");
    if (createdTs != null) {
      transaction.setCreatedDate(createdTs.toLocalDateTime());
    }
    return transaction;
  }
}
