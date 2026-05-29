package model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Group {
    private int id;
    private String name;
    private String type; // 'EQUB' or 'EDIR'
    private int createdBy;
    private double contributionAmount; // Aligns with 'monthly_fee' for Edir
    private LocalDateTime createdAt;

    // --- Edir Explicit Extensions ---
    private double fundBalance;        // Holds the running net capital vault pool
    private String rules;              // Holds claims criteria and bylaws text

    // Dynamic runtime metrics computed via aggregations
    private int activeMemberCount;
    private double totalCollectedCalculated;
    private String nextPayoutMemberName;
    private int missedPaymentsCount;
    private int activeCasesCount;       // Edir real-time pending emergency count

    // Default Constructor
    public Group() {
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Helper mapping constructor to convert data maps directly into a strong typed Model object.
     * Keeps your layers seamlessly aligned when fetching from DAO strings.
     */
    public static Group fromMap(Map<String, String> data) {
        if (data == null) return null;
        Group g = new Group();
        g.setName(data.get("name"));
        g.setContributionAmount(Double.parseDouble(data.getOrDefault("monthly_fee", "0.0")));
        g.setFundBalance(Double.parseDouble(data.getOrDefault("fund_balance", "0.0")));
        g.setActiveMemberCount(Integer.parseInt(data.getOrDefault("member_count", "0")));
        g.setActiveCasesCount(Integer.parseInt(data.getOrDefault("active_cases", "0")));
        g.setRules(data.get("rules"));
        return g;
    }

    /**
     * Converts this Model object properties to a string map structure
     * perfectly satisfying your existing UI panel requirements.
     */
    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<>();
        map.put("name", this.name);
        map.put("monthly_fee", String.valueOf(this.contributionAmount));
        map.put("fund_balance", String.valueOf(this.fundBalance));
        map.put("member_count", String.valueOf(this.activeMemberCount));
        map.put("active_cases", String.valueOf(this.activeCasesCount));
        map.put("rules", this.rules);
        return map;
    }

    // --- Standard Getters and Setters ---
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

    // --- Getters and Setters for Edir Extensions ---
    public double getFundBalance() { return fundBalance; }
    public void setFundBalance(double fundBalance) { this.fundBalance = fundBalance; }

    public String getRules() { return rules; }
    public void setRules(String rules) { this.rules = rules; }

    public int getActiveCasesCount() { return activeCasesCount; }
    public void setActiveCasesCount(int activeCasesCount) { this.activeCasesCount = activeCasesCount; }

    // --- Aggregated Performance Metrics ---
    public int getActiveMemberCount() { return activeMemberCount; }
    public void setActiveMemberCount(int activeMemberCount) { this.activeMemberCount = activeMemberCount; }

    public double getTotalCollectedCalculated() { return totalCollectedCalculated; }
    public void setTotalCollectedCalculated(double totalCollectedCalculated) { this.totalCollectedCalculated = totalCollectedCalculated; }

    public String getNextPayoutMemberName() { return nextPayoutMemberName; }
    public void setNextPayoutMemberName(String nextPayoutMemberName) { this.nextPayoutMemberName = nextPayoutMemberName; }

    public int getMissedPaymentsCount() { return missedPaymentsCount; }
    public void setMissedPaymentsCount(int missedPaymentsCount) { this.missedPaymentsCount = missedPaymentsCount; }
}