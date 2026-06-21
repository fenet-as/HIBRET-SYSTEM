package model;

import java.time.LocalDateTime;

public class User {
  private int id;
  private String username;
  private String passwordHash;
  private String email;
  private String role;
  private String status;
  private LocalDateTime createdDate;
  private LocalDateTime lastLoginDate;
  
  public User() {
    this.createdDate = LocalDateTime.now();
  }
  
  public User(String username, String passwordHash, String email, String role) {
    this();
    this.username = username;
    this.passwordHash = passwordHash;
    this.email = email;
    this.role = role;
    this.status = "Active";
  }
  
  // Getters and Setters
  public int getId() { return id; }
  public void setId(int id) { this.id = id; }
  
  public String getUsername() { return username; }
  public void setUsername(String username) { this.username = username; }
  
  public String getPasswordHash() { return passwordHash; }
  public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
  
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
  
  public String getRole() { return role; }
  public void setRole(String role) { this.role = role; }
  
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
  
  public LocalDateTime getCreatedDate() { return createdDate; }
  public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
  
  public LocalDateTime getLastLoginDate() { return lastLoginDate; }
  public void setLastLoginDate(LocalDateTime lastLoginDate) { this.lastLoginDate = lastLoginDate; }
  
  @Override
  public String toString() {
    return "User{" +
      "id=" + id +
      ", username='" + username + '\'' +
      ", role='" + role + '\'' +
      ", status='" + status + '\'' +
      '}';
  }
}
