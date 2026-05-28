package dao;

import model.Group;
import model.Member;
import model.Transaction;
import java.util.List;

public interface EqubDAO {
    void createEqubGroup(String name, double contributionAmount);
    List<Group> getAllEqubGroups();
    void deleteEqubGroup(int groupId);
    List<Member> getMembersInGroup(int groupId);
    void addMemberToGroup(int groupId, int memberId);
    void recordPayment(int groupId, int memberId, double amount, String date, String note);
    void recordPayout(int groupId, int memberId, double amount, String date, String description);
    List<Transaction> getRecentPaymentsForGroup(int groupId);
    Member triggerRandomRotationalDraw(int groupId);
    int createNewSystemMember(Member member);
}