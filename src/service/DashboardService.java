package service;

import model.DashboardMetrics;

public interface DashboardService {
    /**
     * Aggregates real-time architectural metrics across Equb circles,
     * Edir groups, members, and pending validation pipeline queues.
     * * @return A hydrated data carrier POJO containing active database calculations.
     */
    DashboardMetrics getSystemSummary(int userId);
}