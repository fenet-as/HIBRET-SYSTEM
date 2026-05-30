package service;

import java.util.List;
import java.util.Map;

public interface EdirService {

    List<Map<String, String>> getAllGroups();

    List<Map<String, String>> getEdirGroupsForUser(int userId);

    boolean createGroup(String groupName, double monthlyFee, double initialPool, String rules, int creatorUserId);

    // ✅ ALL CRITICAL ACCESS METHODS UPDATED FROM 'String groupName' TO 'int groupId'
    boolean deleteGroup(int groupId);

    Map<String, String> getGroupDetails(int groupId);

    List<Map<String, String>> getMembersByGroup(int groupId);

    boolean addMemberToGroup(int groupId, String fullName, String phone);

    boolean recordContribution(int groupId, String memberName, String month, double amount, String receiptNo);

    List<Map<String, String>> getRecentContributions(int groupId);

    boolean registerEmergencyCase(int groupId, String memberName, String type, double amount, String description);

    boolean authorizePayout(int groupId, String caseTxId, double amount, String approvedBy, String notes);

    /**
     * Fetches unresolved emergency records ('PENDING') from the ledger.
     * Tied explicitly to the Distribute Payout screen drop-down lookup grid.
     */
    List<Map<String, String>> getPendingClaimsByGroup(int groupId);

    List<Map<String, String>> getGroupTransactionLedger(int groupId);

    double getActualAvailableRoundPool(int groupId);

    double getGroupBalance(int groupId);

    void clearGroupTransactions(int groupId);

    boolean removeMemberFromGroup(int groupId, String memberName);
}