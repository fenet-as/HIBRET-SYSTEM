package ui.reports;

import service.ReportService;
import model.ReportDataModels.EqubReport;
import model.ReportDataModels.EqubMemberRow;
import util.LanguageManager;
import util.FontManager; // ✅ Imported FontManager

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
    private boolean isListeningToDropdown = true; // Flag to prevent multi-trigger glitches during list rebuilds

    public EqubReportPanel(ReportHomePanel subCoordinator, ReportService reportService) {
        this.reportService = reportService;

        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(25, 35, 40, 35));

        // 1. HEADER CONTAINER
        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);
        headerRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ✅ Localized Main Title
        JLabel lblTitle = new JLabel(LanguageManager.getString("equb.report.title"));
        lblTitle.setFont(FontManager.getBoldFont(30)); // ✅ Updated to FontManager
        lblTitle.setForeground(new Color(101, 53, 15));
        headerRow.add(lblTitle, BorderLayout.WEST);

        // ✅ Localized Back Button
        JButton btnBack = new JButton(LanguageManager.getString("equb.report.btn_back"));
        btnBack.setFont(FontManager.getBoldFont(13)); // ✅ Updated to FontManager
        btnBack.setForeground(new Color(130, 90, 40));
        btnBack.setContentAreaFilled(false);
        btnBack.setBorderPainted(false);
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.addActionListener(e -> subCoordinator.navigateToSubReport("HomeSelection"));
        headerRow.add(btnBack, BorderLayout.EAST);
        add(headerRow);
        add(Box.createVerticalStrut(15));

        // 2. LIVE SELECTOR GROUP DROPDOWN
        JPanel selectorRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        selectorRow.setOpaque(false);
        selectorRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ✅ Localized Selector Label
        JLabel lblFilterLabel = new JLabel(LanguageManager.getString("equb.report.lbl_select_group"));
        lblFilterLabel.setFont(FontManager.getBoldFont(14)); // ✅ Updated to FontManager
        lblFilterLabel.setForeground(new Color(101, 53, 15));

        dropdownFilterOptions = new JComboBox<>();
        dropdownFilterOptions.setFont(FontManager.getPlainFont(13)); // ✅ Updated to FontManager
        dropdownFilterOptions.setPreferredSize(new Dimension(220, 30));
        selectorRow.add(lblFilterLabel);
        selectorRow.add(dropdownFilterOptions);
        add(selectorRow);
        add(Box.createVerticalStrut(20));

        // 3. STATS CARD GRID
        JPanel metricsGrid = new JPanel(new GridLayout(1, 4, 15, 0)) {
            @Override
            public Dimension getMaximumSize() {
                return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
            }
        };
        metricsGrid.setOpaque(false);
        metricsGrid.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ✅ Localized Stat Cards & Dynamic Currency Suffixes
        String defaultCurrency = "- " + LanguageManager.getString("currency.unit");
        metricsGrid.add(createMiniStatCard(LanguageManager.getString("equb.report.stat.total_members"), lblTotalMembersVal = new JLabel("-"), new Color(101, 53, 15)));
        metricsGrid.add(createMiniStatCard(LanguageManager.getString("equb.report.stat.total_money"), lblTotalMoneyVal = new JLabel(defaultCurrency), new Color(46, 117, 59)));
        metricsGrid.add(createMiniStatCard(LanguageManager.getString("equb.report.stat.current_cycle"), lblCurrentCycleVal = new JLabel("-"), new Color(101, 53, 15)));
        metricsGrid.add(createMiniStatCard(LanguageManager.getString("equb.report.stat.next_payout"), lblNextPayoutVal = new JLabel("-"), new Color(46, 117, 59)));
        add(metricsGrid);
        add(Box.createVerticalStrut(25));

        // 4. DATA TABLE LEDGER
        // ✅ Localized Table Title Descriptor
        JLabel lblTableTitle = new JLabel(LanguageManager.getString("equb.report.table_title"));
        lblTableTitle.setFont(FontManager.getBoldFont(18)); // ✅ Updated to FontManager
        lblTableTitle.setForeground(new Color(101, 53, 15));
        lblTableTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(lblTableTitle);
        add(Box.createVerticalStrut(10));

        // ✅ Localized Table Meta Column Headers Configuration
        String[] headers = {
                LanguageManager.getString("equb.report.col.member"),
                LanguageManager.getString("equb.report.col.cycle"),
                LanguageManager.getString("equb.report.col.amount"),
                LanguageManager.getString("equb.report.col.status")
        };
        tableModel = new DefaultTableModel(null, headers) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(tableModel);
        table.setRowHeight(34);
        table.setShowGrid(false);
        table.setFont(FontManager.getPlainFont(13)); // ✅ Updated to FontManager
        table.getTableHeader().setFont(FontManager.getBoldFont(13)); // ✅ Updated to FontManager
        table.getTableHeader().setBackground(new Color(240, 232, 215));
        table.getTableHeader().setPreferredSize(new Dimension(0, 36));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(225, 215, 195)));
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(scrollPane);

        // Bind data change listener
        dropdownFilterOptions.addActionListener(e -> {
            if (isListeningToDropdown) {
                loadData((String) dropdownFilterOptions.getSelectedItem());
            }
        });

        // Load initialization datasets
        refreshViewOnLifecycleSignal();
    }

    private void loadData(String groupName) {
        if (groupName == null || groupName.trim().isEmpty()) {
            clearDashboardDisplay();
            return;
        }

        tableModel.setRowCount(0);
        EqubReport report = reportService.getEqubReportData(groupName);

        if (report != null) {
            String unit = " " + LanguageManager.getString("currency.unit");
            lblTotalMembersVal.setText(String.valueOf(report.totalMembers));
            lblTotalMoneyVal.setText(String.format("%,.2f" + unit, report.totalCollected));

            // ✅ Localized Fallback Labels for Evaluation States
            lblCurrentCycleVal.setText(report.currentCycle != null ? report.currentCycle : LanguageManager.getString("equb.report.status.active"));
            lblNextPayoutVal.setText(report.nextPayoutMember != null ? report.nextPayoutMember : LanguageManager.getString("equb.report.status.drawing_pool"));

            if (report.memberRows != null && !report.memberRows.isEmpty()) {
                for (EqubMemberRow row : report.memberRows) {
                    tableModel.addRow(new Object[]{
                            row.memberName,
                            row.cyclePaid,
                            String.format("%,.2f" + unit, row.amount),
                            row.status
                    });
                }
            } else {
                // ✅ Localized Empty Ledger Fallback Label
                tableModel.addRow(new Object[]{"-", "-", LanguageManager.getString("equb.report.table.empty_row"), "-"});
            }
        } else {
            clearDashboardDisplay();
        }

        revalidate();
        repaint();
    }

    private void clearDashboardDisplay() {
        String defaultCurrency = "- " + LanguageManager.getString("currency.unit");
        lblTotalMembersVal.setText("-");
        lblTotalMoneyVal.setText(defaultCurrency);
        lblCurrentCycleVal.setText("-");
        lblNextPayoutVal.setText("-");
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
        card.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        card.setPreferredSize(new Dimension(160, 75));

        JLabel lblMsg = new JLabel(label);
        lblMsg.setFont(FontManager.getBoldFont(12)); // ✅ Updated to FontManager
        lblMsg.setForeground(new Color(130, 125, 115));

        lblValueRef.setFont(FontManager.getBoldFont(18)); // ✅ Updated to FontManager
        lblValueRef.setForeground(textValueColor);

        card.add(lblMsg);
        card.add(Box.createVerticalStrut(4));
        card.add(lblValueRef);
        return card;
    }

    /**
     * ✅ DYNAMIC LIFECYCLE HOOK ENTRY POINT:
     * Rebuilds the combobox dropdown options straight from the DB every time the user
     * opens the Reports menu, ensuring zero context misalignment.
     */
    public void refreshViewOnLifecycleSignal() {
        isListeningToDropdown = false; // Suppress triggers while modifying indices
        dropdownFilterOptions.removeAllItems();

        List<String> groups = reportService.getAllEqubGroups();

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