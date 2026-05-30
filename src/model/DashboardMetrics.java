package model;

public class DashboardMetrics {
    private double totalEqubVaultBalance;
    private int totalEqubCircles;
    private int totalEdirGroups;
    private int totalEdirMembers;
    private double totalEdirVaultBalance;
    private int activeEmergencyClaims;

    // Getters and Setters
    public double getTotalEqubVaultBalance() { return totalEqubVaultBalance; }
    public void setTotalEqubVaultBalance(double totalEqubVaultBalance) { this.totalEqubVaultBalance = totalEqubVaultBalance; }

    public int getTotalEqubCircles() { return totalEqubCircles; }
    public void setTotalEqubCircles(int totalEqubCircles) { this.totalEqubCircles = totalEqubCircles; }

    public int getTotalEdirGroups() { return totalEdirGroups; }
    public void setTotalEdirGroups(int totalEdirGroups) { this.totalEdirGroups = totalEdirGroups; }

    public int getTotalEdirMembers() { return totalEdirMembers; }
    public void setTotalEdirMembers(int totalEdirMembers) { this.totalEdirMembers = totalEdirMembers; }

    public double getTotalEdirVaultBalance() { return totalEdirVaultBalance; }
    public void setTotalEdirVaultBalance(double totalEdirVaultBalance) { this.totalEdirVaultBalance = totalEdirVaultBalance; }

    public int getActiveEmergencyClaims() { return activeEmergencyClaims; }
    public void setActiveEmergencyClaims(int activeEmergencyClaims) { this.activeEmergencyClaims = activeEmergencyClaims; }
}