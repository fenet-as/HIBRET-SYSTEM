package dao.impl;

import dao.ReportDAO;
import model.ReportDataModels.*;
import model.User;
import session.Session;
import util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportDAOImpl implements ReportDAO {

    private int getAuthenticatedSessionUserId() {
        User loggedInUser = Session.getCurrentUser();
        if (loggedInUser != null) {
            return loggedInUser.getId();
        }
        return -1;
    }

    private Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }

    /**
     * 1. INDIVIDUAL MEMBER LIST SIDEBAR
     * FIXED: Querying structural normalized tables directly instead of the broken view.
     * Finds every unique member linked to any group created by the logged-in user.
     */
    @Override
    public List<String> fetchAllManagedMemberNames() {
        List<String> names = new ArrayList<>();
        int creatorId = getAuthenticatedSessionUserId();

        System.out.println("--> REPORTS: Fetching managed members for User ID: " + creatorId);

        String sql = "SELECT DISTINCT m.full_name " +
                "FROM members m " +
                "JOIN group_members gm ON m.id = gm.member_id " +
                "JOIN groups g ON gm.group_id = g.id " +
                "WHERE g.created_by = ? AND m.full_name IS NOT NULL " +
                "ORDER BY m.full_name ASC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, creatorId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    names.add(rs.getString("full_name"));
                }
            }
            System.out.println("--> REPORTS: Found " + names.size() + " unique members across user groups.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return names;
    }

    /**
     * 2. INDIVIDUAL MEMBER DASHBOARD METRICS & LEDGER
     * FIXED: Eliminated the user_member_group_summary view reference.
     * Properly resolves the active member based on direct relationships.
     */
    @Override
    public MemberReport fetchMemberReportData(String searchKeyword) {
        MemberReport report = new MemberReport();
        report.transactions = new ArrayList<>();

        int creatorId = getAuthenticatedSessionUserId();
        boolean isEmptySearch = (searchKeyword == null || searchKeyword.trim().isEmpty());

        String memberSql;
        if (isEmptySearch) {
            // Find the alphabetically first member belonging to this user's groups
            memberSql = "SELECT m.id AS member_id, m.full_name AS member_name " +
                    "FROM members m " +
                    "JOIN group_members gm ON m.id = gm.member_id " +
                    "JOIN groups g ON gm.group_id = g.id " +
                    "WHERE g.created_by = ? " +
                    "ORDER BY m.full_name ASC LIMIT 1";
        } else {
            // Target the specific member by name within the user's groups
            memberSql = "SELECT DISTINCT m.id AS member_id, m.full_name AS member_name " +
                    "FROM members m " +
                    "JOIN group_members gm ON m.id = gm.member_id " +
                    "JOIN groups g ON gm.group_id = g.id " +
                    "WHERE g.created_by = ? AND UPPER(TRIM(m.full_name)) = UPPER(TRIM(?)) LIMIT 1";
        }

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(memberSql)) {

            stmt.setInt(1, creatorId);
            if (!isEmptySearch) {
                stmt.setString(2, searchKeyword.trim());
            }

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int memberId = rs.getInt("member_id");
                    report.name = rs.getString("member_name");

                    // Total transactions logged across system for this member
                    String countSql = "SELECT COUNT(*) FROM transactions WHERE member_id = ?";
                    try (PreparedStatement cStmt = conn.prepareStatement(countSql)) {
                        cStmt.setInt(1, memberId);
                        try (ResultSet crs = cStmt.executeQuery()) {
                            if (crs.next()) report.transactionCount = crs.getInt(1);
                        }
                    }

                    // Case-insensitive broad filter matching contribution ledger mutations
                    String sumSql = "SELECT COALESCE(SUM(amount), 0) FROM transactions " +
                            "WHERE member_id = ? AND UPPER(TRIM(type)) IN ('CONTRIBUTION', 'DEPOSIT', 'PAYMENT', 'IN')";
                    try (PreparedStatement sStmt = conn.prepareStatement(sumSql)) {
                        sStmt.setInt(1, memberId);
                        try (ResultSet srs = sStmt.executeQuery()) {
                            if (srs.next()) report.totalPaid = srs.getDouble(1);
                        }
                    }

                    // Unique structural groups joined by this member via normalized table layout
                    String gCountSql = "SELECT COUNT(DISTINCT group_id) FROM group_members WHERE member_id = ?";
                    try (PreparedStatement gStmt = conn.prepareStatement(gCountSql)) {
                        gStmt.setInt(1, memberId);
                        try (ResultSet grs = gStmt.executeQuery()) {
                            if (grs.next()) report.groupsJoinedCount = grs.getInt(1);
                        }
                    }

                    // Changed t.date to t.created_at to match relational schema layout
                    String txnSql = "SELECT t.id, t.created_at::text as txn_date, t.amount, t.type, " +
                            "COALESCE(g.name, 'Global System') as display_group_name, t.description " +
                            "FROM transactions t " +
                            "LEFT JOIN groups g ON t.group_id = g.id " +
                            "WHERE t.member_id = ? ORDER BY t.created_at DESC";

                    try (PreparedStatement txnStmt = conn.prepareStatement(txnSql)) {
                        txnStmt.setInt(1, memberId);
                        try (ResultSet txRs = txnStmt.executeQuery()) {
                            while (txRs.next()) {
                                String rawDate = txRs.getString("txn_date");
                                report.transactions.add(new TransactionRow(
                                        "TXN-" + txRs.getInt("id"),
                                        rawDate != null && rawDate.length() > 16 ? rawDate.substring(0, 16) : rawDate,
                                        txRs.getDouble("amount"),
                                        txRs.getString("type"),
                                        txRs.getString("display_group_name"),
                                        txRs.getString("description")
                                ));
                            }
                        }
                    }
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return report;
    }

    /**
     * 3. EQUB GROUP FILTER POPULATION ROUTINE
     */
    @Override
    public List<String> fetchEqubGroupNames() {
        List<String> groups = new ArrayList<>();
        int creatorId = getAuthenticatedSessionUserId();

        String sql = "SELECT name FROM groups WHERE UPPER(TRIM(type)) = 'EQUB' AND created_by = ? ORDER BY name ASC";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, creatorId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    groups.add(rs.getString("name"));
                }
            }
            System.out.println("--> EQUB: Found " + groups.size() + " Equb groups for dashboard selection.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return groups;
    }

    /**
     * 4. EQUB METRICS AND PARTICIPANTS STATUS GRID
     */
    @Override
    public EqubReport fetchEqubReportData(String equbGroupName) {
        EqubReport report = new EqubReport();
        report.memberRows = new ArrayList<>();
        int creatorId = getAuthenticatedSessionUserId();

        System.out.println("\n================= 🏛️ EQUB REPORT DEBUG LAYER =================");
        System.out.println("--> [START] Target Group Name: [" + equbGroupName + "] | Logged In User ID: " + creatorId);

        String baseGroupSql = "SELECT id FROM groups WHERE UPPER(TRIM(name)) = UPPER(TRIM(?)) AND created_by = ? AND UPPER(TRIM(type)) = 'EQUB' LIMIT 1";
        String configSql = "SELECT COALESCE(contribution_amount, 0) as amount FROM equb_groups WHERE UPPER(TRIM(name)) = UPPER(TRIM(?)) LIMIT 1";

        String rosterSql = "SELECT m.id as m_id, m.full_name as m_name, " +
                "COALESCE((SELECT SUM(t.amount) FROM transactions t " +
                "          WHERE t.group_id = gm.group_id " +
                "            AND t.member_id = m.id " +
                "            AND (t.type IS NULL " +
                "                 OR UPPER(TRIM(t.type)) IN ('CONTRIBUTION', 'DEPOSIT', 'PAYMENT', 'IN'))), 0) as paid_amount " +
                "FROM group_members gm " +
                "JOIN members m ON gm.member_id = m.id " +
                "WHERE gm.group_id = ?";

        try (Connection conn = getConnection()) {
            int groupId = -1;
            double baseContributionAmount = 0.0;

            try (PreparedStatement stmt = conn.prepareStatement(baseGroupSql)) {
                stmt.setString(1, equbGroupName);
                stmt.setInt(2, creatorId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        groupId = rs.getInt("id");
                    }
                }
            }

            System.out.println("--> Resolved Group ID from Database: " + groupId);

            if (groupId == -1) {
                System.out.println("--> [ABORT] No matching group entry found for user context.");
                return null;
            }

            try (PreparedStatement stmt = conn.prepareStatement(configSql)) {
                stmt.setString(1, equbGroupName);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        baseContributionAmount = rs.getDouble("amount");
                    }
                }
            }

            double computedRunningTotalAggregate = 0.0;
            int totalActiveMembersDetected = 0;

            try (PreparedStatement rosterStmt = conn.prepareStatement(rosterSql)) {
                rosterStmt.setInt(1, groupId);
                try (ResultSet rs = rosterStmt.executeQuery()) {
                    while (rs.next()) {
                        totalActiveMembersDetected++;
                        String memberName = rs.getString("m_name");
                        double memberTotalPaid = rs.getDouble("paid_amount");

                        computedRunningTotalAggregate += memberTotalPaid;

                        int cyclesPaid = baseContributionAmount > 0 ? (int) (memberTotalPaid / baseContributionAmount) : 0;
                        if (cyclesPaid == 0 && memberTotalPaid > 0) {
                            cyclesPaid = 1;
                        }

                        String paymentStatus = memberTotalPaid > 0 ? "Paid (" + cyclesPaid + "x)" : "Pending Payment";

                        report.memberRows.add(new EqubMemberRow(
                                memberName,
                                cyclesPaid,
                                memberTotalPaid,
                                paymentStatus
                        ));
                    }
                }
            }

            report.totalMembers = totalActiveMembersDetected;
            report.totalCollected = computedRunningTotalAggregate;
            report.currentCycle = "Ongoing Rotation";
            report.nextPayoutMember = "Rotational Draw Pool";

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return report;
    }

    /**
     * 5. EDIR GROUP FILTER POPULATION ROUTINE
     */
    @Override
    public List<String> fetchEdirGroupNames() {
        List<String> groups = new ArrayList<>();
        int creatorId = getAuthenticatedSessionUserId();
        String sql = "SELECT name FROM groups WHERE UPPER(TRIM(type)) = 'EDIR' AND created_by = ? ORDER BY name ASC";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, creatorId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    groups.add(rs.getString("name"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return groups;
    }

    /**
     * 6. EDIR METRICS AND EMERGENCY CASES TABLE
     */
    @Override
    public EdirReport fetchEdirReportData(String edirGroupName) {
        EdirReport report = new EdirReport();
        report.emergencyRows = new ArrayList<>();
        int creatorId = getAuthenticatedSessionUserId();

        String baseGroupSql = "SELECT id FROM groups WHERE UPPER(TRIM(name)) = UPPER(TRIM(?)) AND created_by = ? AND UPPER(TRIM(type)) = 'EDIR' LIMIT 1";

        String ledgerSql = "SELECT t.created_at::text as txn_date, m.full_name, t.type, t.description, t.amount " +
                "FROM transactions t " +
                "JOIN members m ON t.member_id = m.id " +
                "WHERE t.group_id = ? " +
                "ORDER BY t.created_at DESC";

        String statsSql = "SELECT " +
                "COUNT(*) as total_cases, " +
                "COUNT(CASE WHEN UPPER(TRIM(status)) = 'APPROVED' THEN 1 END) as approved_cases, " +
                "COUNT(CASE WHEN UPPER(TRIM(status)) = 'PENDING' OR UPPER(TRIM(status)) LIKE '%PENDING%' THEN 1 END) as pending_cases " +
                "FROM emergency_cases WHERE group_id = ?";

        try (Connection conn = getConnection()) {
            int groupId = -1;

            try (PreparedStatement stmt = conn.prepareStatement(baseGroupSql)) {
                stmt.setString(1, edirGroupName);
                stmt.setInt(2, creatorId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        groupId = rs.getInt("id");
                    }
                }
            }

            if (groupId == -1) return null;

            double dynamicRunningContributionsTotal = 0.0;
            double dynamicRunningPayoutsTotal = 0.0;

            try (PreparedStatement stmt = conn.prepareStatement(ledgerSql)) {
                stmt.setInt(1, groupId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String dateStr = rs.getString("txn_date");
                        String shortDate = (dateStr != null && dateStr.length() > 10) ? dateStr.substring(0, 10) : dateStr;
                        String memberName = rs.getString("full_name");
                        String rawType = rs.getString("type") != null ? rs.getString("type").toUpperCase().trim() : "";
                        double amount = rs.getDouble("amount");

                        String uiDisplayType = rs.getString("type");
                        String uiDisplayStatus = "Processed";

                        if (rawType.equals("CONTRIBUTION") || rawType.equals("REGISTRATION") || rawType.equals("DEPOSIT") || rawType.equals("IN")) {
                            dynamicRunningContributionsTotal += amount;
                        } else if (rawType.equals("PAYOUT") || rawType.equals("OUT")) {
                            dynamicRunningPayoutsTotal += amount;
                            uiDisplayStatus = "Approved & Closed";
                            if (uiDisplayType == null || uiDisplayType.trim().isEmpty()) {
                                uiDisplayType = "Emergency Payout";
                            }
                        } else if (rawType.contains("CLAIM") || rawType.contains("EMERGENCY")) {
                            if (!rawType.contains("APPROVED_CLAIM")) {
                                dynamicRunningPayoutsTotal += amount;
                            }
                            uiDisplayStatus = rawType.contains("PENDING") ? "🚨 Pending Review" : "Approved & Closed";
                        } else {
                            dynamicRunningContributionsTotal += amount;
                        }

                        report.emergencyRows.add(new EdirEmergencyRow(
                                shortDate,
                                memberName,
                                uiDisplayType,
                                uiDisplayStatus,
                                amount
                        ));
                    }
                }
            }

            try (PreparedStatement stmt = conn.prepareStatement(statsSql)) {
                stmt.setInt(1, groupId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        report.totalEmergencyCases = rs.getInt("total_cases");
                        report.approvedEmergencies = rs.getInt("approved_cases");
                        report.pendingEmergencies = rs.getInt("pending_cases");
                    }
                }
            }

            report.totalContributions = dynamicRunningContributionsTotal;
            report.remainingFundBalance = dynamicRunningContributionsTotal - dynamicRunningPayoutsTotal;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return report;
    }

    /**
     * 7. SYSTEM REPORT SUMMARY
     */
    @Override
    public SystemReport fetchSystemReportData() {
        SystemReport report = new SystemReport();
        report.recentTransactions = new ArrayList<>();
        int creatorId = getAuthenticatedSessionUserId();

        String summarySql = "SELECT " +
                "(SELECT COUNT(DISTINCT gm.member_id) FROM group_members gm JOIN groups g ON gm.group_id = g.id WHERE g.created_by = ?) as total_m, " +
                "(SELECT COUNT(*) FROM groups WHERE created_by = ?) as total_g, " +
                "(SELECT COUNT(*) FROM transactions t JOIN groups g ON t.group_id = g.id WHERE g.created_by = ?) as total_t, " +
                "COALESCE((SELECT SUM(t.amount) FROM transactions t JOIN groups g ON t.group_id = g.id WHERE UPPER(TRIM(t.type)) IN ('CONTRIBUTION', 'DEPOSIT', 'IN') AND g.created_by = ?), 0) as total_cash";

        String recentTxSql = "SELECT t.created_at::text as txn_date, t.amount, COALESCE(g.name, 'Global System') as group_name, t.type, t.description " +
                "FROM transactions t " +
                "JOIN groups g ON t.group_id = g.id " +
                "WHERE g.created_by = ? " +
                "ORDER BY t.created_at DESC LIMIT 10";

        try (Connection conn = getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(summarySql)) {
                stmt.setInt(1, creatorId);
                stmt.setInt(2, creatorId);
                stmt.setInt(3, creatorId);
                stmt.setInt(4, creatorId);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        report.totalUsers = 0;
                        report.totalMembers = rs.getInt("total_m");
                        report.totalGroups = rs.getInt("total_g");
                        report.totalTransactions = rs.getInt("total_t");
                        report.totalMoneyInSystem = rs.getDouble("total_cash");
                    }
                }
            }

            try (PreparedStatement stmt = conn.prepareStatement(recentTxSql)) {
                stmt.setInt(1, creatorId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        TransactionRow row = new TransactionRow();
                        row.transactionId = "TXN-LOG";
                        String rawDate = rs.getString("txn_date");
                        row.date = rawDate != null && rawDate.length() > 16 ? rawDate.substring(0, 16) : rawDate;
                        row.amount = rs.getDouble("amount");
                        row.type = rs.getString("type");
                        row.groupName = rs.getString("group_name");
                        row.description = rs.getString("description");
                        report.recentTransactions.add(row);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return report;
    }
}