package service.impl;

import dao.EqubDAO;
import model.Group;
import model.Member;
import model.Transaction;
import service.EqubService;
import java.util.List;

public class EqubServiceImpl implements EqubService {

    private final EqubDAO equbDAO;

    // Direct Constructor Injection for the Equb DAO Layer
    public EqubServiceImpl(EqubDAO equbDAO) {
        this.equbDAO = equbDAO;
    }

    @Override
    public void createEqubGroup(String name, double contributionAmount, int creatorUserId) {
        equbDAO.createEqubGroup(name, contributionAmount, creatorUserId);
    }

    @Override
    public List<Group> getAllEqubGroups() {
        return equbDAO.getAllEqubGroups();
    }

    @Override
    public List<Group> getEqubGroupsForUser(int userId) {
        return equbDAO.getEqubGroupsForUser(userId);
    }

    @Override
    public void deleteEqubGroup(int groupId) {
        equbDAO.deleteEqubGroup(groupId);
    }

    @Override
    public List<Member> getMembersInGroup(int groupId) {
        return equbDAO.getMembersInGroup(groupId);
    }

    @Override
    public void addMemberToGroup(int groupId, int memberId) {
        equbDAO.addMemberToGroup(groupId, memberId);
    }

    @Override
    public List<Member> getAllSystemMembers() {
        return equbDAO.getAllSystemMembers();
    }

    @Override
    public int createNewSystemMember(Member member) {
        return equbDAO.createNewSystemMember(member);
    }

    @Override
    public void recordPayment(int groupId, int memberId, double amount, String date, String note) {
        equbDAO.recordPayment(groupId, memberId, amount, date, note);
    }

    @Override
    public void recordPayout(int groupId, int memberId, double amount, String date, String description) {
        equbDAO.recordPayout(groupId, memberId, amount, date, description);
    }

    @Override
    public List<Transaction> getRecentPaymentsForGroup(int groupId) {
        return equbDAO.getRecentPaymentsForGroup(groupId);
    }

    @Override
    public boolean reverseTransaction(int transactionId) {
        return equbDAO.reverseTransaction(transactionId);
    }

    @Override
    public Member triggerRandomRotationalDraw(int groupId) {
        return equbDAO.triggerRandomRotationalDraw(groupId);
    }

    @Override
    public int getCompletedRoundsCount(int groupId) {
        return equbDAO.getCompletedRoundsCount(groupId);
    }

    @Override
    public boolean hasEligibleUnpaidMembers(int groupId) {
        return equbDAO.hasEligibleUnpaidMembers(groupId);
    }

    @Override
    public double getActualAvailableRoundPool(int groupId) {
        return equbDAO.getActualAvailableRoundPool(groupId);
    }

    // ✅ NEW INTERCEPT VALIDATOR: Routes down directly to your database tracker logic
    @Override
    public boolean haveAllMembersPaidCurrentRound(int groupId) {
        return equbDAO.haveAllMembersPaidCurrentRound(groupId);
    }

    // ✅ NEW PURGE ENGINES: Connects UI Reset Action payload to your raw DB state
    @Override
    public boolean clearAllTransactionsForGroup(int groupId) {
        return equbDAO.clearAllTransactionsForGroup(groupId);
    }
}