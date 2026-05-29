package ui.edir;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import java.util.Map;
import service.EdirService;

public class EdirGroupDetailPanel extends JPanel {
    private final JPanel parentWrapper;
    private final EdirService edirService;
    private final String groupName;

    private JLabel lblMembersValue;
    private JLabel lblBalanceValue;
    private JLabel lblCasesValue;
    private JTable recentLedgerTable;
    private DefaultTableModel ledgerTableModel;

    public EdirGroupDetailPanel(JPanel parentWrapper, EdirService edirService, String groupName) {
        this.parentWrapper = parentWrapper;
        this.edirService = edirService;
        this.groupName = groupName;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(253, 247, 237));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        initHeader();
        add(Box.createVerticalStrut(25));
        initStatCards();
        add(Box.createVerticalStrut(25));
        initActionButtons();
        add(Box.createVerticalStrut(30));
        initRecentContributions();

        refreshDashboardMetricsAndLedger();
    }

    public EdirService getEdirService() {
        return this.edirService;
    }

    // ✅ FIXED LOOKUP MAP HOOK KEYS
    public void refreshDashboardMetricsAndLedger() {
        Map<String, String> metrics = edirService.getGroupDetails(groupName);

        if (metrics != null && !metrics.isEmpty()) {
            lblMembersValue.setText(metrics.getOrDefault("total_members", "0") + " Active");
            double balance = 0.0;
            try {
                balance = Double.parseDouble(metrics.getOrDefault("fund_balance", "0.0"));
            } catch (NumberFormatException e) {}
            lblBalanceValue.setText(String.format("%,.2f ETB", balance));
            lblCasesValue.setText(metrics.getOrDefault("active_cases", "0") + " Request(s)");
        }

        ledgerTableModel.setRowCount(0);

        List<Map<String, String>> ledgerRows = edirService.getGroupTransactionLedger(groupName);

        for (Map<String, String> row : ledgerRows) {
            double amt = 0.0;
            try {
                amt = Double.parseDouble(row.getOrDefault("amount", "0"));
            } catch (NumberFormatException e) {}

            String type = row.getOrDefault("type", "UNKNOWN");
            String displayUser = row.getOrDefault("member_name", "SYSTEM/OFFICER");
            String descriptionText = row.getOrDefault("description", "No details logged");
            String formattedAmount;
            String status;

            switch (type) {
                case "PAYOUT":
                    formattedAmount = String.format("-%,.2f ETB", amt);
                    status = "Disbursed";
                    break;
                case "PENDING_CLAIM":
                    formattedAmount = String.format("%,.2f ETB", amt);
                    status = "🚨 Pending Claim";
                    break;
                case "APPROVED_CLAIM":
                    formattedAmount = String.format("%,.2f ETB", amt);
                    status = "Approved Case";
                    break;
                case "REGISTRATION":
                    formattedAmount = "0.00 ETB";
                    status = "Enrolled";
                    break;
                default: // CONTRIBUTION
                    formattedAmount = String.format("+%,.2f ETB", amt);
                    status = "Cleared";
                    break;
            }

            ledgerTableModel.addRow(new Object[]{
                    displayUser,
                    formattedAmount,
                    descriptionText,
                    status
            });
        }

        revalidate();
        repaint();
    }

    private void initHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 45));

        JButton btnBack = new JButton("← Return Overview") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(235, 225, 210));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnBack.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnBack.setForeground(new Color(101, 31, 16));
        btnBack.setContentAreaFilled(false);
        btnBack.setBorderPainted(false);
        btnBack.setPreferredSize(new Dimension(150, 38));
        btnBack.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btnBack.addActionListener(e -> {
            for (Component comp : parentWrapper.getComponents()) {
                if (comp instanceof EdirHomePanel) {
                    ((EdirHomePanel) comp).loadGroups();
                }
            }
            CardLayout layout = (CardLayout) parentWrapper.getLayout();
            layout.show(parentWrapper, "EdirHome");
        });

        JLabel lblTitle = new JLabel(groupName);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 26));
        lblTitle.setForeground(new Color(101, 31, 16));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setOpaque(false);
        leftPanel.add(btnBack);
        leftPanel.add(lblTitle);

        headerPanel.add(leftPanel, BorderLayout.WEST);
        add(headerPanel);
    }

    private void initStatCards() {
        JPanel cardsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        cardsPanel.setOpaque(false);
        cardsPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 110));

        lblMembersValue = new JLabel("0 Active");
        JPanel card1 = createStatCard("Registered Members", lblMembersValue, new Color(44, 122, 123));

        lblBalanceValue = new JLabel("0.00 ETB");
        JPanel card2 = createStatCard("Net Vault Balance", lblBalanceValue, new Color(34, 139, 94));

        lblCasesValue = new JLabel("0 Request(s)");
        JPanel card3 = createStatCard("Active Claims", lblCasesValue, new Color(197, 48, 48));

        JLabel lblArrearsPlaceholder = new JLabel("0 Arrears");
        JPanel card4 = createStatCard("Pending Notices", lblArrearsPlaceholder, new Color(183, 100, 30));

        cardsPanel.add(card1);
        cardsPanel.add(card2);
        cardsPanel.add(card3);
        cardsPanel.add(card4);

        add(cardsPanel);
    }

    private void initActionButtons() {
        JPanel actionPanel = new JPanel(new GridLayout(1, 5, 15, 0));
        actionPanel.setOpaque(false);
        actionPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 50));

        JButton btnAddMember = createModuleButton("Add Member", "👤");
        JButton btnRecordContribution = createModuleButton("Contributions", "💰");
        JButton btnEmergency = createModuleButton("Emergency Case", "🚨");
        JButton btnDistribute = createModuleButton("Disbursed Payout", "📤");
        JButton btnViewMembers = createModuleButton("View Members", "👥");

        btnAddMember.addActionListener(e -> {
            model.EdirGroup genericGroupObj = new model.EdirGroup();
            genericGroupObj.setName(groupName);
            AddMemberPanel p = new AddMemberPanel(parentWrapper, genericGroupObj, this);
            parentWrapper.add(p, "AddMember");
            ((CardLayout) parentWrapper.getLayout()).show(parentWrapper, "AddMember");
        });

        btnViewMembers.addActionListener(e -> {
            JPanel membersPanel = new JPanel(new BorderLayout(15, 15));
            membersPanel.setBackground(new Color(253, 247, 237));
            membersPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

            JLabel lblHeading = new JLabel("Active Enrollment Records for: " + groupName);
            lblHeading.setFont(new Font("SansSerif", Font.BOLD, 18));
            lblHeading.setForeground(new Color(101, 31, 16));
            membersPanel.add(lblHeading, BorderLayout.NORTH);

            String[] cols = {"Member Registry ID", "Full Legal Name", "Phone Mapping Line", "Profile Status"};
            DefaultTableModel membersModel = new DefaultTableModel(null, cols) {
                @Override
                public boolean isCellEditable(int r, int c) { return false; }
            };

            List<Map<String, String>> membersList = edirService.getMembersByGroup(groupName);
            for (Map<String, String> m : membersList) {
                membersModel.addRow(new Object[]{
                        m.get("id"),
                        m.get("full_name"),
                        m.get("phone"),
                        m.getOrDefault("status", "Active")
                });
            }

            JTable table = new JTable(membersModel);
            table.setRowHeight(38);
            table.setShowGrid(false);
            table.setFont(new Font("SansSerif", Font.PLAIN, 13));
            table.getTableHeader().setBackground(new Color(249, 237, 222));
            table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
            table.getTableHeader().setPreferredSize(new Dimension(0, 36));

            JScrollPane scroll = new JScrollPane(table);
            scroll.setBorder(BorderFactory.createLineBorder(new Color(230, 215, 195)));
            membersPanel.add(scroll, BorderLayout.CENTER);

            JButton btnReturn = new JButton("← Back to Group Dashboard");
            btnReturn.setFont(new Font("SansSerif", Font.BOLD, 13));
            btnReturn.setPreferredSize(new Dimension(200, 40));
            btnReturn.addActionListener(ev -> {
                CardLayout cl = (CardLayout) parentWrapper.getLayout();
                cl.show(parentWrapper, "EdirDetail");
            });

            JPanel southContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            southContainer.setOpaque(false);
            southContainer.add(btnReturn);
            membersPanel.add(southContainer, BorderLayout.SOUTH);

            parentWrapper.add(membersPanel, "GroupMembersListView");
            ((CardLayout) parentWrapper.getLayout()).show(parentWrapper, "GroupMembersListView");
        });

        btnRecordContribution.addActionListener(e -> {
            EdirContributionPanel p = new EdirContributionPanel(parentWrapper, edirService, groupName);
            parentWrapper.add(p, "EdirContribution");
            ((CardLayout) parentWrapper.getLayout()).show(parentWrapper, "EdirContribution");
        });

        btnEmergency.addActionListener(e -> {
            EmergencyCasePanel p = new EmergencyCasePanel(parentWrapper, edirService, groupName);
            parentWrapper.add(p, "EmergencyForm");
            ((CardLayout) parentWrapper.getLayout()).show(parentWrapper, "EmergencyForm");
        });

        btnDistribute.addActionListener(e -> {
            DistributeFundPanel p = new DistributeFundPanel(parentWrapper, edirService, groupName);
            parentWrapper.add(p, "DistributeFund");
            ((CardLayout) parentWrapper.getLayout()).show(parentWrapper, "DistributeFund");
        });

        actionPanel.add(btnAddMember);
        actionPanel.add(btnRecordContribution);
        actionPanel.add(btnEmergency);
        actionPanel.add(btnDistribute);
        actionPanel.add(btnViewMembers);

        add(actionPanel);
    }

    private void initRecentContributions() {
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);

        JLabel lblSec = new JLabel("Unified Audit Transaction Ledger Logs");
        lblSec.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblSec.setForeground(new Color(101, 31, 16));
        lblSec.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        container.add(lblSec, BorderLayout.NORTH);

        String[] cols = {"Entity Party Involved", "Cash Flow Allocation", "Log Audit Description", "System Status"};
        ledgerTableModel = new DefaultTableModel(null, cols);

        recentLedgerTable = new JTable(ledgerTableModel);
        recentLedgerTable.setRowHeight(40);
        recentLedgerTable.setShowGrid(false);
        recentLedgerTable.setBackground(Color.WHITE);
        recentLedgerTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        recentLedgerTable.getTableHeader().setBackground(new Color(249, 237, 222));
        recentLedgerTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        recentLedgerTable.getTableHeader().setPreferredSize(new Dimension(0, 36));

        JScrollPane sp = new JScrollPane(recentLedgerTable);
        sp.setBorder(BorderFactory.createLineBorder(new Color(230, 215, 195)));
        container.add(sp, BorderLayout.CENTER);

        add(container);
    }

    private JPanel createStatCard(String title, JLabel lblValue, Color textCol) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.setColor(new Color(230, 220, 205));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 16, 16));
                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblT = new JLabel(title);
        lblT.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblT.setForeground(Color.GRAY);

        lblValue.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblValue.setForeground(textCol);

        card.add(lblT);
        card.add(Box.createVerticalStrut(8));
        card.add(lblValue);
        return card;
    }

    private JButton createModuleButton(String text, String unicodeIcon) {
        JButton btn = new JButton("<html><body style='text-align: center;'>" + unicodeIcon + "<br>" + text + "</body></html>") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
                g2.setColor(new Color(220, 210, 190));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 14, 14));
                g2.dispose();

                FontMetrics fm = g.getFontMetrics();
                String[] lines = getText().replace("<html><body style='text-align: center;'>", "").replace("</body></html>", "").split("<br>");
                g.setFont(getFont());
                g.setColor(getForeground());

                int y = (getHeight() - (lines.length * fm.getHeight())) / 2 + fm.getAscent();
                for (String line : lines) {
                    int x = (getWidth() - fm.stringWidth(line)) / 2;
                    g.drawString(line, x, y);
                    y += fm.getHeight();
                }
            }
        };
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setForeground(new Color(101, 31, 16));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}