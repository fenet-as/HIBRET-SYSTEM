package dao;
import model.DashboardMetrics;

public interface DashboardDAO {

    DashboardMetrics getSystemSummary(int userId);
}