package service;

import model.Group;
import model.Member;
import model.Transaction;
import java.util.List;

public interface EqubService {
    // Group Context Layout Handlers
    void createEqubGroup(String name, double contributionAmount, int creatorUserId);
    List<Group> getAllEqubGroups();
    List<Group> getEqubGroupsForUser(int userId);
    void deleteEqubGroup(int groupId);

    // Member Matrix Operations
    List<Member> getMembersInGroup(int groupId);
    void addMemberToGroup(int groupId, int memberId);
    List<Member> getAllSystemMembers();
    int createNewSystemMember(Member member);

    // Core Transaction Ledger Flows
    void recordPayment(int groupId, int memberId, double amount, String date, String note);
    void recordPayout(int groupId, int memberId, double amount, String date, String description);
    List<Transaction> getRecentPaymentsForGroup(int groupId);
    boolean reverseTransaction(int transactionId);

    // Rotation & Cycle Track Engines
    Member triggerRandomRotationalDraw(int groupId);
    int getCompletedRoundsCount(int groupId);
    boolean hasEligibleUnpaidMembers(int groupId);
    double getActualAvailableRoundPool(int groupId);

    // ✅ NEW ADDITIONS: Structural Validation Hooks
    boolean haveAllMembersPaidCurrentRound(int groupId);
    boolean clearAllTransactionsForGroup(int groupId);


}