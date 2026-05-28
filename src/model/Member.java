package model;

/**
 * Represents an individual member in an Equb group.
 */
public class Member {
    private String name;
    private String phone;
    private double paidAmount;

    public Member(String name, String phone) {
        this.name = name;
        this.phone = phone;
        this.paidAmount = 0.0;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public double getPaidAmount() {
        return paidAmount;
    }

    /**
     * Logic to record a payment for this member.
     * @param amount The amount to be added.
     */
    public void pay(double amount) {
        if (amount > 0) {
            this.paidAmount += amount;
        }
    }
}
