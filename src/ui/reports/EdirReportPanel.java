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
    private boolean isListeningToDropdown = true; // Prevents triggering updates while the list is reloading

    public EdirReportPanel(ReportHomePanel subCoordinator, ReportService reportService) {
        this.reportService = reportService;

        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(25, 35, 40, 35));

        // 1. TOP HEADER ROW
        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);
        headerRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTitle = new JLabel("Edir Financial Summary");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 30));
        lblTitle.setForeground(new Color(101, 53, 15));
        headerRow.add(lblTitle, BorderLayout.WEST);

        JButton btnBack = new JButton("Back to Overview");
        btnBack.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnBack.setForeground(new Color(130, 90, 40));
        btnBack.setContentAreaFilled(false);
        btnBack.setBorderPainted(false);
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.addActionListener(e -> subCoordinator.navigateToSubReport("HomeSelection"));
        headerRow.add(btnBack, BorderLayout.EAST);
        add(headerRow);
        add(Box.createVerticalStrut(15));

        // 2. FILTER DROPDOWN ROW
        JPanel selectorRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        selectorRow.setOpaque(false);
        selectorRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblFilterLabel = new JLabel("Select Group:");
        lblFilterLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblFilterLabel.setForeground(new Color(101, 53, 15));

        dropdownFilterOptions = new JComboBox<>();
        dropdownFilterOptions.setFont(new Font("SansSerif", Font.PLAIN, 13));
        dropdownFilterOptions.setPreferredSize(new Dimension(220, 30));
        selectorRow.add(lblFilterLabel);
        selectorRow.add(dropdownFilterOptions);
        add(selectorRow);
        add(Box.createVerticalStrut(20));

        // 3. METRICS CARDS GRID
        JPanel metricsGrid = new JPanel(new GridLayout(1, 5, 12, 0)) {
            @Override
            public Dimension getMaximumSize() {
                return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
            }
        };
        metricsGrid.setOpaque(false);
        metricsGrid.setAlignmentX(Component.LEFT_ALIGNMENT);

        metricsGrid.add(createMiniStatCard("Total Contributions", lblContributions = new JLabel("- ETB"), new Color(46, 117, 59)));
        metricsGrid.add(createMiniStatCard("Emergency Cases", lblCases = new JLabel("-"), new Color(101, 53, 15)));
        metricsGrid.add(createMiniStatCard("Approved Claims", lblApproved = new JLabel("-"), new Color(101, 53, 15)));
        metricsGrid.add(createMiniStatCard("Pending Review", lblPending = new JLabel("-"), new Color(184, 91, 23)));
        metricsGrid.add(createMiniStatCard("Available Balance", lblBalance = new JLabel("- ETB"), new Color(46, 117, 59)));
        add(metricsGrid);
        add(Box.createVerticalStrut(25));

        // 4. HISTORY TABLE TITLE
        JLabel lblTableTitle = new JLabel("Recent Emergency Claims");
        lblTableTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTableTitle.setForeground(new Color(101, 53, 15));
        lblTableTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(lblTableTitle);
        add(Box.createVerticalStrut(10));

        // Clean, descriptive table headers
        String[] headers = {
                "Filing Date",
                "Member Name",
                "Emergency Type",
                "Claim Status",
                "Paid Amount"
        };
        tableModel = new DefaultTableModel(null, headers) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(tableModel);
        table.setRowHeight(32);
        table.setShowGrid(false);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(240, 232, 215));
        table.getTableHeader().setPreferredSize(new Dimension(0, 36));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(225, 215, 195)));
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(scrollPane);

        // Listen for group selection changes
        dropdownFilterOptions.addActionListener(e -> {
            if (isListeningToDropdown) {
                loadData((String) dropdownFilterOptions.getSelectedItem());
            }
        });

        refreshViewOnLifecycleSignal();
    }

    private void loadData(String groupName) {
        if (groupName == null || groupName.trim().isEmpty()) {
            clearDashboardDisplay();
            return;
        }

        tableModel.setRowCount(0);
        EdirReport report = reportService.getEdirReportData(groupName);

        if (report != null) {
            String unit = " ETB";
            lblContributions.setText(String.format("%,.2f" + unit, report.totalContributions));
            lblCases.setText(String.valueOf(report.totalEmergencyCases));
            lblApproved.setText(String.valueOf(report.approvedEmergencies));
            lblPending.setText(String.valueOf(report.pendingEmergencies));
            lblBalance.setText(String.format("%,.2f" + unit, report.remainingFundBalance));

            if (report.emergencyRows != null && !report.emergencyRows.isEmpty()) {
                for (EdirEmergencyRow row : report.emergencyRows) {
                    tableModel.addRow(new Object[]{
                            row.date,
                            row.memberName,
                            row.type,
                            row.status,
                            String.format("%,.2f" + unit, row.amount)
                    });
                }
            } else {
                tableModel.addRow(new Object[]{"-", "-", "No emergency cases recorded for this group.", "-", "-"});
            }
        } else {
            clearDashboardDisplay();
        }
        revalidate();
        repaint();
    }

    private void clearDashboardDisplay() {
        lblContributions.setText("- ETB");
        lblCases.setText("-");
        lblApproved.setText("-");
        lblPending.setText("-");
        lblBalance.setText("- ETB");
        tableModel.setRowCount(0);
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

    /**
     * Refreshes the group dropdown menu items directly from the database.
     */
    public void refreshViewOnLifecycleSignal() {
        isListeningToDropdown = false; // Turn off listener updates while changing items
        dropdownFilterOptions.removeAllItems();

        List<String> groups = reportService.getAllEdirGroups();

        if (groups != null && !groups.isEmpty()) {
            for (String group : groups) {
                dropdownFilterOptions.addItem(group);
            }
            isListeningToDropdown = true;
            dropdownFilterOptions.setSelectedIndex(0);
            loadData((String) dropdownFilterOptions.getSelectedItem());
        } else {
            isListeningToDropdown = true;
            clearDashboardDisplay();
        }
        revalidate();
        repaint();
    }
}