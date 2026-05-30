package ui.reports;

import service.ReportService;
import model.ReportDataModels.MemberReport;
import model.ReportDataModels.TransactionRow;

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

        JLabel lblTitle = new JLabel("Member Account Statement Report");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 28));
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

        JLabel lblListHeader = new JLabel("Registered System Members:");
        lblListHeader.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblListHeader.setForeground(new Color(101, 53, 15));
        sidebar.add(lblListHeader, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 12, 0);
        txtListSearch = new JTextField();
        txtListSearch.setFont(new Font("SansSerif", Font.PLAIN, 14));
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
        memberJList.setFont(new Font("SansSerif", Font.PLAIN, 14));
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
        lblAvatar.setFont(new Font("SansSerif", Font.PLAIN, 52));
        profileSummaryCard.add(lblAvatar);

        JPanel textWrapper = new JPanel();
        textWrapper.setOpaque(false);
        textWrapper.setLayout(new BoxLayout(textWrapper, BoxLayout.Y_AXIS));

        JLabel lblStaticType = new JLabel("PARTICIPANT SUMMARY STATEMENT");
        lblStaticType.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblStaticType.setForeground(new Color(130, 125, 115));

        lblNameValue = new JLabel("Select a member from sidebar");
        lblNameValue.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblNameValue.setForeground(new Color(46, 117, 59));

        lblStatsSubLine = new JLabel("Total Transactions: - | Value Pool Metrics Unloaded");
        lblStatsSubLine.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblStatsSubLine.setForeground(new Color(101, 53, 15));

        textWrapper.add(lblStaticType);
        textWrapper.add(lblNameValue);
        textWrapper.add(Box.createVerticalStrut(4));
        textWrapper.add(lblStatsSubLine);
        profileSummaryCard.add(textWrapper);

        detailsPanel.add(profileSummaryCard);
        detailsPanel.add(Box.createVerticalStrut(20));

        String[] columnHeaders = {
                "Transaction ID",
                "Posting Date",
                "Target Association Group",
                "Flow Direction",
                "Activity Narration / Memo"
        };
        tableModel = new DefaultTableModel(null, columnHeaders) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable table = new JTable(tableModel);
        table.setRowHeight(34);
        table.setShowGrid(false);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(240, 232, 215));
        table.getTableHeader().setPreferredSize(new Dimension(0, 36));

        JScrollPane tableScrollerPane = new JScrollPane(table);
        tableScrollerPane.setBorder(BorderFactory.createLineBorder(new Color(225, 215, 195)));
        tableScrollerPane.setAlignmentX(Component.LEFT_ALIGNMENT);

        detailsPanel.add(tableScrollerPane);
        return detailsPanel;
    }

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
                lblNameValue.setText("No Registered Members Found");
                lblStatsSubLine.setText("Please register participants into the database to populate reports.");
                tableModel.setRowCount(0);
            }

            memberJList.revalidate();
            memberJList.repaint();
            revalidate();
            repaint();
        });
    }

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
            tableModel.setRowCount(0);
            lblNameValue.setText("No Filter Match Found");
            lblStatsSubLine.setText("Try adjusting spelling constraints or lookup alternative initials.");
        }

        revalidate();
        repaint();
    }

    private void loadMemberDetailedReport(String name) {
        tableModel.setRowCount(0);
        MemberReport report = reportService.getMemberReportData(name);

        if (report != null && report.name != null) {
            lblNameValue.setText(report.name);

            lblStatsSubLine.setText(String.format("Total Activity Items: %d | Total Capital Outlays: %,.2f ETB | Enrolled Groups: %d",
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
                tableModel.addRow(new Object[]{"-", "No transaction historical logs exist for this participant statement.", "-", "-", "-"});
            }
        }

        revalidate();
        repaint();
    }

    public void refreshViewOnLifecycleSignal() {
        initializeSidebarList();
    }
}