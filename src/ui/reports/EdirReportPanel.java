package ui.reports;

import service.ReportService;
import model.ReportDataModels.EdirReport;
import model.ReportDataModels.EdirEmergencyRow;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class EdirReportPanel extends JPanel {
    private final ReportService reportService;
    private final DefaultTableModel tableModel;
    private final JComboBox<String> dropdownFilterOptions;
    private final JLabel lblContributions;
    private final JLabel lblCases;
    private final JLabel lblApproved;
    private final JLabel lblPending;
    private final JLabel lblBalance;

    public EdirReportPanel(ReportHomePanel subCoordinator, ReportService reportService) {
        this.reportService = reportService;

        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(25, 35, 40, 35));

        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);
        headerRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTitle = new JLabel("❤️ Edir Report Panel");
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
        add(Box.createVerticalStrut(15));

        JPanel selectorRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        selectorRow.setOpaque(false);
        selectorRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lblFilterLabel = new JLabel("Select Edir group");
        lblFilterLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblFilterLabel.setForeground(new Color(101, 53, 15));

        dropdownFilterOptions = new JComboBox<>();
        dropdownFilterOptions.setFont(new Font("SansSerif", Font.PLAIN, 13));
        selectorRow.add(lblFilterLabel);
        selectorRow.add(dropdownFilterOptions);
        add(selectorRow);
        add(Box.createVerticalStrut(20));

        JPanel metricsGrid = new JPanel(new GridLayout(1, 5, 12, 0)) {
            @Override
            public Dimension getMaximumSize() {
                return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
            }
        };
        metricsGrid.setOpaque(false);
        metricsGrid.setAlignmentX(Component.LEFT_ALIGNMENT);

        metricsGrid.add(createMiniStatCard("Total contributions", lblContributions = new JLabel("- birr"), new Color(46, 117, 59)));
        metricsGrid.add(createMiniStatCard("Total emergency cases", lblCases = new JLabel("-"), new Color(101, 53, 15)));
        metricsGrid.add(createMiniStatCard("Approved emergencies", lblApproved = new JLabel("-"), new Color(101, 53, 15)));
        metricsGrid.add(createMiniStatCard("Pending emergencies", lblPending = new JLabel("-"), new Color(184, 91, 23)));
        metricsGrid.add(createMiniStatCard("Remaining fund Balance", lblBalance = new JLabel("- birr"), new Color(46, 117, 59)));
        add(metricsGrid);
        add(Box.createVerticalStrut(25));

        JLabel lblTableTitle = new JLabel("Emergency history table");
        lblTableTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTableTitle.setForeground(new Color(101, 53, 15));
        lblTableTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(lblTableTitle);
        add(Box.createVerticalStrut(10));

        String[] headers = {"Date", "Member", "Type", "Status", "Amount"};
        tableModel = new DefaultTableModel(null, headers) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(tableModel);
        table.setRowHeight(32);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(scrollPane);

        // Populate dropdown from Database
        List<String> groups = reportService.getAllEdirGroups();
        for (String group : groups) {
            dropdownFilterOptions.addItem(group);
        }

        dropdownFilterOptions.addActionListener(e -> loadData((String) dropdownFilterOptions.getSelectedItem()));

        if (dropdownFilterOptions.getItemCount() > 0) {
            loadData((String) dropdownFilterOptions.getSelectedItem());
        }
    }

    private void loadData(String groupName) {
        if (groupName == null) return;
        EdirReport report = reportService.getEdirReportData(groupName);
        tableModel.setRowCount(0);

        if (report != null) {
            lblContributions.setText(String.format("%,.2f birr", report.totalContributions));
            lblCases.setText(String.valueOf(report.totalEmergencyCases));
            lblApproved.setText(String.valueOf(report.approvedEmergencies));
            lblPending.setText(String.valueOf(report.pendingEmergencies));
            lblBalance.setText(String.format("%,.2f birr", report.remainingFundBalance));

            if (report.emergencyRows != null) {
                for (EdirEmergencyRow row : report.emergencyRows) {
                    tableModel.addRow(new Object[]{row.date, row.memberName, row.type, row.status, String.format("%,.2f birr", row.amount)});
                }
            }
        }
    }

    private JPanel createMiniStatCard(String label, JLabel lblValueRef, Color textValueColor) {
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
        card.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));
        card.setPreferredSize(new Dimension(150, 75));

        JLabel lblMsg = new JLabel(label);
        lblMsg.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblMsg.setForeground(new Color(130, 125, 115));

        lblValueRef.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblValueRef.setForeground(textValueColor);

        card.add(lblMsg);
        card.add(Box.createVerticalStrut(4));
        card.add(lblValueRef);
        return card;
    }
}