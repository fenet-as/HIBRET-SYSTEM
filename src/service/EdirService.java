package service;

import java.util.List;
import java.util.Map;

public interface EdirService {

    List<Map<String, String>> getAllGroups();

    boolean createGroup(String groupName, double monthlyFee, double initialPool, String rules);

    boolean deleteGroup(String groupName);

    Map<String, String> getGroupDetails(String groupName);

    List<Map<String, String>> getMembersByGroup(String groupName);

    boolean addMemberToGroup(String groupName, String fullName, String phone);

    boolean recordContribution(String groupName, String memberName, String month, double amount, String receiptNo);

    List<Map<String, String>> getRecentContributions(String groupName);

    boolean registerEmergencyCase(String groupName, String memberName, String type, double amount, String description);

    boolean authorizePayout(String groupName, String caseTxId, double amount, String approvedBy, String notes);

    /**
     * Fetches unresolved emergency records ('PENDING_CLAIM') from the ledger.
     * Tied explicitly to the Distribute Payout screen drop-down lookup grid.
     */
    List<Map<String, String>> getPendingClaimsByGroup(String groupName);
}