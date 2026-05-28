package model;

import java.util.Date;

/**
 * Stores payment and payout history.
 */
public class Transaction {
    private String memberName;
    private double amount;
    private String type; // e.g., "PAYMENT" or "PAYOUT"
    private Date date;

    public Transaction(String memberName, double amount, String type) {
        this.memberName = memberName;
        this.amount = amount;
        this.type = type;
        this.date = new Date();
    }

    // Getters
    public String getMemberName() {
        return memberName;
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
}