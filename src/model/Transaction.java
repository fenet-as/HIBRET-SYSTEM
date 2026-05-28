package model;

import java.util.Date;

public class Transaction {

    private int id;
    private int memberId;
    private int groupId;
    private double amount;
    private String type;
    private Date date;
    private String description;

    // ✅ GETTERS
    public int getId() {
        return id;
    }

    public int getMemberId() {
        return memberId;
    }

    public int getGroupId() {
        return groupId;
    }

    public double getAmount() {
        return amount;
    }

    public String getType() {
        return type;
    }

    public Date getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    // ✅ SETTERS (THIS FIXES YOUR ERROR)
    public void setId(int id) {
        this.id = id;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}