package dao;

import model.ReportDataModels.*;
import java.util.List;

public interface ReportDAO {
    // Add these missing declarations to your interface:
    MemberReport fetchMemberReportData(String searchKeyword);
    List<String> fetchEqubGroupNames();
    EqubReport fetchEqubReportData(String equbGroupName);
    List<String> fetchEdirGroupNames();
    EdirReport fetchEdirReportData(String edirGroupName);
    SystemReport fetchSystemReportData();
}