package model;

import java.time.LocalDateTime;

public class Group {
  private int id;
  private String name;
  private String type;
  private String description;
  private int totalMembers;
  private String status;
  private LocalDateTime createdDate;
  private LocalDateTime updatedDate;
  
  public Group() {
    this.createdDate = LocalDateTime.now();
    this.updatedDate = LocalDateTime.now();
    this.totalMembers = 0;
  }
  
  public Group(String name, String type, String description) {
    this();
    this.name = name;
    this.type = type;
    this.description = description;
    this.status = "Active";
  }
  
  // Getters and Setters
  public int getId() { return id; }
  public void setId(int id) { this.id = id; }
  
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  
  public String getType() { return type; }
  public void setType(String type) { this.type = type; }
  
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
  
  public int getTotalMembers() { return totalMembers; }
  public void setTotalMembers(int totalMembers) { this.totalMembers = totalMembers; }
  
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
  
  public LocalDateTime getCreatedDate() { return createdDate; }
  public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
  
  public LocalDateTime getUpdatedDate() { return updatedDate; }
  public void setUpdatedDate(LocalDateTime updatedDate) { this.updatedDate = updatedDate; }
  
  @Override
  public String toString() {
    return "Group{" +
      "id=" + id +
      ", name='" + name + '\'' +
      ", type='" + type + '\'' +
      ", members=" + totalMembers +
      ", status='" + status + '\'' +
      '}';
  }
}
