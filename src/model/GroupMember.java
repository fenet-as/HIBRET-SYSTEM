package model;

import java.time.LocalDateTime;

public class GroupMember {
  private int id;
  private int groupId;
  private int memberId;
  private String role;
  private String membershipStatus;
  private LocalDateTime joinDate;
  private LocalDateTime updatedDate;
  
  public GroupMember() {
    this.joinDate = LocalDateTime.now();
    this.updatedDate = LocalDateTime.now();
    this.membershipStatus = "Active";
  }
  
  public GroupMember(int groupId, int memberId, String role) {
    this();
    this.groupId = groupId;
    this.memberId = memberId;
    this.role = role;
  }
  
  // Getters and Setters
  public int getId() { return id; }
  public void setId(int id) { this.id = id; }
  
  public int getGroupId() { return groupId; }
  public void setGroupId(int groupId) { this.groupId = groupId; }
  
  public int getMemberId() { return memberId; }
  public void setMemberId(int memberId) { this.memberId = memberId; }
  
  public String getRole() { return role; }
  public void setRole(String role) { this.role = role; }
  
  public String getMembershipStatus() { return membershipStatus; }
  public void setMembershipStatus(String membershipStatus) { this.membershipStatus = membershipStatus; }
  
  public LocalDateTime getJoinDate() { return joinDate; }
  public void setJoinDate(LocalDateTime joinDate) { this.joinDate = joinDate; }
  
  public LocalDateTime getUpdatedDate() { return updatedDate; }
  public void setUpdatedDate(LocalDateTime updatedDate) { this.updatedDate = updatedDate; }
  
  @Override
  public String toString() {
    return "GroupMember{" +
      "id=" + id +
      ", groupId=" + groupId +
      ", memberId=" + memberId +
      ", role='" + role + '\'' +
      ", status='" + membershipStatus + '\'' +
      '}';
  }
}
