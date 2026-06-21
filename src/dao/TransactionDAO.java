package dao;

import model.Transaction;
import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;

public interface TransactionDAO {
  
  /**
   * Add a new transaction
   */
  void add(Transaction transaction);
  
  /**
   * Update an existing transaction
   */
  void update(Transaction transaction);
  
  /**
   * Delete a transaction
   */
  void delete(int transactionId);
  
  /**
   * Get transaction by ID
   */
  Transaction getById(int transactionId);
  
  /**
   * Get all transactions
   */
  List<Transaction> getAll();
  
  /**
   * Get transactions by group
   */
  List<Transaction> getByGroupId(int groupId);
  
  /**
   * Get transactions by member
   */
  List<Transaction> getByMemberId(int memberId);
  
  /**
   * Get transactions by type
   */
  List<Transaction> getByType(String type);
  
  /**
   * Get transactions within date range
   */
  List<Transaction> getByDateRange(LocalDateTime startDate, LocalDateTime endDate);
  
  /**
   * Get total transaction amount for a group
   */
  BigDecimal getTotalAmount(int groupId);
  
  /**
   * Count transactions within date range
   */
  int countByDateRange(LocalDateTime startDate, LocalDateTime endDate);
  
  /**
   * Count total transactions
   */
  int count();
}
