package ui.reports;

import service.ReportService;
import model.ReportDataModels.EdirReport;
import model.ReportDataModels.EdirEmergencyRow;
import util.LanguageManager;
import util.FontManager; // ✅ Imported FontManager

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
    private boolean isListeningToDropdown = true; // Prevents triggering queries mid-rebuild

    public EdirReportPanel(ReportHomePanel subCoordinator, ReportService reportService) {
        this.reportService = reportService;

        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(25, 35, 40, 35));

        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);
        headerRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ✅ Localized Main Title Layout
        JLabel lblTitle = new JLabel(LanguageManager.getString("edir.report.title"));
        lblTitle.setFont(FontManager.getBoldFont(30)); // ✅ Updated to FontManager
        lblTitle.setForeground(new Color(101, 53, 15));
        headerRow.add(lblTitle, BorderLayout.WEST);

        // ✅ Localized Return Back Action Layout
        JButton btnBack = new JButton(LanguageManager.getString("edir.report.btn_back"));
        btnBack.setFont(FontManager.getBoldFont(13)); // ✅ Updated to FontManager
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

        // ✅ Localized Select Filter Combo Box Description Text
        JLabel lblFilterLabel = new JLabel(LanguageManager.getString("edir.report.lbl_select_group"));
        lblFilterLabel.setFont(FontManager.getBoldFont(14)); // ✅ Updated to FontManager
        lblFilterLabel.setForeground(new Color(101, 53, 15));

        dropdownFilterOptions = new JComboBox<>();
        dropdownFilterOptions.setFont(FontManager.getPlainFont(13)); // ✅ Updated to FontManager
        dropdownFilterOptions.setPreferredSize(new Dimension(220, 30));
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

        // ✅ Localized Statistical Metric Segment Card Arrays
        String defaultCurrency = " - " + LanguageManager.getString("currency.unit");
        metricsGrid.add(createMiniStatCard(LanguageManager.getString("edir.report.stat.total_contrib"), lblContributions = new JLabel(defaultCurrency), new Color(46, 117, 59)));
        metricsGrid.add(createMiniStatCard(LanguageManager.getString("edir.report.stat.total_cases"), lblCases = new JLabel("-"), new Color(101, 53, 15)));
        metricsGrid.add(createMiniStatCard(LanguageManager.getString("edir.report.stat.approved_cases"), lblApproved = new JLabel("-"), new Color(101, 53, 15)));
        metricsGrid.add(createMiniStatCard(LanguageManager.getString("edir.report.stat.pending_cases"), lblPending = new JLabel("-"), new Color(184, 91, 23)));
        metricsGrid.add(createMiniStatCard(LanguageManager.getString("edir.report.stat.rem_balance"), lblBalance = new JLabel(defaultCurrency), new Color(46, 117, 59)));
        add(metricsGrid);
        add(Box.createVerticalStrut(25));

        // ✅ Localized Secondary Workspace Subtitle Meta Headers
        JLabel lblTableTitle = new JLabel(LanguageManager.getString("edir.report.table_title"));
        lblTableTitle.setFont(FontManager.getBoldFont(18)); // ✅ Updated to FontManager
        lblTableTitle.setForeground(new Color(101, 53, 15));
        lblTableTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(lblTableTitle);
        add(Box.createVerticalStrut(10));

        // ✅ Localized Table Column Meta Descriptions Matrix
        String[] headers = {
                LanguageManager.getString("edir.report.col.date"),
                LanguageManager.getString("edir.report.col.member"),
                LanguageManager.getString("edir.report.col.type"),
                LanguageManager.getString("edir.report.col.status"),
                LanguageManager.getString("edir.report.col.amount")
        };
        tableModel = new DefaultTableModel(null, headers) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(tableModel);
        table.setRowHeight(32);
        table.setShowGrid(false);
        table.setFont(FontManager.getPlainFont(13)); // ✅ Updated to FontManager
        table.getTableHeader().setFont(FontManager.getBoldFont(13)); // ✅ Updated to FontManager
        table.getTableHeader().setBackground(new Color(240, 232, 215));
        table.getTableHeader().setPreferredSize(new Dimension(0, 36));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(225, 215, 195)));
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(scrollPane);

        // Bind data change listener securely
        dropdownFilterOptions.addActionListener(e -> {
            if (isListeningToDropdown) {
                loadData((String) dropdownFilterOptions.getSelectedItem());
            }
        });

        // Initialize display configuration state elements
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
            // ✅ Localized Financial Value Suffix Rules
            String unit = " " + LanguageManager.getString("currency.unit");
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
                // ✅ Localized Empty Directory Query Fallback Label Text
                tableModel.addRow(new Object[]{"-", "-", LanguageManager.getString("edir.report.table.empty_row"), "-", "-"});
            }
        } else {
            clearDashboardDisplay();
        }
        revalidate();
        repaint();
    }

    private void clearDashboardDisplay() {
        String defaultCurrency = "- " + LanguageManager.getString("currency.unit");
        lblContributions.setText(defaultCurrency);
        lblCases.setText("-");
        lblApproved.setText("-");
        lblPending.setText("-");
        lblBalance.setText(defaultCurrency);
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
        lblMsg.setFont(FontManager.getBoldFont(11)); // ✅ Updated to FontManager
        lblMsg.setForeground(new Color(130, 125, 115));

        lblValueRef.setFont(FontManager.getBoldFont(16)); // ✅ Updated to FontManager
        lblValueRef.setForeground(textValueColor);

        card.add(lblMsg);
        card.add(Box.createVerticalStrut(4));
        card.add(lblValueRef);
        return card;
    }

    /**
     * ✅ DYNAMIC LIFECYCLE HOOK:
     * Rebuilds the collection options directly from the database schema layer
     * to prevent stale data display anomalies.
     */
    public void refreshViewOnLifecycleSignal() {
        isListeningToDropdown = false;
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