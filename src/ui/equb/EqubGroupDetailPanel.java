package ui.equb;

import service.EqubService;
import model.Group;
import model.Member;
import model.Transaction;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
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

    public EqubGroupDetailPanel(JPanel containerPanel, EqubService service, Group groupCtx) {
        this.containerPanel = containerPanel;
        this.service = service;
        this.groupCtx = groupCtx;

        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // ==========================================================
        // 1. TOP HEADER NAVIGATION LAYOUT
        // ==========================================================
        JPanel headPanel = new JPanel(new BorderLayout());
        headPanel.setOpaque(false);

        JPanel titleBlock = new JPanel(new GridLayout(2, 1, 0, 4));
        titleBlock.setOpaque(false);
        JLabel lblGroupName = new JLabel("🏦 Pool: " + groupCtx.getName());
        lblGroupName.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblGroupName.setForeground(new Color(101, 53, 15));

        JLabel lblGroupMeta = new JLabel(String.format("Cycle Rate: %,.2f Birr per member", groupCtx.getContributionAmount()));
        lblGroupMeta.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblGroupMeta.setForeground(new Color(120, 110, 95));
        titleBlock.add(lblGroupName);
        titleBlock.add(lblGroupMeta);
        headPanel.add(titleBlock, BorderLayout.WEST);

        JButton btnBack = new JButton("⬅ Back to Pools Grid");
        btnBack.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnBack.addActionListener(e -> navigateBackToHome());
        headPanel.add(btnBack, BorderLayout.EAST);
        add(headPanel, BorderLayout.NORTH);

        // ==========================================================
        // 2. LIVE DASHBOARD METRIC MATRIX DISPLAY
        // ==========================================================
        JPanel summaryRibbon = new JPanel(new GridLayout(1, 3, 20, 0));
        summaryRibbon.setOpaque(false);
        summaryRibbon.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        lblMemberCount = createCardMetricLabel("0");
        summaryRibbon.add(createMetricCard("Registered Members", lblMemberCount, new Color(54, 122, 204)));

        lblTotalFunds = createCardMetricLabel("0.00 ETB");
        summaryRibbon.add(createMetricCard("Aggregated Capital Box", lblTotalFunds, new Color(34, 112, 43)));

        lblPayoutReceiver = createCardMetricLabel("No Draw Active");
        lblPayoutReceiver.setFont(new Font("SansSerif", Font.BOLD, 16));
        summaryRibbon.add(createMetricCard("Latest Cycle Payout Winner", lblPayoutReceiver, new Color(176, 90, 32)));

        // ==========================================================
        // 3. TRANSACTION CONSOLE WORKFLOW HOOKS
        // ==========================================================
        JPanel controlConsole = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        controlConsole.setOpaque(false);

        JButton btnAddMember = createStyledActionButton("👤 Add Member to Group", new Color(33, 115, 70));
        btnAddMember.addActionListener(e -> {
            EqubMembersPanel allocationScreen = new EqubMembersPanel(containerPanel, service, this.groupCtx);
            containerPanel.add(allocationScreen, "GroupMembersAllocation");
            ((CardLayout) containerPanel.getLayout()).show(containerPanel, "GroupMembersAllocation");
        });

        JButton btnRecordPayment = createStyledActionButton("💰 Record Member Payment", new Color(40, 96, 144));
        btnRecordPayment.addActionListener(e -> {
            EqubPaymentPanel paymentScreen = new EqubPaymentPanel(containerPanel, service, this.groupCtx);
            containerPanel.add(paymentScreen, "GroupPaymentAllocation");
            ((CardLayout) containerPanel.getLayout()).show(containerPanel, "GroupPaymentAllocation");
        });

        JButton btnTriggerRotation = createStyledActionButton("🔄 Payout Rotation Wheel", new Color(139, 69, 19));
        btnTriggerRotation.addActionListener(e -> {
            EqubRotationPanel rotationScreen = new EqubRotationPanel(containerPanel, service, this.groupCtx);
            containerPanel.add(rotationScreen, "RotationWheelContext");
            ((CardLayout) containerPanel.getLayout()).show(containerPanel, "RotationWheelContext");
        });

        // ✅ ADDED: Guarded Master Reset/Wipe button functionality
        JButton btnResetLedger = createStyledActionButton("🗑️ Clear & Reset Ledger", new Color(192, 41, 43));
        btnResetLedger.addActionListener(e -> {
            int firstCheck = JOptionPane.showConfirmDialog(this,
                    "Are you absolutely sure you want to drop ALL transactions for this pool?\nThis resets your Aggregated Capital Box to 0.00 ETB.",
                    "⚠️ CRITICAL SYSTEM RESET WARNING",
                    JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);

            if (firstCheck == JOptionPane.YES_OPTION) {
                String passwordCheck = JOptionPane.showInputDialog(this,
                        "Type the administrator password 'admin' to execute ledger database clear:",
                        "Master Authentication Required",
                        JOptionPane.WARNING_MESSAGE);

                if (passwordCheck != null && passwordCheck.equals("admin")) {
                    if (service.clearAllTransactionsForGroup(groupCtx.getId())) {
                        JOptionPane.showMessageDialog(this, "Ledger records cleared successfully. Pool balance restored to 0.00.", "Purge Complete", JOptionPane.INFORMATION_MESSAGE);
                        refreshViewGridData();
                    } else {
                        JOptionPane.showMessageDialog(this, "Database execution error occurred while processing requests.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else if (passwordCheck != null) {
                    JOptionPane.showMessageDialog(this, "Incorrect password. Data operation aborted.", "Access Denied", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        controlConsole.add(btnAddMember);
        controlConsole.add(btnRecordPayment);
        controlConsole.add(btnTriggerRotation);
        controlConsole.add(btnResetLedger); // Appended button layout index array matrix

        JPanel topWrapper = new JPanel(new BorderLayout());
        topWrapper.setOpaque(false);
        topWrapper.add(summaryRibbon, BorderLayout.NORTH);
        topWrapper.add(controlConsole, BorderLayout.SOUTH);

        // ==========================================================
        // 4. DATA TABLES SUB-SYSTEM WORKSPACE
        // ==========================================================
        JPanel dataWorkspaceSplit = new JPanel(new GridLayout(1, 2, 25, 0));
        dataWorkspaceSplit.setOpaque(false);
        dataWorkspaceSplit.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 0));

        // LEFT SIDE PANEL: Active Members List
        JPanel leftMembersContainer = new JPanel(new BorderLayout(0, 8));
        leftMembersContainer.setOpaque(false);
        JLabel lblLeftHeading = new JLabel("👥 Active Group Participants");
        lblLeftHeading.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblLeftHeading.setForeground(new Color(101, 53, 15));
        leftMembersContainer.add(lblLeftHeading, BorderLayout.NORTH);

        modelMembers = new DefaultTableModel(new String[]{"ID", "Full Name", "Phone Registry"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tableMembers = new JTable(modelMembers);
        configureTableAesthetics(tableMembers);
        leftMembersContainer.add(new JScrollPane(tableMembers), BorderLayout.CENTER);

        // RIGHT SIDE PANEL: Recent Financial Log Ledger
        JPanel rightTxContainer = new JPanel(new BorderLayout(0, 8));
        rightTxContainer.setOpaque(false);
        JLabel lblRightHeading = new JLabel("📜 Recent Group Transactions Ledger");
        lblRightHeading.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblRightHeading.setForeground(new Color(101, 53, 15));
        rightTxContainer.add(lblRightHeading, BorderLayout.NORTH);

        modelTransactions = new DefaultTableModel(new String[]{"TX ID", "Record Date", "Member Name", "Amount", "Action"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 4; }
        };
        tableTransactions = new JTable(modelTransactions);
        configureTableAesthetics(tableTransactions);

        tableTransactions.getColumnModel().getColumn(0).setMinWidth(0);
        tableTransactions.getColumnModel().getColumn(0).setMaxWidth(0);
        tableTransactions.getColumnModel().getColumn(0).setPreferredWidth(0);

        // IMMEDIATE INLINE TRANSACTION ROLLBACK RENDERING ENGINE
        tableTransactions.getColumnModel().getColumn(4).setCellRenderer(new TableCellRenderer() {
            private final JButton btnUndo = new JButton("↩ Undo");
            {
                btnUndo.setFont(new Font("SansSerif", Font.BOLD, 11));
                btnUndo.setMargin(new Insets(2, 4, 2, 4));
            }
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) {
                return btnUndo;
            }
        });

        tableTransactions.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            private int targetTxId;
            private final JButton btnUndo = new JButton("↩ Undo");
            {
                btnUndo.setFont(new Font("SansSerif", Font.BOLD, 11));
                btnUndo.addActionListener(e -> {
                    fireEditingStopped();
                    int choice = JOptionPane.showConfirmDialog(null,
                            "Do you want to reverse transaction #" + targetTxId + "?\nThis rolls back the capital box balance instantly.",
                            "Confirm Undo Reversal", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (choice == JOptionPane.YES_OPTION) {
                        if (service.reverseTransaction(targetTxId)) {
                            JOptionPane.showMessageDialog(null, "Transaction successfully reversed.", "Reversal Complete", JOptionPane.INFORMATION_MESSAGE);
                            refreshViewGridData();
                        } else {
                            JOptionPane.showMessageDialog(null, "Error executing database deletion payload.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });
            }
            @Override
            public Component getTableCellEditorComponent(JTable t, Object v, boolean isS, int r, int c) {
                targetTxId = (int) t.getValueAt(r, 0);
                return btnUndo;
            }
        });

        rightTxContainer.add(new JScrollPane(tableTransactions), BorderLayout.CENTER);
        dataWorkspaceSplit.add(leftMembersContainer);
        dataWorkspaceSplit.add(rightTxContainer);

        JPanel coreCentralStack = new JPanel(new BorderLayout());
        coreCentralStack.setOpaque(false);
        coreCentralStack.add(topWrapper, BorderLayout.NORTH);
        coreCentralStack.add(dataWorkspaceSplit, BorderLayout.CENTER);
        add(coreCentralStack, BorderLayout.CENTER);

        refreshViewGridData();
    }

    // ==========================================================
    // 5. SYNCHRONIZED INTERACTIVE VIEW DATA ENGINE LOOP
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
            modelMembers.addRow(new Object[]{m.getId(), m.getFullName(), m.getPhone()});
        }

        List<Transaction> attachedLedgerLog = service.getRecentPaymentsForGroup(groupCtx.getId());
        for (Transaction tx : attachedLedgerLog) {
            modelTransactions.addRow(new Object[]{
                    tx.getId(),
                    tx.getDateString(),
                    tx.getMemberName(),
                    String.format("%,.2f Birr", tx.getAmount()),
                    "Undo"
            });
        }

        lblMemberCount.setText(String.valueOf(attachedMembersList.size()));

        double freshVaultFunds = service.getActualAvailableRoundPool(groupCtx.getId());
        lblTotalFunds.setText(String.format("%,.2f ETB", freshVaultFunds));

        String receiver = (groupCtx.getNextPayoutMemberName() == null) ? "No Draw Active" : groupCtx.getNextPayoutMemberName();
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

    private JPanel createMetricCard(String caption, JLabel lblMetricValue, Color themeAccentColor) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(themeAccentColor);
                g2.fillRect(0, 0, 8, getHeight());
                g2.setColor(new Color(230, 225, 215));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 15));

        JLabel lblCaption = new JLabel(caption.toUpperCase());
        lblCaption.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblCaption.setForeground(new Color(130, 125, 115));

        card.add(lblCaption);
        card.add(Box.createVerticalStrut(6));
        card.add(lblMetricValue);
        return card;
    }

    private JLabel createCardMetricLabel(String valueText) {
        JLabel l = new JLabel(valueText);
        l.setFont(new Font("SansSerif", Font.BOLD, 22));
        l.setForeground(new Color(50, 45, 35));
        return l;
    }

    private JButton createStyledActionButton(String text, Color baseColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setForeground(baseColor);
        btn.setBackground(Color.WHITE);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(baseColor, 1, true),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)
        ));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(baseColor);
                btn.setForeground(Color.WHITE);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(Color.WHITE);
                btn.setForeground(baseColor);
            }
        });
        return btn;
    }

    private void configureTableAesthetics(JTable table) {
        table.setRowHeight(32);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(242, 238, 228));
        table.getTableHeader().setForeground(new Color(101, 53, 15));
        table.getTableHeader().setReorderingAllowed(false);
        table.setSelectionBackground(new Color(235, 243, 232));
        table.setSelectionForeground(Color.BLACK);
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
    }
}