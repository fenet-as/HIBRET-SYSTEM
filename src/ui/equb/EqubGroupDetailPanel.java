package ui.equb;

import service.EqubService;
import model.Group;
import model.Member;
import model.Transaction;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class EqubGroupDetailPanel extends JPanel {
    private final JPanel containerPanel;
    private final EqubService service;
    private Group groupCtx;

    // UI View References
    private final JLabel lblMemberCount;
    private final JLabel lblTotalFunds;
    private final JLabel lblPayoutReceiver;
    private final JTable tableMembers;
    private final JTable tableTransactions;
    private final DefaultTableModel modelMembers;
    private final DefaultTableModel modelTransactions;

    // Core System Color Constants
    private static final Color TEXT_DARK_BROWN = new Color(101, 53, 15);
    private static final Color BUTTON_GREEN = new Color(46, 117, 59);
    private static final Color BUTTON_RED = new Color(160, 40, 20);
    private static final Color TABLE_HEADER_BG = new Color(245, 242, 235);
    private static final Color TABLE_BORDER_COLOR = new Color(230, 225, 210);
    private static final Color CARD_LABEL_GRAY = new Color(130, 125, 115);

    public EqubGroupDetailPanel(JPanel containerPanel, EqubService service, Group groupCtx) {
        this.containerPanel = containerPanel;
        this.service = service;
        this.groupCtx = groupCtx;

        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(25, 35, 40, 35));

        // ==========================================================
        // 1. TOP HEADER NAVIGATION LAYOUT
        // ==========================================================
        JPanel headPanel = new JPanel(new BorderLayout());
        headPanel.setOpaque(false);
        headPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        JPanel titleBlock = new JPanel(new GridLayout(2, 1, 0, 4));
        titleBlock.setOpaque(false);

        JLabel lblGroupName = new JLabel("Equb Group: " + groupCtx.getName());
        lblGroupName.setFont(new Font("SansSerif", Font.BOLD, 32));
        lblGroupName.setForeground(TEXT_DARK_BROWN);

        String formattedRate = String.format("%,.2f", groupCtx.getContributionAmount());
        JLabel lblGroupMeta = new JLabel("Payment per Round: " + formattedRate + " ETB");
        lblGroupMeta.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblGroupMeta.setForeground(CARD_LABEL_GRAY);
        titleBlock.add(lblGroupName);
        titleBlock.add(lblGroupMeta);

        JButton btnBack = new JButton("Back") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(TABLE_HEADER_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(TABLE_BORDER_COLOR);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnBack.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnBack.setForeground(TEXT_DARK_BROWN);
        btnBack.setContentAreaFilled(false);
        btnBack.setBorderPainted(false);
        btnBack.setFocusPainted(false);
        btnBack.setPreferredSize(new Dimension(95, 42));
        btnBack.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnBack.addActionListener(e -> navigateBackToHome());

        headPanel.add(titleBlock, BorderLayout.WEST);
        headPanel.add(btnBack, BorderLayout.EAST);
        add(headPanel);

        // ==========================================================
        // 2. LIVE DASHBOARD METRIC DISPLAY
        // ==========================================================
        JPanel summaryRibbon = new JPanel(new GridLayout(1, 3, 20, 0)) {
            @Override public Dimension getMaximumSize() {
                return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
            }
        };
        summaryRibbon.setOpaque(false);
        summaryRibbon.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        lblMemberCount = new JLabel("0");
        summaryRibbon.add(createMetricCard("Total Members", lblMemberCount, new Color(40, 40, 40)));

        lblTotalFunds = new JLabel("0.00 ETB");
        summaryRibbon.add(createMetricCard("Total Money Collected", lblTotalFunds, BUTTON_GREEN));

        lblPayoutReceiver = new JLabel("No Winner Picked");
        summaryRibbon.add(createMetricCard("Last Winner", lblPayoutReceiver, new Color(184, 91, 23)));
        add(summaryRibbon);

        // ==========================================================
        // 3. ACTION BUTTONS CONSOLE
        // ==========================================================
        JPanel controlConsole = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0)) {
            @Override public Dimension getMaximumSize() {
                return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
            }
        };
        controlConsole.setOpaque(false);
        controlConsole.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JButton btnAddMember = createStyledModuleButton("Add Member", BUTTON_GREEN);
        btnAddMember.addActionListener(e -> {
            EqubMembersPanel allocationScreen = new EqubMembersPanel(containerPanel, service, this.groupCtx);
            containerPanel.add(allocationScreen, "GroupMembersAllocation");
            ((CardLayout) containerPanel.getLayout()).show(containerPanel, "GroupMembersAllocation");
        });

        JButton btnRecordPayment = createStyledModuleButton("Record Payment", new Color(40, 110, 170));
        btnRecordPayment.addActionListener(e -> {
            EqubPaymentPanel paymentScreen = new EqubPaymentPanel(containerPanel, service, this.groupCtx);
            containerPanel.add(paymentScreen, "GroupPaymentAllocation");
            ((CardLayout) containerPanel.getLayout()).show(containerPanel, "GroupPaymentAllocation");
        });

        JButton btnTriggerRotation = createStyledModuleButton("Pick Winner", new Color(140, 90, 40));
        btnTriggerRotation.addActionListener(e -> {
            EqubRotationPanel rotationScreen = new EqubRotationPanel(containerPanel, service, this.groupCtx);
            containerPanel.add(rotationScreen, "RotationWheelContext");
            ((CardLayout) containerPanel.getLayout()).show(containerPanel, "RotationWheelContext");
        });

        JButton btnResetLedger = createStyledModuleButton("Clear History", BUTTON_RED);
        btnResetLedger.addActionListener(e -> {
            UIManager.put("OptionPane.messageFont", new Font("SansSerif", Font.PLAIN, 14));
            UIManager.put("OptionPane.buttonFont", new Font("SansSerif", Font.PLAIN, 13));
            UIManager.put("OptionPane.inputFont", new Font("SansSerif", Font.PLAIN, 14));

            int firstCheck = JOptionPane.showConfirmDialog(this,
                    "Are you completely sure you want to delete all history? This cannot be undone.",
                    "Warning",
                    JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);

            if (firstCheck == JOptionPane.YES_OPTION) {
                String passwordCheck = JOptionPane.showInputDialog(this,
                        "Please type admin to clean history:",
                        "Confirmation Required",
                        JOptionPane.WARNING_MESSAGE);

                if (passwordCheck != null && passwordCheck.equals("admin")) {
                    if (service.clearAllTransactionsForGroup(groupCtx.getId())) {
                        JOptionPane.showMessageDialog(this, "All history records have been successfully cleared.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        refreshViewGridData();
                    } else {
                        JOptionPane.showMessageDialog(this, "Could not update the database.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else if (passwordCheck != null) {
                    JOptionPane.showMessageDialog(this, "Incorrect password. Access denied.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        controlConsole.add(btnAddMember);
        controlConsole.add(btnRecordPayment);
        controlConsole.add(btnTriggerRotation);
        controlConsole.add(btnResetLedger);
        add(controlConsole);

        // ==========================================================
        // 4. DATA TABLES SUB-SYSTEM WORKSPACE
        // ==========================================================
        JPanel dataWorkspaceSplit = new JPanel(new GridLayout(1, 2, 25, 0));
        dataWorkspaceSplit.setOpaque(false);

        // ----------------------------------------------------------
        // LEFT SIDE PANEL: Active Members List (with Remove option)
        // ----------------------------------------------------------
        JPanel leftMembersContainer = new JPanel(new BorderLayout(0, 10));
        leftMembersContainer.setOpaque(false);
        JLabel lblLeftHeading = new JLabel("List of Members");
        lblLeftHeading.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblLeftHeading.setForeground(TEXT_DARK_BROWN);
        leftMembersContainer.add(lblLeftHeading, BorderLayout.NORTH);

        String[] leftColumns = { "ID", "Full Name", "Phone Number", "Action" };
        modelMembers = new DefaultTableModel(leftColumns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 3; }
        };
        tableMembers = new JTable(modelMembers);
        configureTableAesthetics(tableMembers);

        // Hide ID Column on members table
        tableMembers.getColumnModel().getColumn(0).setMinWidth(0);
        tableMembers.getColumnModel().getColumn(0).setMaxWidth(0);
        tableMembers.getColumnModel().getColumn(0).setPreferredWidth(0);
        tableMembers.getColumnModel().getColumn(1).setPreferredWidth(140);
        tableMembers.getColumnModel().getColumn(2).setPreferredWidth(110);
        tableMembers.getColumnModel().getColumn(3).setPreferredWidth(90);

        // Set up the Remove button render and action editor
        tableMembers.getColumnModel().getColumn(3).setCellRenderer(new TableCellRenderer() {
            private final JPanel cellPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 8));
            private final JButton btnRemove = createStyledTableActionButton("Remove", BUTTON_RED);
            {
                cellPanel.setOpaque(true);
                cellPanel.add(btnRemove);
            }
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) {
                cellPanel.setBackground(isS ? t.getSelectionBackground() : Color.WHITE);
                return cellPanel;
            }
        });

        tableMembers.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            private int targetMemberId;
            private String targetMemberName;
            private final JPanel cellPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 8));
            private final JButton btnRemove = createStyledTableActionButton("Remove", BUTTON_RED);
            {
                cellPanel.setOpaque(true);
                btnRemove.addActionListener(e -> {
                    fireEditingStopped();
                    UIManager.put("OptionPane.messageFont", new Font("SansSerif", Font.PLAIN, 14));
                    UIManager.put("OptionPane.buttonFont", new Font("SansSerif", Font.PLAIN, 13));

                    int choice = JOptionPane.showConfirmDialog(null,
                            "Are you sure you want to remove " + targetMemberName + " from this group?",
                            "Confirm Removal",
                            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                    if (choice == JOptionPane.YES_OPTION) {
                        if (service.removeMemberFromGroup(groupCtx.getId(), targetMemberId)) {
                            JOptionPane.showMessageDialog(null, "Member removed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                            refreshViewGridData();
                        } else {
                            JOptionPane.showMessageDialog(null, "Could not remove member. Check if they have transactions.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });
                cellPanel.add(btnRemove);
            }
            @Override
            public Component getTableCellEditorComponent(JTable t, Object v, boolean isS, int r, int c) {
                targetMemberId = (int) t.getValueAt(r, 0);
                targetMemberName = (String) t.getValueAt(r, 1);
                cellPanel.setBackground(t.getSelectionBackground());
                return cellPanel;
            }
        });

        JScrollPane scrollMembers = new JScrollPane(tableMembers);
        scrollMembers.setBorder(BorderFactory.createLineBorder(TABLE_BORDER_COLOR, 1));
        scrollMembers.getViewport().setBackground(Color.WHITE);
        leftMembersContainer.add(scrollMembers, BorderLayout.CENTER);

        // ----------------------------------------------------------
        // RIGHT SIDE PANEL: Recent Financial Log (with Type Column)
        // ----------------------------------------------------------
        JPanel rightTxContainer = new JPanel(new BorderLayout(0, 10));
        rightTxContainer.setOpaque(false);
        JLabel lblRightHeading = new JLabel("Recent Payments & Payouts");
        lblRightHeading.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblRightHeading.setForeground(TEXT_DARK_BROWN);
        rightTxContainer.add(lblRightHeading, BorderLayout.NORTH);

        String[] rightColumns = { "ID", "Date", "Name", "Type", "Amount", "Action" };
        modelTransactions = new DefaultTableModel(rightColumns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 5; }
        };
        tableTransactions = new JTable(modelTransactions);
        configureTableAesthetics(tableTransactions);

        // Layout dimensions for right side table columns
        tableTransactions.getColumnModel().getColumn(0).setMinWidth(0);
        tableTransactions.getColumnModel().getColumn(0).setMaxWidth(0);
        tableTransactions.getColumnModel().getColumn(0).setPreferredWidth(0);
        tableTransactions.getColumnModel().getColumn(1).setPreferredWidth(85);
        tableTransactions.getColumnModel().getColumn(2).setPreferredWidth(110);
        tableTransactions.getColumnModel().getColumn(3).setPreferredWidth(95);
        tableTransactions.getColumnModel().getColumn(4).setPreferredWidth(95);
        tableTransactions.getColumnModel().getColumn(5).setPreferredWidth(85);

        // TRANSACTION UNDO ENGINE
        tableTransactions.getColumnModel().getColumn(5).setCellRenderer(new TableCellRenderer() {
            private final JPanel cellPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 8));
            private final JButton btnUndo = createStyledTableActionButton("Undo", BUTTON_RED);
            {
                cellPanel.setOpaque(true);
                cellPanel.add(btnUndo);
            }
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) {
                cellPanel.setBackground(isS ? t.getSelectionBackground() : Color.WHITE);
                return cellPanel;
            }
        });

        tableTransactions.getColumnModel().getColumn(5).setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            private int targetTxId;
            private final JPanel cellPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 8));
            private final JButton btnUndo = createStyledTableActionButton("Undo", BUTTON_RED);
            {
                cellPanel.setOpaque(true);
                btnUndo.addActionListener(e -> {
                    fireEditingStopped();
                    UIManager.put("OptionPane.messageFont", new Font("SansSerif", Font.PLAIN, 14));
                    UIManager.put("OptionPane.buttonFont", new Font("SansSerif", Font.PLAIN, 13));

                    int choice = JOptionPane.showConfirmDialog(null,
                            "Are you sure you want to undo the transaction? This changes calculations.",
                            "Confirm",
                            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                    if (choice == JOptionPane.YES_OPTION) {
                        if (service.reverseTransaction(targetTxId)) {
                            JOptionPane.showMessageDialog(null, "Successfully undone and totals updated.", "Success", JOptionPane.INFORMATION_MESSAGE);
                            refreshViewGridData();
                        } else {
                            JOptionPane.showMessageDialog(null, "Could not undo this action.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });
                cellPanel.add(btnUndo);
            }
            @Override
            public Component getTableCellEditorComponent(JTable t, Object v, boolean isS, int r, int c) {
                targetTxId = (int) t.getValueAt(r, 0);
                cellPanel.setBackground(t.getSelectionBackground());
                return cellPanel;
            }
        });

        JScrollPane scrollTx = new JScrollPane(tableTransactions);
        scrollTx.setBorder(BorderFactory.createLineBorder(TABLE_BORDER_COLOR, 1));
        scrollTx.getViewport().setBackground(Color.WHITE);
        rightTxContainer.add(scrollTx, BorderLayout.CENTER);

        dataWorkspaceSplit.add(leftMembersContainer);
        dataWorkspaceSplit.add(rightTxContainer);
        add(dataWorkspaceSplit);

        refreshViewGridData();
    }

    // ==========================================================
    // 5. SYNCHRONIZED INTERACTIVE DATA LOOP
    // ==========================================================
    public void refreshViewGridData() {
        modelMembers.setRowCount(0);
        modelTransactions.setRowCount(0);

        List<Group> allGroups = service.getAllEqubGroups();
        if (allGroups != null) {
            for (Group lookup : allGroups) {
                if (lookup.getId() == this.groupCtx.getId()) {
                    this.groupCtx = lookup;
                    break;
                }
            }
        }

        List<Member> attachedMembersList = service.getMembersInGroup(groupCtx.getId());
        for (Member m : attachedMembersList) {
            modelMembers.addRow(new Object[]{m.getId(), m.getFullName(), m.getPhone(), "Remove"});
        }

        // REPLACE the transaction loop inside EqubGroupDetailPanel.refreshViewGridData() with this:
        List<Transaction> attachedLedgerLog = service.getRecentPaymentsForGroup(groupCtx.getId());
        for (Transaction tx : attachedLedgerLog) {

            // Direct identification from the transaction type property itself
            String typeDisplay = "Contribution";
            if (tx.getType() != null && tx.getType().equalsIgnoreCase("PAYOUT")) {
                typeDisplay = "Payout";
            }

            modelTransactions.addRow(new Object[]{
                    tx.getId(),
                    tx.getDateString(),
                    tx.getMemberName(),
                    typeDisplay, // Displays "Contribution" or "Payout" clearly based on data structure
                    String.format("%,.2f Birr", tx.getAmount()),
                    "Undo"
            });
        }




        lblMemberCount.setText(attachedMembersList.size() + " Active");

        double freshVaultFunds = service.getActualAvailableRoundPool(groupCtx.getId());
        lblTotalFunds.setText(String.format("%,.2f ETB", freshVaultFunds));

        String receiver = (groupCtx.getNextPayoutMemberName() == null) ? "No Winner Picked" : groupCtx.getNextPayoutMemberName();
        lblPayoutReceiver.setText(receiver);

        this.revalidate();
        this.repaint();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        refreshViewGridData();
    }

    private void navigateBackToHome() {
        ((CardLayout) containerPanel.getLayout()).show(containerPanel, "EqubHome");
        for (Component c : containerPanel.getComponents()) {
            if (c instanceof ui.equb.EqubHomePanel) {
                ((ui.equb.EqubHomePanel) c).loadEqubGroupsData();
            }
        }
    }

    private JPanel createMetricCard(String title, JLabel lblMetricValue, Color themeAccentColor) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 3, 16, 16);
                g2.setColor(TABLE_BORDER_COLOR);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 3, 16, 16);
                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));
        card.setPreferredSize(new Dimension(160, 95));

        JLabel lblT = new JLabel(title);
        lblT.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblT.setForeground(CARD_LABEL_GRAY);

        lblMetricValue.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblMetricValue.setForeground(themeAccentColor);

        card.add(lblT);
        card.add(Box.createVerticalStrut(6));
        card.add(lblMetricValue);
        return card;
    }

    private JButton createStyledModuleButton(String text, Color accentColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.setColor(TABLE_BORDER_COLOR);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setForeground(accentColor);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(150, 40));
        return btn;
    }

    private static JButton createStyledTableActionButton(String text, Color background) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(background);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(80, 30));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void configureTableAesthetics(JTable table) {
        table.setRowHeight(46);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setForeground(new Color(40, 40, 40));
        table.setGridColor(TABLE_BORDER_COLOR);
        table.setSelectionBackground(new Color(242, 238, 228));
        table.setSelectionForeground(TEXT_DARK_BROWN);
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 14));
        header.setBackground(TABLE_HEADER_BG);
        header.setForeground(TEXT_DARK_BROWN);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, TABLE_BORDER_COLOR));
        header.setReorderingAllowed(false);
    }
}