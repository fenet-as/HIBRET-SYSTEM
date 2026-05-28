package model;

public class Member {
    private int id;
    private String fullName;
    private String phone;
    private String email;
    private String status; // Active, Suspended

    // Default constructor
    public Member() {}

    public Member(int id, String fullName, String phone) {
        this.id = id;
        this.fullName = fullName;
        this.phone = phone;
    }

    // --- CRITICAL FIXED METRIC METHODS ---
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Displays nicely inside JComboBox components during entry workflows
    @Override
    public String toString() {
        return fullName + " (" + phone + ")";
    }
}