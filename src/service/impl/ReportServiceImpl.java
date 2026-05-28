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

    @Override
    public MemberReport getMemberReportData(String searchKeyword) {
        // Fallback to avoid empty initial displays
        if (searchKeyword == null || searchKeyword.trim().isEmpty() || searchKeyword.equals("🔍 Search Member...")) {
            searchKeyword = "";
        }
        return reportDAO.fetchMemberReportData(searchKeyword);
    }

    @Override
    public List<String> getAllEqubGroups() {
        return reportDAO.fetchEqubGroupNames();
    }

    @Override
    public EqubReport getEqubReportData(String equbGroupName) {
        return reportDAO.fetchEqubReportData(equbGroupName);
    }

    @Override
    public List<String> getAllEdirGroups() {
        return reportDAO.fetchEdirGroupNames();
    }

    @Override
    public EdirReport getEdirReportData(String edirGroupName) {
        return reportDAO.fetchEdirReportData(edirGroupName);
    }

    @Override
    public SystemReport getSystemReportData() {
        return reportDAO.fetchSystemReportData();
    }
}