package ui.reports;

import service.ReportService;
import model.ReportDataModels.SystemReport;
import model.ReportDataModels.TransactionRow;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class SystemReportPanel extends JPanel {
    private final ReportService reportService;
    private final DefaultTableModel tableModel;
    private final JPanel metricsGrid;

    public SystemReportPanel(ReportHomePanel subCoordinator, ReportService reportService) {
        this.reportService = reportService;

        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(25, 35, 40, 35));

        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);
        headerRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTitle = new JLabel("📊 System Report Panel");
        lblTitle.setFont(new Font("Serif", Font.BOLD, 30));
        lblTitle.setForeground(new Color(101, 53, 15));
        headerRow.add(lblTitle, BorderLayout.WEST);

        JButton btnBack = new JButton("⬅ Back to Reports");
        btnBack.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnBack.setForeground(new Color(130, 90, 40));
        btnBack.setContentAreaFilled(false);
        btnBack.setBorderPainted(false);
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.addActionListener(e -> subCoordinator.navigateToSubReport("HomeSelection"));
        headerRow.add(btnBack, BorderLayout.EAST);
        add(headerRow);
        add(Box.createVerticalStrut(25));

        metricsGrid = new JPanel(new GridLayout(1, 5, 14, 0)) {
            @Override
            public Dimension getMaximumSize() {
                return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
            }
        };
        metricsGrid.setOpaque(false);
        metricsGrid.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(metricsGrid);
        add(Box.createVerticalStrut(30));

        JLabel lblTableTitle = new JLabel("Last 10 transactions table");
        lblTableTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTableTitle.setForeground(new Color(101, 53, 15));
        lblTableTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(lblTableTitle);
        add(Box.createVerticalStrut(10));

        String[] headers = {"Date", "Amount", "Group", "Type", "Description"};
        tableModel = new DefaultTableModel(null, headers) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(tableModel);
        table.setRowHeight(32);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(scrollPane);

        loadData();
    }

    private void loadData() {
        SystemReport report = reportService.getSystemReportData();
        tableModel.setRowCount(0);
        metricsGrid.removeAll();

        if (report != null) {
            metricsGrid.add(createMiniStatCard("Total Users", String.valueOf(report.totalUsers), new Color(101, 53, 15)));
            metricsGrid.add(createMiniStatCard("Total Members", String.valueOf(report.totalMembers), new Color(101, 53, 15)));
            metricsGrid.add(createMiniStatCard("Total Groups (Equb + Edir)", String.valueOf(report.totalGroups), new Color(46, 117, 59)));
            metricsGrid.add(createMiniStatCard("Total Transactions", String.valueOf(report.totalTransactions), new Color(101, 53, 15)));
            metricsGrid.add(createMiniStatCard("Total Money in system", String.format("%,.0f birr", report.totalMoneyInSystem), new Color(46, 117, 59)));

            if (report.recentTransactions != null) {
                for (TransactionRow tx : report.recentTransactions) {
                    tableModel.addRow(new Object[]{tx.date, String.format("%,.2f birr", tx.amount), tx.groupName, tx.type, tx.description});
                }
            }
        }

        metricsGrid.revalidate();
        metricsGrid.repaint();
    }

    private JPanel createMiniStatCard(String label, String value, Color textValueColor) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 2, 12, 12);
                g2.setColor(new Color(230, 225, 212));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 2, 12, 12);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        card.setPreferredSize(new Dimension(150, 75));

        JLabel lblMsg = new JLabel("<html>" + label + "</html>");
        lblMsg.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblMsg.setForeground(new Color(130, 125, 115));

        JLabel lblVal = new JLabel(value);
        lblVal.setFont(new Font("SansSerif", Font.BOLD, 17));
        lblVal.setForeground(textValueColor);

        card.add(lblMsg);
        card.add(Box.createVerticalStrut(4));
        card.add(lblVal);
        return card;
    }
}