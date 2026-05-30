package dao;

import model.ReportDataModels.*;
import java.util.List;

public interface ReportDAO {
    // Crucial listing operation to populate JList view components
    List<String> fetchAllManagedMemberNames();

    MemberReport fetchMemberReportData(String searchKeyword);
    List<String> fetchEqubGroupNames();
    EqubReport fetchEqubReportData(String equbGroupName);
    List<String> fetchEdirGroupNames();
    EdirReport fetchEdirReportData(String edirGroupName);
    SystemReport fetchSystemReportData();


}