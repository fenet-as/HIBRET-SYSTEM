package dao;

import model.EdirGroup;
import model.EmergencyCase;
import model.Transaction; // Reusing your existing transaction model!
import java.util.List;

public interface EdirDAO {
    // Group Management
    void createEdirGroup(String name, double contribution);
    List<EdirGroup> getAllEdirGroups();
    void deleteEdirGroup(int id);

    // Core Metrics Mapping
    Object[] getEdirGroupMetrics(int edirGroupId);

    // Ledger Operations
    void recordEdirContribution(int memberId, int groupId, double amount);
    List<Transaction> getRecentContributionsForGroup(int groupId);

    // Emergency Case Pipelines
    void registerEmergencyCase(int memberId, int groupId, String type, double amount, String description);
    List<EmergencyCase> getPendingPayoutCasesForGroup(int groupId);
    void disburseEmergencyFunds(EmergencyCase ec);
}