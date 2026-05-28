package ui.reports;

import service.ReportService;
import model.ReportDataModels.MemberReport;
import model.ReportDataModels.TransactionRow;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class MemberReportPanel extends JPanel {
    private final ReportService reportService;
    private final DefaultTableModel tableModel;
    private final JLabel lblNameValue;
    private final JLabel lblStatsSubLine;
    private final JTextField txtSearch;

    public MemberReportPanel(ReportHomePanel subCoordinator, ReportService reportService) {
        this.reportService = reportService;

        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(25, 35, 40, 35));

        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);
        headerRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTitle = new JLabel("Member Report Panel");
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
        add(Box.createVerticalStrut(20));

        txtSearch = new JTextField("🔍 Search Member...");
        txtSearch.setFont(new Font("SansSerif", Font.PLAIN, 15));
        txtSearch.setForeground(Color.GRAY);
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(215, 205, 185), 1, true),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        txtSearch.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        txtSearch.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtSearch.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (txtSearch.getText().equals("🔍 Search Member...")) {
                    txtSearch.setText("");
                    txtSearch.setForeground(Color.BLACK);
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (txtSearch.getText().isEmpty()) {
                    txtSearch.setText("🔍 Search Member...");
                    txtSearch.setForeground(Color.GRAY);
                }
            }
        });
        add(txtSearch);
        add(Box.createVerticalStrut(20));

        JPanel profileSummaryCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(248, 230, 202, 160));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
            }
        };
        profileSummaryCard.setOpaque(false);
        profileSummaryCard.setLayout(new FlowLayout(FlowLayout.LEFT, 25, 15));
        profileSummaryCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        profileSummaryCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblAvatar = new JLabel("👤");
        lblAvatar.setFont(new Font("SansSerif", Font.PLAIN, 52));
        profileSummaryCard.add(lblAvatar);

        JPanel summaryDetailsTextWrapper = new JPanel();
        summaryDetailsTextWrapper.setOpaque(false);
        summaryDetailsTextWrapper.setLayout(new BoxLayout(summaryDetailsTextWrapper, BoxLayout.Y_AXIS));

        JLabel lblStaticType = new JLabel("Member");
        lblStaticType.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblStaticType.setForeground(new Color(130, 125, 115));

        lblNameValue = new JLabel("-");
        lblNameValue.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblNameValue.setForeground(new Color(46, 117, 59));

        lblStatsSubLine = new JLabel("Transactions: -  |  Total Paid: - birr  |  Groups Joined: -");
        lblStatsSubLine.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblStatsSubLine.setForeground(new Color(101, 53, 15));

        summaryDetailsTextWrapper.add(lblStaticType);
        summaryDetailsTextWrapper.add(lblNameValue);
        summaryDetailsTextWrapper.add(Box.createVerticalStrut(4));
        summaryDetailsTextWrapper.add(lblStatsSubLine);
        profileSummaryCard.add(summaryDetailsTextWrapper);
        add(profileSummaryCard);
        add(Box.createVerticalStrut(25));

        String[] columnHeaders = {"Transaction ID", "Date", "Amount", "Type"};
        tableModel = new DefaultTableModel(null, columnHeaders) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(tableModel);
        table.setRowHeight(32);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(240, 232, 215));

        JScrollPane tableScrollerPane = new JScrollPane(table);
        tableScrollerPane.setBorder(BorderFactory.createLineBorder(new Color(225, 215, 195)));
        tableScrollerPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(tableScrollerPane);

        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    loadData(txtSearch.getText().trim());
                }
            }
        });

        loadData("");
    }

    private void loadData(String query) {
        MemberReport report = reportService.getMemberReportData(query);
        tableModel.setRowCount(0);

        if (report != null && report.name != null) {
            lblNameValue.setText(report.name);
            lblStatsSubLine.setText(String.format("Transactions: %d  |  Total Paid: %,.2f birr  |  Groups Joined: %d",
                    report.transactionCount, report.totalPaid, report.groupsJoinedCount));

            if (report.transactions != null) {
                for (TransactionRow tx : report.transactions) {
                    tableModel.addRow(new Object[]{tx.transactionId, tx.date, String.format("%,.2f birr", tx.amount), tx.type});
                }
            }
        } else {
            lblNameValue.setText("No member selected");
            lblStatsSubLine.setText("Transactions: 0  |  Total Paid: 0 birr  |  Groups Joined: 0");
        }
    }
}