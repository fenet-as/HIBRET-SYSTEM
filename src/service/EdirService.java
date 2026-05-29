package service;

import java.util.List;
import model.EdirGroup;
import model.Transaction;

public interface EdirService {
    // Group configuration rules
    void createEdirGroup(String name, double contribution);
    List<EdirGroup> getAllEdirGroups();
    void deleteEdirGroup(int id);

    // Member management rules
    void addMemberToGroup(String fullName, int groupId);

    // Dashboard dynamic matrix metrics
    Object[] getEdirGroupMetrics(int groupId);
    List<Transaction> getRecentContributionsForGroup(int groupId);

    // Financial ledger transaction records matching workflow buttons
    void recordContribution(int memberId, int groupId, double amount); // ⭐ This clears your compiler error!
    void registerEmergencyCase(int memberId, int groupId, String type, double amount, String desc);
    void disburseEdirFunds(int groupId, double amount, String rationale);
    void disburseEmergencyFunds(model.EmergencyCase chosenCase);

    List<model.EmergencyCase> getPendingPayoutCasesForGroup(int groupId);
}