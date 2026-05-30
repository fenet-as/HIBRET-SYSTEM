package service.impl;

import dao.ReportDAO;
import model.ReportDataModels.*;
import service.ReportService;
import java.util.List;

public class ReportServiceImpl implements ReportService {
    private final ReportDAO reportDAO;

    public ReportServiceImpl(ReportDAO reportDAO) {
        this.reportDAO = reportDAO;
    }

    /**
     * NEW OPERATION ALIGNED WITH REFACTORED WORKSPACE:
     * Pulls the complete array list of members managed under the logged-in user.
     */
    @Override
    public List<String> getAllManagedMemberNames() {
        // FIXED: Changed 'reportDAOImpl' to 'reportDAO' to match your class variable
        return reportDAO.fetchAllManagedMemberNames();
    }

    @Override
    public MemberReport getMemberReportData(String searchKeyword) {
        // Fallback filter to map active input string instances
        if (searchKeyword == null || searchKeyword.trim().isEmpty() || searchKeyword.equals("🔍 Search Member...")) {
            searchKeyword = "";
        }
        return reportDAO.fetchMemberReportData(searchKeyword.trim());
    }

    @Override
    public List<String> getAllEqubGroups() {
        return reportDAO.fetchEqubGroupNames();
    }

    @Override
    public EqubReport getEqubReportData(String equbGroupName) {
        if (equbGroupName == null || equbGroupName.trim().isEmpty()) return null;
        return reportDAO.fetchEqubReportData(equbGroupName.trim());
    }

    @Override
    public List<String> getAllEdirGroups() {
        return reportDAO.fetchEdirGroupNames();
    }

    @Override
    public EdirReport getEdirReportData(String edirGroupName) {
        if (edirGroupName == null || edirGroupName.trim().isEmpty()) return null;
        return reportDAO.fetchEdirReportData(edirGroupName.trim());
    }

    @Override
    public SystemReport getSystemReportData() {
        return reportDAO.fetchSystemReportData();
    }
}