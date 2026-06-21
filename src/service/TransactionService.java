package service;

import model.Transaction;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionService {
  
  /**
   * Record a new transaction
   */
  void recordTransaction(Transaction transaction);
  
  /**
   * Get transaction by ID
   */
  Transaction getTransactionById(int transactionId);
  
  /**
   * Get all transactions
   */
  List<Transaction> getAllTransactions();
  
  /**
   * Get transactions by group
   */
  List<Transaction> getTransactionsByGroup(int groupId);
  
  /**
   * Get transactions by member
   */
  List<Transaction> getTransactionsByMember(int memberId);
  
  /**
   * Get transactions by type
   */
  List<Transaction> getTransactionsByType(String type);
  
  /**
   * Get transactions within date range
   */
  List<Transaction> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
  
  /**
   * Get total transaction amount
   */
  java.math.BigDecimal getTotalTransactionAmount(int groupId);
  
  /**
   * Get transaction count for a period
   */
  int getTransactionCount(LocalDateTime startDate, LocalDateTime endDate);
  
  /**
   * Verify transaction
   */
  void verifyTransaction(int transactionId);
  
  /**
   * Cancel transaction
   */
  void cancelTransaction(int transactionId);
}
