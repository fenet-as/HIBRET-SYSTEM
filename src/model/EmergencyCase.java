package model;

import java.sql.Timestamp;

public class EmergencyCase {
    private int id;
    private int memberId;
    private String memberName;
    private int groupId;
    private String emergencyType;
    private double amountNeeded;
    private String description;
    private String status;
    private Timestamp date;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getMemberId() { return memberId; }
    public void setMemberId(int memberId) { this.memberId = memberId; }
    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }
    public int getGroupId() { return groupId; }
    public void setGroupId(int groupId) { this.groupId = groupId; }
    public String getEmergencyType() { return emergencyType; }
    public void setEmergencyType(String type) { this.emergencyType = type; }
    public double getAmountNeeded() { return amountNeeded; }
    public void setAmountNeeded(double amount) { this.amountNeeded = amount; }
    public String getDescription() { return description; }
    public void setDescription(String desc) { this.description = desc; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Timestamp getDate() { return date; }
    public void setDate(Timestamp date) { this.date = date; }

    @Override
    public String toString() {
        return memberName + " - " + emergencyType + " (" + amountNeeded + " Birr)";
    }
}