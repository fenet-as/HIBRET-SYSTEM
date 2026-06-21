package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {
  private int id;
  private int groupId;
  private int memberId;
  private String transactionType;
  private BigDecimal amount;
  private String description;
  private LocalDateTime transactionDate;
  private String status;
  private LocalDateTime createdDate;
  
  public Transaction() {
    this.transactionDate = LocalDateTime.now();
    this.createdDate = LocalDateTime.now();
    this.status = "Completed";
  }
  
  public Transaction(int groupId, int memberId, String transactionType, BigDecimal amount, String description) {
    this();
    this.groupId = groupId;
    this.memberId = memberId;
    this.transactionType = transactionType;
    this.amount = amount;
    this.description = description;
  }
  
  // Getters and Setters
  public int getId() { return id; }
  public void setId(int id) { this.id = id; }
  
  public int getGroupId() { return groupId; }
  public void setGroupId(int groupId) { this.groupId = groupId; }
  
  public int getMemberId() { return memberId; }
  public void setMemberId(int memberId) { this.memberId = memberId; }
  
  public String getTransactionType() { return transactionType; }
  public void setTransactionType(String transactionType) { this.transactionType = transactionType; }
  
  public BigDecimal getAmount() { return amount; }
  public void setAmount(BigDecimal amount) { this.amount = amount; }
  
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
  
  public LocalDateTime getTransactionDate() { return transactionDate; }
  public void setTransactionDate(LocalDateTime transactionDate) { this.transactionDate = transactionDate; }
  
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
  
  public LocalDateTime getCreatedDate() { return createdDate; }
  public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
  
  @Override
  public String toString() {
    return "Transaction{" +
      "id=" + id +
      ", type='" + transactionType + '\'' +
      ", amount=" + amount +
      ", status='" + status + '\'' +
      '}';
  }
}
