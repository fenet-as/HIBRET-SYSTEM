package model;

import java.util.List;

public class ReportDataModels {

    // --- Member Report Structures ---
    public static class MemberReport {
        public String name;
        public int transactionCount;
        public double totalPaid;
        public int groupsJoinedCount;
        public List<TransactionRow> transactions;
    }

    public static class TransactionRow {
        public String transactionId;
        public String date;
        public double amount;
        public String type;
        public String groupName;
        public String description;

        public TransactionRow(String transactionId, String date, double amount, String type, String groupName, String description) {
            this.transactionId = transactionId;
            this.date = date;
            this.amount = amount;
            this.type = type;
            this.groupName = groupName;
            this.description = description;
        }
    }

    // --- Equb Report Structures ---
    public static class EqubReport {
        public int totalMembers;
        public double totalCollected;
        public String currentCycle;
        public String nextPayoutMember;
        public List<EqubMemberRow> memberRows;
    }

    public static class EqubMemberRow {
        public String memberName;
        public int cyclePaid;
        public double amount;
        public String status;

        public EqubMemberRow(String memberName, int cyclePaid, double amount, String status) {
            this.memberName = memberName;
            this.cyclePaid = cyclePaid;
            this.amount = amount;
            this.status = status;
        }
    }

    // --- Edir Report Structures ---
    public static class EdirReport {
        public double totalContributions;
        public int totalEmergencyCases;
        public int approvedEmergencies;
        public int pendingEmergencies;
        public double remainingFundBalance;
        public List<EdirEmergencyRow> emergencyRows;
    }

    public static class EdirEmergencyRow {
        public String date;
        public String memberName;
        public String type;
        public String status;
        public double amount;

        public EdirEmergencyRow(String date, String memberName, String type, String status, double amount) {
            this.date = date;
            this.memberName = memberName;
            this.type = type;
            this.status = status;
            this.amount = amount;
        }
    }

    // --- System Report Structures ---
    public static class SystemReport {
        public int totalUsers;
        public int totalMembers;
        public int totalGroups;
        public int totalTransactions;
        public double totalMoneyInSystem;
        public List<TransactionRow> recentTransactions;
    }
}