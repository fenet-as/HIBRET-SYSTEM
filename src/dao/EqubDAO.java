package dao;

import model.Group;
import model.Member;
import model.Transaction;
import java.util.List;

public interface EqubDAO {
    void createEqubGroup(String name, double contributionAmount, int creatorUserId);
    List<Group> getAllEqubGroups();
    void deleteEqubGroup(int groupId);
    List<Member> getMembersInGroup(int groupId);
    void addMemberToGroup(int groupId, int memberId);
    void recordPayment(int groupId, int memberId, double amount, String date, String note);
    void recordPayout(int groupId, int memberId, double amount, String date, String description);
    List<Transaction> getRecentPaymentsForGroup(int groupId);
    Member triggerRandomRotationalDraw(int groupId);
    int createNewSystemMember(Member member);
    List<Group> getEqubGroupsForUser(int userId);
    List<Member> getAllSystemMembers(); // Ensured parity with Service tier signature requirements

    int getCompletedRoundsCount(int groupId);
    boolean hasEligibleUnpaidMembers(int groupId);
    double getActualAvailableRoundPool(int groupId);
    boolean reverseTransaction(int transactionId);

    boolean haveAllMembersPaidCurrentRound(int groupId);
    boolean clearAllTransactionsForGroup(int groupId);
    boolean removeMemberFromGroup(int groupId, int memberId);


}