package model;

import java.sql.Timestamp;

public class EdirGroup {
    private int id;
    private String name;
    private double contribution;
    private Timestamp createdAt;

    // Default Constructor
    public EdirGroup() {}

    // Overloaded Constructor for quick instantiation
    public EdirGroup(int id, String name, double contribution, Timestamp createdAt) {
        this.id = id;
        this.name = name;
        this.contribution = contribution;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getContribution() {
        return contribution;
    }

    public void setContribution(double contribution) {
        this.contribution = contribution;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    // Handy for UI ComboBox components
    @Override
    public String toString() {
        return this.name + " (" + this.contribution + " Birr)";
    }
}