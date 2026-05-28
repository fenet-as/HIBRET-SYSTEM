package service;

import model.ReportDataModels.*;
import java.util.List;

public interface ReportService {
    MemberReport getMemberReportData(String searchKeyword);
    List<String> getAllEqubGroups();
    EqubReport getEqubReportData(String equbGroupName);
    List<String> getAllEdirGroups();
    EdirReport getEdirReportData(String edirGroupName);
    SystemReport getSystemReportData();
}