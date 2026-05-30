package service.impl;

import dao.EdirDAO;
import service.EdirService;

import java.util.List;
import java.util.Map;

public class EdirServiceImpl implements EdirService {
    private final EdirDAO edirDAO;

    public EdirServiceImpl(EdirDAO edirDAO) {
        this.edirDAO = edirDAO;
    }

    @Override
    public List<Map<String, String>> getAllGroups() {
        return edirDAO.getAllGroups();
    }

    @Override
    public List<Map<String, String>> getEdirGroupsForUser(int userId) {
        return edirDAO.getEdirGroupsForUser(userId);
    }

    @Override
    public boolean createGroup(String groupName, double monthlyFee, double initialPool, String rules, int creatorUserId) {
        // Group creation still takes a String name because it is creating a NEW database entry row
        return edirDAO.createGroup(groupName, monthlyFee, initialPool, rules, creatorUserId);
    }

    // ✅ ALL METHODS BELOW ROUTE SECURELY VIA 'int groupId' TO MATCH THE DAO LAYER
    @Override
    public boolean deleteGroup(int groupId) {
        return edirDAO.deleteGroup(groupId);
    }

    @Override
    public Map<String, String> getGroupDetails(int groupId) {
        return edirDAO.getGroupDetails(groupId);
    }

    @Override
    public List<Map<String, String>> getMembersByGroup(int groupId) {
        return edirDAO.getMembersByGroup(groupId);
    }

    @Override
    public boolean addMemberToGroup(int groupId, String fullName, String phone) {
        return edirDAO.addMemberToGroup(groupId, fullName, phone);
    }

    @Override
    public boolean recordContribution(int groupId, String memberName, String month, double amount, String receiptNo) {
        return edirDAO.recordContribution(groupId, memberName, month, amount, receiptNo);
    }

    @Override
    public List<Map<String, String>> getRecentContributions(int groupId) {
        return edirDAO.getRecentContributions(groupId);
    }

    @Override
    public List<Map<String, String>> getGroupTransactionLedger(int groupId) {
        return edirDAO.getGroupTransactionLedger(groupId);
    }

    @Override
    public boolean registerEmergencyCase(int groupId, String memberName, String type, double amount, String description) {
        return edirDAO.registerEmergencyCase(groupId, memberName, type, amount, description);
    }

    @Override
    public List<Map<String, String>> getPendingClaimsByGroup(int groupId) {
        // CLEANED: No messy downcasting needed since it is now natively defined in the interface layer
        return edirDAO.getPendingClaimsByGroup(groupId);
    }

    @Override
    public boolean authorizePayout(int groupId, String caseTxId, double amount, String approvedBy, String notes) {
        return edirDAO.authorizePayout(groupId, caseTxId, amount, approvedBy, notes);
    }

    @Override
    public double getActualAvailableRoundPool(int groupId) {
        return edirDAO.getActualAvailableRoundPool(groupId);
    }

    @Override
    public double getGroupBalance(int groupId) {
        return edirDAO.getGroupBalance(groupId);
    }

    @Override
    public void clearGroupTransactions(int groupId) {
        edirDAO.clearGroupTransactions(groupId);
    }

    @Override
    public boolean removeMemberFromGroup(int groupId, String memberName) {
        return edirDAO.removeMemberFromGroup(groupId, memberName);
    }
}