package ui.equb;

import service.EqubService;
import model.Group;
import model.Member;
import model.Transaction;
import util.LanguageManager;
import util.FontManager; // ✅ Imported FontManager
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

        // ✅ Localized Group Name with dynamic font metrics lookup
        JLabel lblGroupName = new JLabel(LanguageManager.getFormattedString("equb.detail.pool", groupCtx.getName()));
        lblGroupName.setFont(FontManager.getBoldFont(22));
        lblGroupName.setForeground(new Color(101, 53, 15));

        String formattedRate = String.format("%,.2f", groupCtx.getContributionAmount());
        JLabel lblGroupMeta = new JLabel(LanguageManager.getFormattedString("equb.detail.meta", formattedRate));
        lblGroupMeta.setFont(FontManager.getPlainFont(13));
        lblGroupMeta.setForeground(new Color(120, 110, 95));
        titleBlock.add(lblGroupName);
        titleBlock.add(lblGroupMeta);
        headPanel.add(titleBlock, BorderLayout.WEST);

        // ✅ Localized Back Navigation Action Title Text Label
        JButton btnBack = new JButton(LanguageManager.getString("equb.detail.btn_back"));
        btnBack.setFont(FontManager.getBoldFont(13));
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
        summaryRibbon.add(createMetricCard(LanguageManager.getString("equb.detail.card.members"), lblMemberCount, new Color(54, 122, 204)));

        lblTotalFunds = createCardMetricLabel("0.00 ETB");
        summaryRibbon.add(createMetricCard(LanguageManager.getString("equb.detail.card.funds"), lblTotalFunds, new Color(34, 112, 43)));

        // ✅ Localized Fallback Winner Value Text with responsive layout boundaries
        lblPayoutReceiver = createCardMetricLabel(LanguageManager.getString("equb.detail.no_draw"));
        lblPayoutReceiver.setFont(FontManager.getBoldFont(16));
        summaryRibbon.add(createMetricCard(LanguageManager.getString("equb.detail.card.winner"), lblPayoutReceiver, new Color(176, 90, 32)));

        // ==========================================================
        // 3. TRANSACTION CONSOLE WORKFLOW HOOKS
        // ==========================================================
        JPanel controlConsole = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        controlConsole.setOpaque(false);

        // ✅ Localized Actions Form Management Label Targets
        JButton btnAddMember = createStyledActionButton(LanguageManager.getString("equb.detail.btn.add_member"), new Color(33, 115, 70));
        btnAddMember.addActionListener(e -> {
            EqubMembersPanel allocationScreen = new EqubMembersPanel(containerPanel, service, this.groupCtx);
            containerPanel.add(allocationScreen, "GroupMembersAllocation");
            ((CardLayout) containerPanel.getLayout()).show(containerPanel, "GroupMembersAllocation");
        });

        JButton btnRecordPayment = createStyledActionButton(LanguageManager.getString("equb.detail.btn.record_payment"), new Color(40, 96, 144));
        btnRecordPayment.addActionListener(e -> {
            EqubPaymentPanel paymentScreen = new EqubPaymentPanel(containerPanel, service, this.groupCtx);
            containerPanel.add(paymentScreen, "GroupPaymentAllocation");
            ((CardLayout) containerPanel.getLayout()).show(containerPanel, "GroupPaymentAllocation");
        });

        JButton btnTriggerRotation = createStyledActionButton(LanguageManager.getString("equb.detail.btn.rotation"), new Color(139, 69, 19));
        btnTriggerRotation.addActionListener(e -> {
            EqubRotationPanel rotationScreen = new EqubRotationPanel(containerPanel, service, this.groupCtx);
            containerPanel.add(rotationScreen, "RotationWheelContext");
            ((CardLayout) containerPanel.getLayout()).show(containerPanel, "RotationWheelContext");
        });

        JButton btnResetLedger = createStyledActionButton(LanguageManager.getString("equb.detail.btn.reset"), new Color(192, 41, 43));
        btnResetLedger.addActionListener(e -> {
            // Apply font configurations before triggering alert boxes
            UIManager.put("OptionPane.messageFont", FontManager.getPlainFont(14));
            UIManager.put("OptionPane.buttonFont", FontManager.getPlainFont(13));
            UIManager.put("OptionPane.inputFont", FontManager.getPlainFont(14));

            // ✅ Localized Security Wiping Messages Box Prompts
            int firstCheck = JOptionPane.showConfirmDialog(this,
                    LanguageManager.getString("equb.detail.reset.confirm"),
                    LanguageManager.getString("equb.detail.reset.title"),
                    JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);

            if (firstCheck == JOptionPane.YES_OPTION) {
                String passwordCheck = JOptionPane.showInputDialog(this,
                        LanguageManager.getString("equb.detail.reset.auth_prompt"),
                        LanguageManager.getString("equb.detail.reset.auth_title"),
                        JOptionPane.WARNING_MESSAGE);

                if (passwordCheck != null && passwordCheck.equals("admin")) {
                    if (service.clearAllTransactionsForGroup(groupCtx.getId())) {
                        JOptionPane.showMessageDialog(this,
                                LanguageManager.getString("equb.detail.reset.success"),
                                LanguageManager.getString("equb.detail.reset.success_title"),
                                JOptionPane.INFORMATION_MESSAGE);
                        refreshViewGridData();
                    } else {
                        JOptionPane.showMessageDialog(this, LanguageManager.getString("edir.members.err.database"), LanguageManager.getString("msg.error"), JOptionPane.ERROR_MESSAGE);
                    }
                } else if (passwordCheck != null) {
                    JOptionPane.showMessageDialog(this, LanguageManager.getString("msg.access_denied"), LanguageManager.getString("msg.error"), JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        controlConsole.add(btnAddMember);
        controlConsole.add(btnRecordPayment);
        controlConsole.add(btnTriggerRotation);
        controlConsole.add(btnResetLedger);

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
        JLabel lblLeftHeading = new JLabel(LanguageManager.getString("equb.detail.heading.participants"));
        lblLeftHeading.setFont(FontManager.getBoldFont(14));
        lblLeftHeading.setForeground(new Color(101, 53, 15));
        leftMembersContainer.add(lblLeftHeading, BorderLayout.NORTH);

        // ✅ Localized Columns Def Row Meta Matrix Left Hand Side table
        String[] leftColumns = {
                LanguageManager.getString("equb.detail.col.id"),
                LanguageManager.getString("equb.detail.col.name"),
                LanguageManager.getString("equb.detail.col.phone")
        };
        modelMembers = new DefaultTableModel(leftColumns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tableMembers = new JTable(modelMembers);
        configureTableAesthetics(tableMembers);
        leftMembersContainer.add(new JScrollPane(tableMembers), BorderLayout.CENTER);

        // RIGHT SIDE PANEL: Recent Financial Log Ledger
        JPanel rightTxContainer = new JPanel(new BorderLayout(0, 8));
        rightTxContainer.setOpaque(false);
        JLabel lblRightHeading = new JLabel(LanguageManager.getString("equb.detail.heading.transactions"));
        lblRightHeading.setFont(FontManager.getBoldFont(14));
        lblRightHeading.setForeground(new Color(101, 53, 15));
        rightTxContainer.add(lblRightHeading, BorderLayout.NORTH);

        // ✅ Localized Table Model Column Data Schemas Row Definitions
        String[] rightColumns = {
                LanguageManager.getString("equb.detail.col.tx_id"),
                LanguageManager.getString("equb.detail.col.date"),
                LanguageManager.getString("equb.detail.col.name"),
                LanguageManager.getString("equb.detail.col.amount"),
                LanguageManager.getString("equb.detail.col.action")
        };
        modelTransactions = new DefaultTableModel(rightColumns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 4; }
        };
        tableTransactions = new JTable(modelTransactions);
        configureTableAesthetics(tableTransactions);

        tableTransactions.getColumnModel().getColumn(0).setMinWidth(0);
        tableTransactions.getColumnModel().getColumn(0).setMaxWidth(0);
        tableTransactions.getColumnModel().getColumn(0).setPreferredWidth(0);

        // IMMEDIATE INLINE TRANSACTION ROLLBACK RENDERING ENGINE
        tableTransactions.getColumnModel().getColumn(4).setCellRenderer(new TableCellRenderer() {
            private final JButton btnUndo = new JButton(LanguageManager.getString("equb.detail.table.undo"));
            {
                btnUndo.setFont(FontManager.getBoldFont(11));
                btnUndo.setMargin(new Insets(2, 4, 2, 4));
            }
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) {
                return btnUndo;
            }
        });

        tableTransactions.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            private int targetTxId;
            private final JButton btnUndo = new JButton(LanguageManager.getString("equb.detail.table.undo"));
            {
                btnUndo.setFont(FontManager.getBoldFont(11));
                btnUndo.addActionListener(e -> {
                    fireEditingStopped();

                    UIManager.put("OptionPane.messageFont", FontManager.getPlainFont(14));
                    UIManager.put("OptionPane.buttonFont", FontManager.getPlainFont(13));

                    // ✅ Localized Inline Table Dynamic Validation Box Triggers
                    int choice = JOptionPane.showConfirmDialog(null,
                            LanguageManager.getFormattedString("equb.detail.table.undo_confirm", String.valueOf(targetTxId)),
                            LanguageManager.getString("equb.detail.table.undo_title"),
                            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                    if (choice == JOptionPane.YES_OPTION) {
                        if (service.reverseTransaction(targetTxId)) {
                            JOptionPane.showMessageDialog(null,
                                    LanguageManager.getString("equb.detail.table.undo_success"),
                                    LanguageManager.getString("equb.detail.table.undo_success_title"),
                                    JOptionPane.INFORMATION_MESSAGE);
                            refreshViewGridData();
                        } else {
                            JOptionPane.showMessageDialog(null, LanguageManager.getString("edir.home.delete.fail"), LanguageManager.getString("msg.error"), JOptionPane.ERROR_MESSAGE);
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
                    LanguageManager.getString("equb.detail.table.undo")
            });
        }

        lblMemberCount.setText(String.valueOf(attachedMembersList.size()));

        double freshVaultFunds = service.getActualAvailableRoundPool(groupCtx.getId());
        lblTotalFunds.setText(String.format("%,.2f ETB", freshVaultFunds));

        String receiver = (groupCtx.getNextPayoutMemberName() == null) ? LanguageManager.getString("equb.detail.no_draw") : groupCtx.getNextPayoutMemberName();
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
        lblCaption.setFont(FontManager.getBoldFont(11));
        lblCaption.setForeground(new Color(130, 125, 115));

        card.add(lblCaption);
        card.add(Box.createVerticalStrut(6));
        card.add(lblMetricValue);
        return card;
    }

    private JLabel createCardMetricLabel(String valueText) {
        JLabel l = new JLabel(valueText);
        l.setFont(FontManager.getBoldFont(22));
        l.setForeground(new Color(50, 45, 35));
        return l;
    }

    private JButton createStyledActionButton(String text, Color baseColor) {
        JButton btn = new JButton(text);
        btn.setFont(FontManager.getBoldFont(13));
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
        table.getTableHeader().setFont(FontManager.getBoldFont(12));
        table.getTableHeader().setBackground(new Color(242, 238, 228));
        table.getTableHeader().setForeground(new Color(101, 53, 15));
        table.getTableHeader().setReorderingAllowed(false);
        table.setSelectionBackground(new Color(235, 243, 232));
        table.setSelectionForeground(Color.BLACK);
        table.setFont(FontManager.getPlainFont(12));
    }
}