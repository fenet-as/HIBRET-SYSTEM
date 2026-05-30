package service.impl;

import dao.DashboardDAO;
import dao.impl.DashboardDAOImpl;
import model.DashboardMetrics;
import service.DashboardService;

public class DashboardServiceImpl implements DashboardService {
    private final DashboardDAO dashboardDAO;

    /**
     * Constructor maps direct operational routing over to the Data Access Object layer.
     */
    public DashboardServiceImpl() {
        this.dashboardDAO = new DashboardDAOImpl();
    }

    @Override
    public DashboardMetrics getSystemSummary(int userId) {
        return dashboardDAO.getSystemSummary(userId); // Forwards user filter context down to SQL execution layer
    }
}