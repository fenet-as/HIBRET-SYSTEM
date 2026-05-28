package model;

import java.time.LocalDateTime;

public class Group {
    private int id;
    private String name;
    private String type; // 'EQUB' or 'EDIR'
    private int createdBy;
    private double contributionAmount;
    private LocalDateTime createdAt;

    // Dynamic runtime metrics computed via aggregations
    private int activeMemberCount;
    private double totalCollectedCalculated;
    private String nextPayoutMemberName;
    private int missedPaymentsCount;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public int getCreatedBy() { return createdBy; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }
    public double getContributionAmount() { return contributionAmount; }
    public void setContributionAmount(double contributionAmount) { this.contributionAmount = contributionAmount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public int getActiveMemberCount() { return activeMemberCount; }
    public void setActiveMemberCount(int activeMemberCount) { this.activeMemberCount = activeMemberCount; }
    public double getTotalCollectedCalculated() { return totalCollectedCalculated; }
    public void setTotalCollectedCalculated(double totalCollectedCalculated) { this.totalCollectedCalculated = totalCollectedCalculated; }
    public String getNextPayoutMemberName() { return nextPayoutMemberName; }
    public void setNextPayoutMemberName(String nextPayoutMemberName) { this.nextPayoutMemberName = nextPayoutMemberName; }
    public int getMissedPaymentsCount() { return missedPaymentsCount; }
    public void setMissedPaymentsCount(int missedPaymentsCount) { this.missedPaymentsCount = missedPaymentsCount; }
}