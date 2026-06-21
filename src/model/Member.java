package model;

import java.time.LocalDateTime;

public class Member {
  private int id;
  private String firstName;
  private String lastName;
  private String email;
  private String phone;
  private String address;
  private String status;
  private LocalDateTime createdDate;
  private LocalDateTime updatedDate;
  
  public Member() {
    this.createdDate = LocalDateTime.now();
    this.updatedDate = LocalDateTime.now();
  }
  
  public Member(String firstName, String lastName, String email, String phone, String address) {
    this();
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.phone = phone;
    this.address = address;
    this.status = "Active";
  }
  
  // Getters and Setters
  public int getId() { return id; }
  public void setId(int id) { this.id = id; }
  
  public String getFirstName() { return firstName; }
  public void setFirstName(String firstName) { this.firstName = firstName; }
  
  public String getLastName() { return lastName; }
  public void setLastName(String lastName) { this.lastName = lastName; }
  
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
  
  public String getPhone() { return phone; }
  public void setPhone(String phone) { this.phone = phone; }
  
  public String getAddress() { return address; }
  public void setAddress(String address) { this.address = address; }
  
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
  
  public LocalDateTime getCreatedDate() { return createdDate; }
  public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
  
  public LocalDateTime getUpdatedDate() { return updatedDate; }
  public void setUpdatedDate(LocalDateTime updatedDate) { this.updatedDate = updatedDate; }
  
  public String getFullName() {
    return firstName + " " + lastName;
  }
  
  @Override
  public String toString() {
    return "Member{" +
      "id=" + id +
      ", firstName='" + firstName + '\'' +
      ", lastName='" + lastName + '\'' +
      ", email='" + email + '\'' +
      ", phone='" + phone + '\'' +
      ", status='" + status + '\'' +
      '}';
  }
}
