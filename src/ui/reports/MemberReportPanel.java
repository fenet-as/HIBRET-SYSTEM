package ui.reports;

import service.ReportService;
import model.ReportDataModels.MemberReport;
import model.ReportDataModels.TransactionRow;
import util.LanguageManager;
import util.FontManager; // ✅ Imported FontManager

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Vector;

public class MemberReportPanel extends JPanel {
    private final ReportService reportService;

    // Left-side Navigation List Components
    private JTextField txtListSearch;
    private JList<String> memberJList;
    private DefaultListModel<String> listModel;
    private Vector<String> masterMemberList;

    // Right-side Detailed Report Card Components
    private JLabel lblNameValue;
    private JLabel lblStatsSubLine;
    private DefaultTableModel tableModel;

    public MemberReportPanel(ReportHomePanel subCoordinator, ReportService reportService) {
        this.reportService = reportService;
        this.masterMemberList = new Vector<>();

        setOpaque(false);
        setLayout(new BorderLayout(20, 0));
        setBorder(BorderFactory.createEmptyBorder(25, 35, 40, 35));

        // 1. TOP HEADER PANEL
        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);

        // ✅ Localized Main Title
        JLabel lblTitle = new JLabel(LanguageManager.getString("member.report.title"));
        lblTitle.setFont(FontManager.getBoldFont(28)); // ✅ Integrated FontManager
        lblTitle.setForeground(new Color(101, 53, 15));
        headerRow.add(lblTitle, BorderLayout.WEST);

        // ✅ Localized Back Button
        JButton btnBack = new JButton(LanguageManager.getString("member.report.btn_back"));
        btnBack.setFont(FontManager.getBoldFont(13)); // ✅ Integrated FontManager
        btnBack.setForeground(new Color(130, 90, 40));
        btnBack.setContentAreaFilled(false);
        btnBack.setBorderPainted(false);
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.addActionListener(e -> subCoordinator.navigateToSubReport("HomeSelection"));
        headerRow.add(btnBack, BorderLayout.EAST);

        add(headerRow, BorderLayout.NORTH);

        // 2. MAIN CONTAINER SPLIT WORKSPACE
        JPanel workspacePanel = new JPanel(new BorderLayout(25, 0));
        workspacePanel.setOpaque(false);
        workspacePanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        // Build Left Member Selector Sidebar Panel
        JPanel leftSidebar = createLeftSidebar();
        workspacePanel.add(leftSidebar, BorderLayout.WEST);

        // Build Right Detailed Data Card Viewer Panel
        JPanel rightDetailsView = createRightDetailsView();
        workspacePanel.add(rightDetailsView, BorderLayout.CENTER);

        add(workspacePanel, BorderLayout.CENTER);

