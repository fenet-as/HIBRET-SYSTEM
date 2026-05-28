package ui.reports;

import service.ReportService;
import model.ReportDataModels.EqubReport;
import model.ReportDataModels.EqubMemberRow;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class EqubReportPanel extends JPanel {
    private final ReportService reportService;
    private final DefaultTableModel tableModel;
    private final JComboBox<String> dropdownFilterOptions;
    private final JLabel lblTotalMembersVal;
    private final JLabel lblTotalMoneyVal;
    private final JLabel lblCurrentCycleVal;
    private final JLabel lblNextPayoutVal;

    public EqubReportPanel(ReportHomePanel subCoordinator, ReportService reportService) {
        this.reportService = reportService;

        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(25, 35, 40, 35));

        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);
        headerRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTitle = new JLabel("🏛️ Equb Report Panel");
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
        JLabel lblFilterLabel = new JLabel("Select Equb group");
        lblFilterLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblFilterLabel.setForeground(new Color(101, 53, 15));

        dropdownFilterOptions = new JComboBox<>();
        dropdownFilterOptions.setFont(new Font("SansSerif", Font.PLAIN, 13));
        selectorRow.add(lblFilterLabel);
        selectorRow.add(dropdownFilterOptions);
        add(selectorRow);
        add(Box.createVerticalStrut(20));

        JPanel metricsGrid = new JPanel(new GridLayout(1, 4, 15, 0)) {
            @Override
            public Dimension getMaximumSize() {
                return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
            }
        };
        metricsGrid.setOpaque(false);
        metricsGrid.setAlignmentX(Component.LEFT_ALIGNMENT);

        metricsGrid.add(createMiniStatCard("Total members", lblTotalMembersVal = new JLabel("-"), new Color(101, 53, 15)));
        metricsGrid.add(createMiniStatCard("Total collected money", lblTotalMoneyVal = new JLabel("- birr"), new Color(46, 117, 59)));
        metricsGrid.add(createMiniStatCard("Current cycle", lblCurrentCycleVal = new JLabel("-"), new Color(101, 53, 15)));
        metricsGrid.add(createMiniStatCard("Next payout member", lblNextPayoutVal = new JLabel("-"), new Color(46, 117, 59)));
        add(metricsGrid);
        add(Box.createVerticalStrut(25));

        JLabel lblTableTitle = new JLabel("Payment status table");
        lblTableTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTableTitle.setForeground(new Color(101, 53, 15));
        lblTableTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(lblTableTitle);
        add(Box.createVerticalStrut(10));

        String[] headers = {"Member Name", "Cycle Paid", "Amount", "Status"};
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
        List<String> groups = reportService.getAllEqubGroups();
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
        EqubReport report = reportService.getEqubReportData(groupName);
        tableModel.setRowCount(0);

        if (report != null) {
            lblTotalMembersVal.setText(String.valueOf(report.totalMembers));
            lblTotalMoneyVal.setText(String.format("%,.2f birr", report.totalCollected));
            lblCurrentCycleVal.setText(report.currentCycle);
            lblNextPayoutVal.setText(report.nextPayoutMember != null ? report.nextPayoutMember : "None");

            if (report.memberRows != null) {
                for (EqubMemberRow row : report.memberRows) {
                    tableModel.addRow(new Object[]{row.memberName, row.cyclePaid, String.format("%,.2f birr", row.amount), row.status});
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
        card.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        card.setPreferredSize(new Dimension(160, 75));

        JLabel lblMsg = new JLabel(label);
        lblMsg.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblMsg.setForeground(new Color(130, 125, 115));

        lblValueRef.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblValueRef.setForeground(textValueColor);

        card.add(lblMsg);
        card.add(Box.createVerticalStrut(4));
        card.add(lblValueRef);
        return card;
    }
}