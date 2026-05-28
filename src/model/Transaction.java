package model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Transaction {

    private int id;
    private int memberId;
    private int groupId;
    private double amount;
    private String type;
    private Date date;
    private String description;

    // 🌍 UI Extensibility fields (Not saved to the base transaction table, but used for joins)
    private String memberName;
    private String status;
    private String groupType;

    // --- EXISTING GETTERS ---
    public int getId() { return id; }
    public int getMemberId() { return memberId; }
    public int getGroupId() { return groupId; }
    public double getAmount() { return amount; }
    public String getType() { return type; }
    public Date getDate() { return date; }
    public String getDescription() { return description; }

    // --- EXISTING SETTERS ---
    public void setId(int id) { this.id = id; }
    public void setMemberId(int memberId) { this.memberId = memberId; }
    public void setGroupId(int groupId) { this.groupId = groupId; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setType(String type) { this.type = type; }
    public void setDate(Date date) { this.date = date; }
    public void setDescription(String description) { this.description = description; }

    // --- 🛠️ CRITICAL FIXED METHODS FOR DATA COHESION ---

    /**
     * Converts java.util.Date to String for safe visual presentation in JTables.
     */
    public String getDateString() {
        if (this.date == null) return "";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return formatter.format(this.date);
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }
}