        // Bootstrap data onto the user viewport layer
        initializeSidebarList();
    }

    private JPanel createLeftSidebar() {
        JPanel sidebar = new JPanel(new GridBagLayout());
        sidebar.setOpaque(false);
        sidebar.setPreferredSize(new Dimension(280, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(0, 0, 6, 0);

        // ✅ Localized Sidebar Title
        JLabel lblListHeader = new JLabel(LanguageManager.getString("member.report.sidebar_title"));
        lblListHeader.setFont(FontManager.getBoldFont(14)); // ✅ Integrated FontManager
        lblListHeader.setForeground(new Color(101, 53, 15));
        sidebar.add(lblListHeader, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 12, 0);
        txtListSearch = new JTextField();
        txtListSearch.setFont(FontManager.getPlainFont(14)); // ✅ Integrated FontManager
        txtListSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(215, 205, 185), 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        txtListSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterSidebarList(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterSidebarList(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterSidebarList(); }
        });
        sidebar.add(txtListSearch, gbc);

        listModel = new DefaultListModel<>();
        memberJList = new JList<>(listModel);
        memberJList.setFont(FontManager.getPlainFont(14)); // ✅ Integrated FontManager
        memberJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        memberJList.setFixedCellHeight(38);
        memberJList.setSelectionBackground(new Color(225, 212, 190));
        memberJList.setSelectionForeground(new Color(101, 53, 15));

        memberJList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedMemberName = memberJList.getSelectedValue();
                if (selectedMemberName != null) {
                    loadMemberDetailedReport(selectedMemberName);
                }
            }
        });

        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 0, 0, 0);

        JScrollPane listScroller = new JScrollPane(memberJList);
        listScroller.setBorder(BorderFactory.createLineBorder(new Color(220, 210, 190)));
        sidebar.add(listScroller, gbc);

        return sidebar;
    }

    private JPanel createRightDetailsView() {
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setOpaque(false);

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
        lblAvatar.setFont(FontManager.getPlainFont(52)); // ✅ Integrated FontManager
        profileSummaryCard.add(lblAvatar);

        JPanel textWrapper = new JPanel();
        textWrapper.setOpaque(false);
        textWrapper.setLayout(new BoxLayout(textWrapper, BoxLayout.Y_AXIS));

        // ✅ Localized Content Structure overview labels
        JLabel lblStaticType = new JLabel(LanguageManager.getString("member.report.static_overview"));
        lblStaticType.setFont(FontManager.getPlainFont(12)); // ✅ Integrated FontManager
        lblStaticType.setForeground(new Color(130, 125, 115));

        lblNameValue = new JLabel(LanguageManager.getString("member.report.select_prompt"));
        lblNameValue.setFont(FontManager.getBoldFont(22)); // ✅ Integrated FontManager
        lblNameValue.setForeground(new Color(46, 117, 59));

        lblStatsSubLine = new JLabel(LanguageManager.getString("member.report.stats_placeholder"));
        lblStatsSubLine.setFont(FontManager.getBoldFont(13)); // ✅ Integrated FontManager
        lblStatsSubLine.setForeground(new Color(101, 53, 15));

        textWrapper.add(lblStaticType);
        textWrapper.add(lblNameValue);
        textWrapper.add(Box.createVerticalStrut(4));
        textWrapper.add(lblStatsSubLine);
        profileSummaryCard.add(textWrapper);

        detailsPanel.add(profileSummaryCard);
        detailsPanel.add(Box.createVerticalStrut(20));

        // ✅ Localized Column Metadata Headers Configuration Matrix Array
        String[] columnHeaders = {
                LanguageManager.getString("member.report.col.tx_id"),
                LanguageManager.getString("member.report.col.date"),
                LanguageManager.getString("member.report.col.asset"),
                LanguageManager.getString("member.report.col.flow_type"),
                LanguageManager.getString("member.report.col.narration")
        };
        tableModel = new DefaultTableModel(null, columnHeaders) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable table = new JTable(tableModel);
        table.setRowHeight(34);
        table.setShowGrid(false);
        table.setFont(FontManager.getPlainFont(13)); // ✅ Integrated FontManager
        table.getTableHeader().setFont(FontManager.getBoldFont(13)); // ✅ Integrated FontManager
        table.getTableHeader().setBackground(new Color(240, 232, 215));
        table.getTableHeader().setPreferredSize(new Dimension(0, 36));

        JScrollPane tableScrollerPane = new JScrollPane(table);
        tableScrollerPane.setBorder(BorderFactory.createLineBorder(new Color(225, 215, 195)));
        tableScrollerPane.setAlignmentX(Component.LEFT_ALIGNMENT);

        detailsPanel.add(tableScrollerPane);
        return detailsPanel;
    }

    /**
     * ✅ THREAD-SAFE INITIALIZATION ROUTINE:
     * Pulls data safely from database thread and marshals rendering updates directly to the EDT.
     */
    private void initializeSidebarList() {
        java.util.List<String> allNames = reportService.getAllManagedMemberNames();

        SwingUtilities.invokeLater(() -> {
            masterMemberList.clear();
            listModel.clear();

            if (allNames != null && !allNames.isEmpty()) {
                for (String name : allNames) {
                    masterMemberList.add(name);
                    listModel.addElement(name);
                }
                memberJList.setSelectedIndex(0);
            } else {
                // ✅ Localized Initial Directory Verification Fallbacks
                lblNameValue.setText(LanguageManager.getString("member.report.no_members_title"));
                lblStatsSubLine.setText(LanguageManager.getString("member.report.no_members_desc"));
                tableModel.setRowCount(0);
            }

            memberJList.revalidate();
            memberJList.repaint();
            revalidate();
            repaint();
        });
    }

    /**
     * Local in-memory filtering logic to handle search inputs.
     */
    private void filterSidebarList() {
        String filterText = txtListSearch.getText().trim().toLowerCase();
        listModel.clear();

        if (filterText.isEmpty()) {
            for (String name : masterMemberList) {
                listModel.addElement(name);
            }
            if (!listModel.isEmpty()) {
                memberJList.setSelectedIndex(0);
            }
            return;
        }

        for (String name : masterMemberList) {
            if (name.toLowerCase().contains(filterText)) {
                listModel.addElement(name);
            }
        }

        if (!listModel.isEmpty()) {
            memberJList.setSelectedIndex(0);
        } else {
            // ✅ Localized Filter Non-Match Text Block Elements
            tableModel.setRowCount(0);
            lblNameValue.setText(LanguageManager.getString("member.report.no_match_title"));
            lblStatsSubLine.setText(LanguageManager.getString("member.report.no_match_desc"));
        }

        revalidate();
        repaint();
    }

    private void loadMemberDetailedReport(String name) {
        tableModel.setRowCount(0);
        MemberReport report = reportService.getMemberReportData(name);

        if (report != null && report.name != null) {
            lblNameValue.setText(report.name);

            // ✅ Localized Dynamic Dashboard Row Tracker String Output Format Rule
            lblStatsSubLine.setText(String.format(LanguageManager.getString("member.report.stats_format"),
                    report.transactionCount, report.totalPaid, report.groupsJoinedCount));

            if (report.transactions != null && !report.transactions.isEmpty()) {
                for (TransactionRow tx : report.transactions) {
                    tableModel.addRow(new Object[]{
                            tx.transactionId,
                            tx.date,
                            tx.groupName,
                            tx.type,
                            tx.description
                    });
                }
            } else {
                // ✅ Localized Table Historical Rows Verification Empty Label Fallback
                tableModel.addRow(new Object[]{"-", LanguageManager.getString("member.report.empty_table"), "-", "-", "-"});
            }
        }

        revalidate();
        repaint();
    }

    /**
     * ✅ LIFECYCLE TARGET INTERFACE:
     * Invoked by parent panel containers when this dashboard panel gains screen visibility.
     */
    public void refreshViewOnLifecycleSignal() {
        initializeSidebarList();
    }
}