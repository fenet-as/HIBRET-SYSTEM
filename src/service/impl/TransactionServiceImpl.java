package service.impl;

import dao.TransactionDAO;
import dao.impl.TransactionDAOImpl;
import model.Transaction;
import service.TransactionService;
import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;

public class TransactionServiceImpl implements TransactionService {
  
  private TransactionDAO transactionDAO;
  
  public TransactionServiceImpl() {
    this.transactionDAO = new TransactionDAOImpl();
  }
  
  @Override
  public void recordTransaction(Transaction transaction) {
    if (transaction == null) {
      throw new IllegalArgumentException("Transaction cannot be null");
    }
    if (transaction.getGroupId() <= 0) {
      throw new IllegalArgumentException("Valid group ID required");
    }
    if (transaction.getMemberId() <= 0) {
      throw new IllegalArgumentException("Valid member ID required");
    }
    if (transaction.getAmount() == null || transaction.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Amount must be greater than zero");
    }
    transactionDAO.add(transaction);
  }
  
  @Override
  public Transaction getTransactionById(int transactionId) {
    if (transactionId <= 0) {
      throw new IllegalArgumentException("Valid transaction ID required");
    }
    return transactionDAO.getById(transactionId);
  }
  
  @Override
  public List<Transaction> getAllTransactions() {
    return transactionDAO.getAll();
  }
  
  @Override
  public List<Transaction> getTransactionsByGroup(int groupId) {
    if (groupId <= 0) {
      throw new IllegalArgumentException("Valid group ID required");
    }
    return transactionDAO.getByGroupId(groupId);
  }
  
  @Override
  public List<Transaction> getTransactionsByMember(int memberId) {
    if (memberId <= 0) {
      throw new IllegalArgumentException("Valid member ID required");
    }
    return transactionDAO.getByMemberId(memberId);
  }
  
  @Override
  public List<Transaction> getTransactionsByType(String type) {
    if (type == null || type.isEmpty()) {
      return transactionDAO.getAll();
    }
    return transactionDAO.getByType(type);
  }
  
  @Override
  public List<Transaction> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
    if (startDate == null || endDate == null) {
      return transactionDAO.getAll();
    }
    return transactionDAO.getByDateRange(startDate, endDate);
  }
  
  @Override
  public BigDecimal getTotalTransactionAmount(int groupId) {
    if (groupId <= 0) {
      throw new IllegalArgumentException("Valid group ID required");
    }
    return transactionDAO.getTotalAmount(groupId);
  }
  
  @Override
  public int getTransactionCount(LocalDateTime startDate, LocalDateTime endDate) {
    return transactionDAO.countByDateRange(startDate, endDate);
  }
  
  @Override
  public void verifyTransaction(int transactionId) {
    Transaction transaction = getTransactionById(transactionId);
    if (transaction != null) {
      transaction.setStatus("Verified");
      transactionDAO.update(transaction);
    }
  }
  
  @Override
  public void cancelTransaction(int transactionId) {
    Transaction transaction = getTransactionById(transactionId);
    if (transaction != null) {
      transaction.setStatus("Cancelled");
      transactionDAO.update(transaction);
    }
  }
}
