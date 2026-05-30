package service.impl;

import dao.EdirDAO;
import dao.impl.EdirDAOImpl;
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
    public boolean createGroup(String groupName, double monthlyFee, double initialPool, String rules, int creatorUserId) {
        // ✅ Now cleanly passes the tracking ID to the DAO
        return edirDAO.createGroup(groupName, monthlyFee, initialPool, rules, creatorUserId);
    }

    @Override
    public boolean deleteGroup(String groupName) {
        return edirDAO.deleteGroup(groupName);
    }

    @Override
    public Map<String, String> getGroupDetails(String groupName) {
        return edirDAO.getGroupDetails(groupName);
    }

    @Override
    public List<Map<String, String>> getMembersByGroup(String groupName) {
        return edirDAO.getMembersByGroup(groupName);
    }

    @Override
    public boolean addMemberToGroup(String groupName, String fullName, String phone) {
        return edirDAO.addMemberToGroup(groupName, fullName, phone);
    }

    @Override
    public boolean recordContribution(String groupName, String memberName, String month, double amount, String receiptNo) {
        return edirDAO.recordContribution(groupName, memberName, month, amount, receiptNo);
    }

    @Override
    public List<Map<String, String>> getRecentContributions(String groupName) {
        return edirDAO.getRecentContributions(groupName);
    }

    @Override
    public boolean registerEmergencyCase(String groupName, String memberName, String type, double amount, String description) {
        return edirDAO.registerEmergencyCase(groupName, memberName, type, amount, description);
    }

    @Override
    public boolean authorizePayout(String groupName, String caseTxId, double amount, String approvedBy, String notes) {
        return edirDAO.authorizePayout(groupName, caseTxId, amount, approvedBy, notes);
    }

    @Override
    public List<Map<String, String>> getPendingClaimsByGroup(String groupName) {
        // Safe check block: If mapped via EdirDAO interface pattern, forward directly.
        // Otherwise, downcast explicitly to the compiled EdirDAOImpl structural class.
        if (edirDAO instanceof EdirDAOImpl) {
            return ((EdirDAOImpl) edirDAO).getPendingClaimsByGroup(groupName);
        }
        return edirDAO.getPendingClaimsByGroup(groupName);
    }

    @Override
    public List<Map<String, String>> getGroupTransactionLedger(String groupName) {
        // If your EdirDAO interface has this method, call it directly:
        return edirDAO.getGroupTransactionLedger(groupName);
    }

    @Override
    public List<Map<String, String>> getEdirGroupsForUser(int userId) {
        // This delegates the database call straight to your updated DAO implementation
        return edirDAO.getEdirGroupsForUser(userId);
    }


    @Override
    public double getActualAvailableRoundPool(int groupId) {
        return edirDAO.getActualAvailableRoundPool(groupId);// or however your DAO is named
    }

    @Override
    public double getGroupBalance(String groupName) {
        // This delegates the live transactional calculation directly to your DAO layer
        return edirDAO.getGroupBalance(groupName);
    }

    @Override
    public void clearGroupTransactions(String groupName) {
        edirDAO.clearGroupTransactions(groupName);
    }


    @Override
    public boolean removeMemberFromGroup(String groupName, String memberName) {
        // Delegates execution safely down to the implemented DAO data layer pipeline
        return edirDAO.removeMemberFromGroup(groupName, memberName);
    }


}