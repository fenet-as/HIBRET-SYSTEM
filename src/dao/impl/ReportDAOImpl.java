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
     * Aligns with 'user_member_group_summary' filter parameters.
     */
    @Override
    public List<String> fetchAllManagedMemberNames() {
        List<String> names = new ArrayList<>();
        int creatorId = getAuthenticatedSessionUserId();

        System.out.println("--> REPORTS: Fetching managed members for User ID: " + creatorId);

        String sql = "SELECT DISTINCT member_name FROM user_member_group_summary " +
                "WHERE creator_user_id = ? AND member_name IS NOT NULL " +
                "ORDER BY member_name ASC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, creatorId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    names.add(rs.getString("member_name"));
                }
            }
            System.out.println("--> REPORTS: Found " + names.size() + " unique members in summary view.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return names;
    }

    /**
     * 2. INDIVIDUAL MEMBER DASHBOARD METRICS & LEDGER
     * Matches 'user_member_group_summary', 'transactions', and 'members' table rules.
     */
    @Override
    public MemberReport fetchMemberReportData(String searchKeyword) {
        MemberReport report = new MemberReport();
        report.transactions = new ArrayList<>();

        int creatorId = getAuthenticatedSessionUserId();
        boolean isEmptySearch = (searchKeyword == null || searchKeyword.trim().isEmpty());

        String memberSql;
        if (isEmptySearch) {
            memberSql = "SELECT DISTINCT member_id, member_name FROM user_member_group_summary " +
                    "WHERE creator_user_id = ? ORDER BY member_name ASC LIMIT 1";
        } else {
            memberSql = "SELECT DISTINCT member_id, member_name FROM user_member_group_summary " +
                    "WHERE creator_user_id = ? AND member_name = ? LIMIT 1";
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

                    // FIXED: Case-insensitive broad filter matching contribution ledger mutations
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

                    // Transaction lines collection routine
                    String txnSql = "SELECT t.id, t.date::text, t.amount, t.type, " +
                            "COALESCE(g.name, 'Global System') as display_group_name, t.description " +
                            "FROM transactions t " +
                            "LEFT JOIN groups g ON t.group_id = g.id " +
                            "WHERE t.member_id = ? ORDER BY t.date DESC";

                    try (PreparedStatement txnStmt = conn.prepareStatement(txnSql)) {
                        txnStmt.setInt(1, memberId);
                        try (ResultSet txRs = txnStmt.executeQuery()) {
                            while (txRs.next()) {
                                report.transactions.add(new TransactionRow(
                                        "TXN-" + txRs.getInt("id"),
                                        txRs.getString("date") != null && txRs.getString("date").length() > 16 ? txRs.getString("date").substring(0, 16) : txRs.getString("date"),
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
     * Maps to core 'groups' schema context definitions.
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
     * Maps to core 'groups' relational tracking, 'equb_groups' metadata, and normalized connections.
     */
    @Override
    public EqubReport fetchEqubReportData(String equbGroupName) {
        EqubReport report = new EqubReport();
        report.memberRows = new ArrayList<>();
        int creatorId = getAuthenticatedSessionUserId();

        System.out.println("\n================= 🏛️ EQUB REPORT DEBUG LAYER =================");
        System.out.println("--> [START] Target Group Name: [" + equbGroupName + "] | Logged In User ID: " + creatorId);

        // 1. Fetch group profile using a case-insensitive name match
        String baseGroupSql = "SELECT id FROM groups WHERE UPPER(TRIM(name)) = UPPER(TRIM(?)) AND created_by = ? AND UPPER(TRIM(type)) = 'EQUB' LIMIT 1";

        // 2. Fetch the contribution target configuration amount from equb_groups table
        String configSql = "SELECT COALESCE(contribution_amount, 0) as amount FROM equb_groups WHERE UPPER(TRIM(name)) = UPPER(TRIM(?)) LIMIT 1";

        // 3. BROADENED LEDGER QUERY: Matches any transaction linked to this group_id and member_id
        String rosterSql = "SELECT m.id as m_id, m.full_name as m_name, " +
                "COALESCE((SELECT SUM(t.amount) FROM transactions t " +
                "          WHERE t.group_id = gm.group_id " +
                "            AND t.member_id = m.id " +
                "            AND (t.type IS NULL " +
                "                 OR UPPER(TRIM(t.type)) IN ('CONTRIBUTION', 'DEPOSIT', 'PAYMENT', 'IN') " +
                "                 OR UPPER(TRIM(t.group_type)) = 'EQUB')), 0) as paid_amount " +
                "FROM group_members gm " +
                "JOIN members m ON gm.member_id = m.id " +
                "WHERE gm.group_id = ?";

        try (Connection conn = getConnection()) {
            int groupId = -1;
            double baseContributionAmount = 0.0;

            // Step 1: Resolve Core Group ID
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

            // Step 2: Fetch Base Target Contribution Level
            try (PreparedStatement stmt = conn.prepareStatement(configSql)) {
                stmt.setString(1, equbGroupName);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        baseContributionAmount = rs.getDouble("amount");
                    }
                }
            }
            System.out.println("--> Base Target Contribution Amount per cycle: " + baseContributionAmount + " ETB");

            // Step 3: Parse dynamic user rows and compile absolute combined sums
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

                        System.out.println("    └─ Member: " + memberName + " | Total Contributed: " + memberTotalPaid + " ETB | Cycles: " + cyclesPaid);

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

            System.out.println("--> [SUCCESS] Total Members Found: " + report.totalMembers);
            System.out.println("--> [SUCCESS] Dynamic Combined Aggregate Total Money: " + report.totalCollected + " ETB");
            System.out.println("===============================================================\n");

        } catch (SQLException e) {
            System.err.println("--> [CRITICAL ERROR] Failed parsing report data queries inside DAO layer:");
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
     * Maps to core 'groups' relational tracking, 'edir_groups' metadata, and normalized collections.
     */


    @Override
    public EdirReport fetchEdirReportData(String edirGroupName) {
        EdirReport report = new EdirReport();
        report.emergencyRows = new ArrayList<>();
        int creatorId = getAuthenticatedSessionUserId();

        System.out.println("\n================= 🏛️ EDIR SCHEMA-ALIGNED REPORT LAYER =================");
        System.out.println("--> [START] Target Group Name: [" + edirGroupName + "] | Logged In User ID: " + creatorId);

        // 1. Resolve Group ID safely with isolated session validation
        String baseGroupSql = "SELECT id FROM groups WHERE UPPER(TRIM(name)) = UPPER(TRIM(?)) AND created_by = ? AND UPPER(TRIM(type)) = 'EDIR' LIMIT 1";

        // 2. FINANCIAL LEDGER QUERY: Calculates cash flowing strictly into or out of the financial table
        String ledgerSql = "SELECT t.date::text, m.full_name, t.type, t.description, t.amount " +
                "FROM transactions t " +
                "JOIN members m ON t.member_id = m.id " +
                "WHERE t.group_id = ? " +
                "ORDER BY t.date DESC";

        // 3. STATISTICAL HEADCOUNT QUERY: Queries your actual 'emergency_cases' table for clean operational statistics
        String statsSql = "SELECT " +
                "COUNT(*) as total_cases, " +
                "COUNT(CASE WHEN UPPER(TRIM(status)) = 'APPROVED' THEN 1 END) as approved_cases, " +
                "COUNT(CASE WHEN UPPER(TRIM(status)) = 'PENDING' OR UPPER(TRIM(status)) LIKE '%PENDING%' THEN 1 END) as pending_cases " +
                "FROM emergency_cases WHERE group_id = ?";

        try (Connection conn = getConnection()) {
            int groupId = -1;

            // Step 1: Resolve Core Group ID
            try (PreparedStatement stmt = conn.prepareStatement(baseGroupSql)) {
                stmt.setString(1, edirGroupName);
                stmt.setInt(2, creatorId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        groupId = rs.getInt("id");
                    }
                }
            }

            System.out.println("--> Resolved Edir Group ID from Database: " + groupId);

            if (groupId == -1) {
                System.out.println("--> [ABORT] No matching authenticated Edir group entry found.");
                return null;
            }

            // Step 2: Extract Clean Financial Metrics from Ledger (Transactions Table)
            double dynamicRunningContributionsTotal = 0.0;
            double dynamicRunningPayoutsTotal = 0.0;

            try (PreparedStatement stmt = conn.prepareStatement(ledgerSql)) {
                stmt.setInt(1, groupId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String dateStr = rs.getString("date");
                        String shortDate = (dateStr != null && dateStr.length() > 10) ? dateStr.substring(0, 10) : dateStr;
                        String memberName = rs.getString("full_name");
                        String rawType = rs.getString("type") != null ? rs.getString("type").toUpperCase().trim() : "";
                        double amount = rs.getDouble("amount");

                        String uiDisplayType = rs.getString("type");
                        String uiDisplayStatus = "Processed";

                        // Handle financial inputs
                        if (rawType.equals("CONTRIBUTION") || rawType.equals("REGISTRATION") || rawType.equals("DEPOSIT") || rawType.equals("IN")) {
                            dynamicRunningContributionsTotal += amount;
                        }
                        // Handle actual financial outlays (PAYOUT / OUT)
                        // Note: Ignoring APPROVED_CLAIM here if it acts as a duplicate row entry in your legacy dataset
                        else if (rawType.equals("PAYOUT") || rawType.equals("OUT")) {
                            dynamicRunningPayoutsTotal += amount;
                            uiDisplayStatus = "Approved & Closed";
                            if (uiDisplayType == null || uiDisplayType.trim().isEmpty()) {
                                uiDisplayType = "Emergency Payout";
                            }
                        }
                        else if (rawType.contains("CLAIM") || rawType.contains("EMERGENCY")) {
                            // If your business rule saves claims into transactions, count them safely
                            if (!rawType.contains("APPROVED_CLAIM")) { // Skips the duplicate logging identifier
                                dynamicRunningPayoutsTotal += amount;
                            }
                            uiDisplayStatus = rawType.contains("PENDING") ? "🚨 Pending Review" : "Approved & Closed";
                        }
                        else {
                            // Default Fallback
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

            // Step 3: Populate Headcount Statistics directly from 'emergency_cases' table
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

            // Step 4: Map final calculations to UI parameters
            report.totalContributions = dynamicRunningContributionsTotal;
            report.remainingFundBalance = dynamicRunningContributionsTotal - dynamicRunningPayoutsTotal;

            System.out.println("--> [FINAL RESULTS - EDIR SCHEMA VERIFIED]");
            System.out.println("    ├── Total Contributions Pool: " + report.totalContributions + " ETB");
            System.out.println("    ├── Total Absolute Payouts: " + dynamicRunningPayoutsTotal + " ETB");
            System.out.println("    ├── Remaining Balanced Fund: " + report.remainingFundBalance + " ETB");
            System.out.println("    └── Cases Headcount: " + report.totalEmergencyCases + " (Pending: " + report.pendingEmergencies + ")");
            System.out.println("===============================================================\n");

        } catch (SQLException e) {
            System.err.println("--> [CRITICAL ERROR] Failed schema calculations inside ReportDAOImpl:");
            e.printStackTrace();
        }
        return report;
    }
    @Override
    public SystemReport fetchSystemReportData() {
        SystemReport report = new SystemReport();
        report.recentTransactions = new ArrayList<>();
        int creatorId = getAuthenticatedSessionUserId();

        // FIXED: Re-mapped to run metrics directly against normalized structural relationships instead of view files
        String summarySql = "SELECT " +
                "(SELECT COUNT(DISTINCT gm.member_id) FROM group_members gm JOIN groups g ON gm.group_id = g.id WHERE g.created_by = ?) as total_m, " +
                "(SELECT COUNT(*) FROM groups WHERE created_by = ?) as total_g, " +
                "(SELECT COUNT(*) FROM transactions t JOIN groups g ON t.group_id = g.id WHERE g.created_by = ?) as total_t, " +
                "COALESCE((SELECT SUM(t.amount) FROM transactions t JOIN groups g ON t.group_id = g.id WHERE UPPER(TRIM(t.type)) IN ('CONTRIBUTION', 'DEPOSIT', 'IN') AND g.created_by = ?), 0) as total_cash";

        String recentTxSql = "SELECT t.date::text, t.amount, COALESCE(g.name, 'Global System') as group_name, t.type, t.description " +
                "FROM transactions t " +
                "JOIN groups g ON t.group_id = g.id " +
                "WHERE g.created_by = ? " +
                "ORDER BY t.date DESC LIMIT 10";

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
                        row.date = rs.getString("date") != null && rs.getString("date").length() > 16 ? rs.getString("date").substring(0, 16) : rs.getString("date");
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