package dao.impl;

import dao.ReportDAO;
import model.ReportDataModels.*;
import util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportDAOImpl implements ReportDAO {

    // Helper to abstract database routing connectivity using your project's active manager
    private Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }

    @Override
    public MemberReport fetchMemberReportData(String searchKeyword) {
        MemberReport report = new MemberReport();
        report.transactions = new ArrayList<>();

        // Resolves member metrics using the structural 'members' table mapped via user references
        String memberSql = "SELECT m.id AS member_uuid, m.full_name, " +
                "COALESCE((SELECT COUNT(*) FROM transactions t WHERE t.member_id = m.id), 0) as txn_count, " +
                "COALESCE((SELECT SUM(t.amount) FROM transactions t WHERE t.member_id = m.id), 0) as total_paid, " +
                "COALESCE((SELECT COUNT(DISTINCT t.group_id) FROM transactions t WHERE t.member_id = m.id), 0) as groups_count " +
                "FROM members m " +
                "LEFT JOIN users u ON m.user_id = u.id " +
                "WHERE m.full_name ILIKE ? OR u.username ILIKE ? LIMIT 1";

        String txnSql = "SELECT id, date::text, amount, type, group_type, description FROM transactions " +
                "WHERE member_id = ? ORDER BY date DESC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(memberSql)) {

            String queryParam = "%" + searchKeyword + "%";
            stmt.setString(1, queryParam);
            stmt.setString(2, queryParam);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int memberId = rs.getInt("member_uuid");
                    report.name = rs.getString("full_name");
                    report.transactionCount = rs.getInt("txn_count");
                    report.totalPaid = rs.getDouble("total_paid");
                    report.groupsJoinedCount = rs.getInt("groups_count");

                    try (PreparedStatement txnStmt = conn.prepareStatement(txnSql)) {
                        txnStmt.setInt(1, memberId);
                        try (ResultSet txRs = txnStmt.executeQuery()) {
                            while (txRs.next()) {
                                report.transactions.add(new TransactionRow(
                                        "TXN-" + txRs.getInt("id"),
                                        txRs.getString("date"),
                                        txRs.getDouble("amount"),
                                        txRs.getString("type"),
                                        txRs.getString("group_type"),
                                        txRs.getString("description")
                                ));
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return report;
    }

    @Override
    public List<String> fetchEqubGroupNames() {
        List<String> groups = new ArrayList<>();
        // Query your specific equb_groups table so the UI dropdown lists them all
        String sql = "SELECT name FROM equb_groups ORDER BY name ASC";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String name = rs.getString("name");
                if (name != null && !name.trim().isEmpty()) {
                    groups.add(name);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return groups;
    }

    @Override
    public EqubReport fetchEqubReportData(String equbGroupName) {
        EqubReport report = new EqubReport();
        report.memberRows = new ArrayList<>();

        // Get group details directly from equb_groups
        String groupSql = "SELECT id, contribution_amount FROM equb_groups WHERE name = ? LIMIT 1";

        // Query transactions using global group mappings or specific text references
        String summarySql = "SELECT COUNT(DISTINCT t.member_id) as total_members, " +
                "COALESCE(SUM(t.amount), 0) as total_collected " +
                "FROM transactions t " +
                "LEFT JOIN groups g ON t.group_id = g.id " +
                "WHERE g.name = ? OR t.group_type ILIKE 'EQUB'";

        String membersSql = "SELECT m.full_name, COALESCE(SUM(t.amount), 0) as contributed " +
                "FROM transactions t " +
                "JOIN members m ON t.member_id = m.id " +
                "LEFT JOIN groups g ON t.group_id = g.id " +
                "WHERE g.name = ? OR t.group_type ILIKE 'EQUB' " +
                "GROUP BY m.full_name";

        try (Connection conn = getConnection()) {
            double baseContribution = 0.0;

            try (PreparedStatement stmt = conn.prepareStatement(groupSql)) {
                stmt.setString(1, equbGroupName);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        baseContribution = rs.getDouble("contribution_amount");
                    }
                }
            }

            try (PreparedStatement stmt = conn.prepareStatement(summarySql)) {
                stmt.setString(1, equbGroupName);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        report.totalMembers = rs.getInt("total_members");
                        report.totalCollected = rs.getDouble("total_collected");
                    }
                }
            }

            try (PreparedStatement memStmt = conn.prepareStatement(membersSql)) {
                memStmt.setString(1, equbGroupName);
                try (ResultSet mRs = memStmt.executeQuery()) {
                    while (mRs.next()) {
                        double contributed = mRs.getDouble("contributed");
                        int cyclesPaid = baseContribution > 0 ? (int)(contributed / baseContribution) : 1;
                        if (cyclesPaid == 0) cyclesPaid = 1;

                        report.memberRows.add(new EqubMemberRow(
                                mRs.getString("full_name"),
                                cyclesPaid,
                                contributed,
                                "Active"
                        ));
                    }
                }
            }

            // UI metric visual fallbacks
            report.currentCycle = "Running";
            report.nextPayoutMember = "Rotational Draw";

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return report;
    }

    @Override
    public List<String> fetchEdirGroupNames() {
        List<String> groups = new ArrayList<>();
        // Query your specific edir_groups table so the UI dropdown lists them all
        String sql = "SELECT name FROM edir_groups ORDER BY name ASC";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String name = rs.getString("name");
                if (name != null && !name.trim().isEmpty()) {
                    groups.add(name);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return groups;
    }

    @Override
    public EdirReport fetchEdirReportData(String edirGroupName) {
        EdirReport report = new EdirReport();
        report.emergencyRows = new ArrayList<>();

        // Get total financial investments via the transaction ledger
        String summarySql = "SELECT COALESCE(SUM(t.amount), 0) as total_contrib " +
                "FROM transactions t " +
                "LEFT JOIN groups g ON t.group_id = g.id " +
                "WHERE g.name = ? OR t.group_type ILIKE 'EDIR'";

        // Track claim payouts/withdrawals to dynamically compute running fund pool safety metrics
        String casesSql = "SELECT COUNT(*) as total_cases, " +
                "COALESCE(SUM(t.amount), 0) as paid_out " +
                "FROM transactions t " +
                "LEFT JOIN groups g ON t.group_id = g.id " +
                "WHERE (g.name = ? OR t.group_type ILIKE 'EDIR') " +
                "AND LOWER(t.type) IN ('payout', 'claim', 'withdrawal', 'expense')";

        String claimsSql = "SELECT t.date::text, m.full_name, t.type, t.description, t.amount " +
                "FROM transactions t " +
                "JOIN members m ON t.member_id = m.id " +
                "LEFT JOIN groups g ON t.group_id = g.id " +
                "WHERE g.name = ? OR t.group_type ILIKE 'EDIR' " +
                "ORDER BY t.date DESC";

        try (Connection conn = getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(summarySql)) {
                stmt.setString(1, edirGroupName);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        report.totalContributions = rs.getDouble("total_contrib");
                    }
                }
            }

            double totalPaidOutExpenses = 0.0;
            try (PreparedStatement stmt = conn.prepareStatement(casesSql)) {
                stmt.setString(1, edirGroupName);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        report.totalEmergencyCases = rs.getInt("total_cases");
                        report.approvedEmergencies = rs.getInt("total_cases");
                        totalPaidOutExpenses = rs.getDouble("paid_out");
                    }
                }
            }

            report.pendingEmergencies = 0;
            report.remainingFundBalance = report.totalContributions - totalPaidOutExpenses;

            try (PreparedStatement claimStmt = conn.prepareStatement(claimsSql)) {
                claimStmt.setString(1, edirGroupName);
                try (ResultSet cRs = claimStmt.executeQuery()) {
                    while (cRs.next()) {
                        report.emergencyRows.add(new EdirEmergencyRow(
                                cRs.getString("date"),
                                cRs.getString("full_name"),
                                cRs.getString("type"),
                                "Processed",
                                cRs.getDouble("amount")
                        ));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return report;
    }

    @Override
    public SystemReport fetchSystemReportData() {
        SystemReport report = new SystemReport();
        report.recentTransactions = new ArrayList<>();

        String summarySql = "SELECT " +
                "(SELECT COUNT(*) FROM users) as user_count, " +
                "(SELECT COUNT(*) FROM members) as member_count, " +
                "(SELECT COUNT(*) FROM groups) as group_count, " +
                "(SELECT COUNT(*) FROM transactions) as tx_count, " +
                "COALESCE((SELECT SUM(amount) FROM transactions), 0) as total_money";

        String recentTxSql = "SELECT t.date::text, t.amount, COALESCE(g.name, 'Global') as group_name, t.group_type, t.description " +
                "FROM transactions t " +
                "LEFT JOIN groups g ON t.group_id = g.id " +
                "ORDER BY t.date DESC LIMIT 10";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(summarySql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                report.totalUsers = rs.getInt("user_count");
                report.totalMembers = rs.getInt("member_count");
                report.totalGroups = rs.getInt("group_count");
                report.totalTransactions = rs.getInt("tx_count");
                report.totalMoneyInSystem = rs.getDouble("total_money");

                try (PreparedStatement txStmt = conn.prepareStatement(recentTxSql);
                     ResultSet txRs = txStmt.executeQuery()) {
                    while (txRs.next()) {
                        report.recentTransactions.add(new TransactionRow(
                                "",
                                txRs.getString("date"),
                                txRs.getDouble("amount"),
                                txRs.getString("group_type"),
                                txRs.getString("group_name"),
                                txRs.getString("description")
                        ));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return report;
    }
}