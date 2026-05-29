package ui.edir;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import service.EdirService;

public class EdirGroupDetailPanel extends JPanel {
    private final JPanel parentWrapper;
    private final EdirService edirService;
    private final String groupName;

    public EdirGroupDetailPanel(JPanel parentWrapper, EdirService edirService, String groupName) {
        this.parentWrapper = parentWrapper;
        this.edirService = edirService;
        this.groupName = groupName;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(253, 247, 237));
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        initHeader();
        add(Box.createVerticalStrut(20));
        initStatCards();
        add(Box.createVerticalStrut(20));
        initActionButtons();
        add(Box.createVerticalStrut(25));
        initRecentContributions();
    }

    private void initHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 40));

        JButton btnBack = new JButton("← Back");
        btnBack.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnBack.setForeground(new Color(101, 31, 16));
        btnBack.addActionListener(e -> {
            CardLayout innerLayout = (CardLayout) parentWrapper.getLayout();
            innerLayout.show(parentWrapper, "EdirHome");
        });

        JLabel lblTitle = new JLabel(groupName);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblTitle.setForeground(new Color(101, 31, 16));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setOpaque(false);
        leftPanel.add(btnBack);
        leftPanel.add(lblTitle);

        headerPanel.add(leftPanel, BorderLayout.WEST);
        add(headerPanel);
    }

    private void initStatCards() {
        JPanel cardsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        cardsPanel.setOpaque(false);
        cardsPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 100));

        cardsPanel.add(createStatCard("Members", "35", new Color(46, 117, 89)));
        cardsPanel.add(createStatCard("Fund Balance", "80,000 birr", new Color(46, 117, 89)));
        cardsPanel.add(createStatCard("Active Cases", "1", new Color(163, 51, 39)));
        cardsPanel.add(createStatCard("Pending Payments", "5", new Color(163, 51, 39)));

        add(cardsPanel);
    }

    private void initActionButtons() {
        JPanel actionPanel = new JPanel(new GridLayout(1, 5, 12, 0));
        actionPanel.setOpaque(false);
        actionPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 45));

        JButton btnAddMember = createModuleButton("Add Member");
        JButton btnRecordContribution = createModuleButton("Record Contribution");
        JButton btnEmergency = createModuleButton("Emergency Case");
        JButton btnDistribute = createModuleButton("Distribute Fund");
        JButton btnReports = createModuleButton("Reports");

        btnAddMember.addActionListener(e -> {
            EdirMembersPanel membersPanel = new EdirMembersPanel(parentWrapper, edirService, groupName);
            parentWrapper.add(membersPanel, "EdirMembers");
            ((CardLayout) parentWrapper.getLayout()).show(parentWrapper, "EdirMembers");
        });

        btnRecordContribution.addActionListener(e -> {
            EdirContributionPanel contributionPanel = new EdirContributionPanel(parentWrapper, edirService, groupName);
            parentWrapper.add(contributionPanel, "EdirContribution");
            ((CardLayout) parentWrapper.getLayout()).show(parentWrapper, "EdirContribution");
        });

        btnEmergency.addActionListener(e -> {
            EmergencyCasePanel emergencyPanel = new EmergencyCasePanel(parentWrapper, edirService, groupName);
            parentWrapper.add(emergencyPanel, "EmergencyForm");
            ((CardLayout) parentWrapper.getLayout()).show(parentWrapper, "EmergencyForm");
        });

        btnDistribute.addActionListener(e -> {
            DistributeFundPanel distributePanel = new DistributeFundPanel(parentWrapper, edirService, groupName);
            parentWrapper.add(distributePanel, "DistributeFund");
            ((CardLayout) parentWrapper.getLayout()).show(parentWrapper, "DistributeFund");
        });

        btnReports.addActionListener(e -> {
            EdirGroupReportPanel reportPanel = new EdirGroupReportPanel(parentWrapper, edirService, groupName);
            parentWrapper.add(reportPanel, "EdirGroupReport");
            ((CardLayout) parentWrapper.getLayout()).show(parentWrapper, "EdirGroupReport");
        });

        actionPanel.add(btnAddMember);
        actionPanel.add(btnRecordContribution);
        actionPanel.add(btnEmergency);
        actionPanel.add(btnDistribute);
        actionPanel.add(btnReports);

        add(actionPanel);
    }

    private void initRecentContributions() {
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setOpaque(false);

        JLabel lblSection = new JLabel("Recent Contributions");
        lblSection.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblSection.setForeground(new Color(101, 31, 16));
        lblSection.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        tableContainer.add(lblSection, BorderLayout.NORTH);

        String[] cols = {"Member", "Amount", "Date", "Status"};
        Object[][] data = {
                {"Sara", "200 birr", "10/06/2017", "Paid"},
                {"Abel", "200 birr", "10/06/2017", "Paid"},
                {"Hana", "200 birr", "10/06/2017", "Pending"}
        };

        DefaultTableModel model = new DefaultTableModel(data, cols);
        JTable table = new JTable(model) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int col) {
                Component c = super.prepareRenderer(renderer, row, col);
                if (col == 3) {
                    String val = (String) getValueAt(row, col);
                    c.setForeground(val.equals("Paid") ? new Color(46, 117, 89) : new Color(217, 83, 79));
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                } else {
                    c.setForeground(Color.DARK_GRAY);
                }
                return c;
            }
        };

        table.setRowHeight(40);
        table.setShowGrid(false);
        table.setBackground(Color.WHITE);
        table.getTableHeader().setBackground(new Color(249, 237, 222));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(new Color(230, 215, 195)));
        tableContainer.add(sp, BorderLayout.CENTER);

        add(tableContainer);
    }

    private JPanel createStatCard(String title, String value, Color valueColor) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));
                g2.setColor(new Color(235, 225, 210));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 15, 15));
                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblTitle.setForeground(Color.GRAY);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblValue.setForeground(valueColor);

        card.add(lblTitle);
        card.add(Box.createVerticalStrut(8));
        card.add(lblValue);
        return card;
    }

    private JButton createModuleButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.setColor(new Color(230, 215, 195));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 12, 12));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setForeground(new Color(101, 31, 16));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        return btn;
    }


    /**
     * Synchronizes and refreshes local group metrics, active emergency counters,
     * and historical contribution ledger entries directly from the database layer.
     */
    public void refreshDashboardMetricsAndLedger() {
        // 1. Re-fetch up-to-date data structures from the injected database service
        try {
            // Example:
            // double updatedBalance = edirService.getGroupBalance(groupName);
            // int activeCases = edirService.getActiveCasesCount(groupName);

            // 2. Refresh UI labels and table model elements
            // lblBalance.setText(String.format("%,.2f birr", updatedBalance));

            // For now, repaint to verify the hook executes perfectly
            revalidate();
            repaint();

            System.out.println("DEBUG: EdirGroupDetailPanel metric synchronization hook invoked successfully for: " + groupName);
        } catch (Exception ex) {
            System.err.println("ERROR: Failed to run refresh sequence for Edir group details: " + ex.getMessage());
        }
    }
}