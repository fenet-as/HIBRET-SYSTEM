package ui.edir;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import java.util.Map;
import service.EdirService;
import util.LanguageManager;
import util.FontManager; // ✅ Imported FontManager

public class EdirGroupDetailPanel extends JPanel {
    private final JPanel parentWrapper;
    private final EdirService edirService;

    private final int groupId;
    private String groupName = "Loading Group...";

    private JLabel lblTitle;
    private JLabel lblMembersValue;
    private JLabel lblBalanceValue;
    private JLabel lblCasesValue;
    private JTable recentLedgerTable;
    private DefaultTableModel ledgerTableModel;

    public EdirGroupDetailPanel(JPanel parentWrapper, EdirService edirService, int groupId) {
        this.parentWrapper = parentWrapper;
        this.edirService = edirService;
        this.groupId = groupId;

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

    public void refreshDashboardMetricsAndLedger() {
        Map<String, String> metrics = edirService.getGroupDetails(groupId);

        if (metrics != null && !metrics.isEmpty()) {
            this.groupName = metrics.getOrDefault("name", "Edir Group");
            lblTitle.setText(this.groupName);

            lblMembersValue.setText(metrics.getOrDefault("total_members", "0") + " " + LanguageManager.getString("edir.detail.active"));
            double balance = 0.0;
            try {
                balance = Double.parseDouble(metrics.getOrDefault("fund_balance", "0.0"));
            } catch (NumberFormatException e) {}
            lblBalanceValue.setText(String.format("%,.2f ETB", balance));
            lblCasesValue.setText(metrics.getOrDefault("active_cases", "0") + " " + LanguageManager.getString("edir.detail.requests"));
        }

        ledgerTableModel.setRowCount(0);

        List<Map<String, String>> ledgerRows = edirService.getGroupTransactionLedger(this.groupId);

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
                    status = LanguageManager.getString("edir.detail.status.disbursed");
                    break;
                case "PENDING_CLAIM":
                    formattedAmount = String.format("%,.2f ETB", amt);
                    status = LanguageManager.getString("edir.detail.status.pending");
                    break;
                case "APPROVED_CLAIM":
                    formattedAmount = String.format("%,.2f ETB", amt);
                    status = LanguageManager.getString("edir.detail.status.approved");
                    break;
                case "REGISTRATION":
                    formattedAmount = "0.00 ETB";
                    status = LanguageManager.getString("edir.detail.status.enrolled");
                    break;
                default:
                    formattedAmount = String.format("+%,.2f ETB", amt);
                    status = LanguageManager.getString("edir.detail.status.cleared");
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

        // ✅ FIXED: Configured with layout safety parameters to avoid character clipping drops
        JButton btnBack = new JButton(LanguageManager.getString("edir.detail.btn_return")) {
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
        btnBack.setFont(FontManager.getBoldFont(13));
        btnBack.setForeground(new Color(101, 31, 16));
        btnBack.setContentAreaFilled(false);
        btnBack.setBorderPainted(false);
        btnBack.setFocusPainted(false);
        // Expanded bounds from 160 to 185 to account for localized Ge'ez text expansions safely
        btnBack.setPreferredSize(new Dimension(185, 38));
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

        lblTitle = new JLabel(groupName);
        lblTitle.setFont(FontManager.getBoldFont(26));
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

        // ✅ Applied Dynamic Font Overrides onto Metrics Card Text Fields
        lblMembersValue = new JLabel("0 " + LanguageManager.getString("edir.detail.active"));
        JPanel card1 = createStatCard(LanguageManager.getString("edir.detail.card.members"), lblMembersValue, new Color(44, 122, 123));

        lblBalanceValue = new JLabel("0.00 ETB");
        JPanel card2 = createStatCard(LanguageManager.getString("edir.detail.card.balance"), lblBalanceValue, new Color(34, 139, 94));

        lblCasesValue = new JLabel("0 " + LanguageManager.getString("edir.detail.requests"));
        JPanel card3 = createStatCard(LanguageManager.getString("edir.detail.card.claims"), lblCasesValue, new Color(197, 48, 48));

        JLabel lblArrearsPlaceholder = new JLabel(LanguageManager.getString("edir.detail.card.notices_val"));
        JPanel card4 = createStatCard(LanguageManager.getString("edir.detail.card.notices"), lblArrearsPlaceholder, new Color(183, 100, 30));

        cardsPanel.add(card1);
        cardsPanel.add(card2);
        cardsPanel.add(card3);
        cardsPanel.add(card4);

        add(cardsPanel);
    }

    private void initActionButtons() {
        JPanel actionPanel = new JPanel(new GridLayout(1, 6, 12, 0));
        actionPanel.setOpaque(false);
        actionPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 50));

        // ✅ Localized Action Buttons Layout
        JButton btnAddMember = createModuleButton(LanguageManager.getString("edir.detail.btn.add_member"), "👤");
        JButton btnRecordContribution = createModuleButton(LanguageManager.getString("edir.detail.btn.contribs"), "💰");
        JButton btnEmergency = createModuleButton(LanguageManager.getString("edir.detail.btn.emergency"), "🚨");
        JButton btnDistribute = createModuleButton(LanguageManager.getString("edir.detail.btn.payout"), "📤");
        JButton btnViewMembers = createModuleButton(LanguageManager.getString("edir.detail.btn.view_members"), "👥");
        JButton btnClearLogs = createModuleButton(LanguageManager.getString("edir.detail.btn.clear_logs"), "🗑️");
        btnClearLogs.setForeground(new Color(175, 30, 20));

        btnAddMember.addActionListener(e -> {
            AddMemberPanel p = new AddMemberPanel(parentWrapper, this.groupId, this);
            parentWrapper.add(p, "AddMember");
            ((CardLayout) parentWrapper.getLayout()).show(parentWrapper, "AddMember");
        });

        btnViewMembers.addActionListener(e -> {
            JPanel membersPanel = new JPanel(new BorderLayout(15, 15));
            membersPanel.setBackground(new Color(253, 247, 237));
            membersPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

            JLabel lblHeading = new JLabel(LanguageManager.getFormattedString("edir.detail.active_records", groupName));
            lblHeading.setFont(FontManager.getBoldFont(18));
            lblHeading.setForeground(new Color(101, 31, 16));
            membersPanel.add(lblHeading, BorderLayout.NORTH);

            // ✅ Localized Table Column Definition Models
            String[] cols = {
                    LanguageManager.getString("edir.detail.col.member_no"),
                    LanguageManager.getString("edir.detail.col.legal_name"),
                    LanguageManager.getString("edir.detail.col.phone"),
                    LanguageManager.getString("edir.detail.col.status"),
                    LanguageManager.getString("edir.detail.col.action")
            };
            DefaultTableModel membersModel = new DefaultTableModel(null, cols) {
                @Override
                public boolean isCellEditable(int r, int c) {
                    return c == 4;
                }
            };

            JTable table = new JTable(membersModel);
            table.setRowHeight(38);
            table.setShowGrid(false);
            table.setFont(FontManager.getPlainFont(13));
            table.getTableHeader().setBackground(new Color(249, 237, 222));
            table.getTableHeader().setFont(FontManager.getBoldFont(13));
            table.getTableHeader().setPreferredSize(new Dimension(0, 36));

            Runnable loadViewData = () -> {
                membersModel.setRowCount(0);
                List<Map<String, String>> membersList = edirService.getMembersByGroup(this.groupId);
                int sequenceNo = 1;
                for (Map<String, String> m : membersList) {
                    membersModel.addRow(new Object[]{
                            String.valueOf(sequenceNo++),
                            m.get("full_name"),
                            m.get("phone"),
                            m.getOrDefault("status", "Active"),
                            m.get("full_name")
                    });
                }
            };
            loadViewData.run();

            table.getColumnModel().getColumn(4).setCellRenderer(new DeleteButtonRenderer());
            table.getColumnModel().getColumn(4).setCellEditor(new DeleteButtonEditor(table, edirService, this.groupId, this.groupName, loadViewData, this));

            JScrollPane scroll = new JScrollPane(table);
            scroll.setBorder(BorderFactory.createLineBorder(new Color(230, 215, 195)));
            membersPanel.add(scroll, BorderLayout.CENTER);

            JButton btnReturn = new JButton(LanguageManager.getString("edir.detail.btn_back_dash"));
            btnReturn.setFont(FontManager.getBoldFont(13));
            btnReturn.setPreferredSize(new Dimension(240, 40));
            btnReturn.addActionListener(ev -> {
                refreshDashboardMetricsAndLedger();
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
            EdirContributionPanel p = new EdirContributionPanel(parentWrapper, edirService, this.groupId);
            parentWrapper.add(p, "EdirContribution");
            ((CardLayout) parentWrapper.getLayout()).show(parentWrapper, "EdirContribution");
        });

        btnEmergency.addActionListener(e -> {
            EmergencyCasePanel p = new EmergencyCasePanel(parentWrapper, edirService, this.groupId);
            parentWrapper.add(p, "EmergencyForm");
            ((CardLayout) parentWrapper.getLayout()).show(parentWrapper, "EmergencyForm");
        });

        btnDistribute.addActionListener(e -> {
            DistributeFundPanel p = new DistributeFundPanel(parentWrapper, edirService, this.groupId);
            parentWrapper.add(p, "DistributeFund");
            ((CardLayout) parentWrapper.getLayout()).show(parentWrapper, "DistributeFund");
        });

        btnClearLogs.addActionListener(e -> {
            UIManager.put("OptionPane.messageFont", FontManager.getPlainFont(14));
            UIManager.put("OptionPane.buttonFont", FontManager.getPlainFont(13));

            int option = JOptionPane.showConfirmDialog(this,
                    LanguageManager.getFormattedString("edir.detail.clear.warn", groupName),
                    LanguageManager.getString("edir.detail.clear.title"),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (option == JOptionPane.YES_OPTION) {
                try {
                    edirService.clearGroupTransactions(this.groupId);
                    JOptionPane.showMessageDialog(this, LanguageManager.getString("edir.detail.clear.success"), LanguageManager.getString("msg.success"), JOptionPane.INFORMATION_MESSAGE);
                    refreshDashboardMetricsAndLedger();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, LanguageManager.getFormattedString("edir.detail.clear.fail", ex.getMessage()), LanguageManager.getString("msg.error"), JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        actionPanel.add(btnAddMember);
        actionPanel.add(btnRecordContribution);
        actionPanel.add(btnEmergency);
        actionPanel.add(btnDistribute);
        actionPanel.add(btnViewMembers);
        actionPanel.add(btnClearLogs);

        add(actionPanel);
    }

    private void initRecentContributions() {
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);

        // ✅ Updated Recent Section Heading Label Layout configuration
        JLabel lblSec = new JLabel(LanguageManager.getString("edir.detail.ledger_title"));
        lblSec.setFont(FontManager.getBoldFont(16));
        lblSec.setForeground(new Color(101, 31, 16));
        lblSec.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        container.add(lblSec, BorderLayout.NORTH);

        // ✅ Localized Columns Model Mapping
        String[] cols = {
                LanguageManager.getString("edir.detail.col.ledger_party"),
                LanguageManager.getString("edir.detail.col.ledger_flow"),
                LanguageManager.getString("edir.detail.col.ledger_desc"),
                LanguageManager.getString("edir.detail.col.ledger_status")
        };
        ledgerTableModel = new DefaultTableModel(null, cols);

        recentLedgerTable = new JTable(ledgerTableModel);
        recentLedgerTable.setRowHeight(40);
        recentLedgerTable.setShowGrid(false);
        recentLedgerTable.setBackground(Color.WHITE);
        recentLedgerTable.setFont(FontManager.getPlainFont(13));
        recentLedgerTable.getTableHeader().setBackground(new Color(249, 237, 222));
        recentLedgerTable.getTableHeader().setFont(FontManager.getBoldFont(13));
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
        lblT.setFont(FontManager.getBoldFont(13));
        lblT.setForeground(Color.GRAY);

        lblValue.setFont(FontManager.getBoldFont(20));
        lblValue.setForeground(textCol);

        card.add(lblT);
        card.add(Box.createVerticalStrut(8));
        card.add(lblValue);
        return card;
    }

    private JButton createModuleButton(String text, String unicodeIcon) {
        // ✅ FIXED: Rely entirely on standard Swing HTML architecture to resolve emoji symbol glyphs smoothly
        JButton btn = new JButton("<html><center><font size='5'>" + unicodeIcon + "</font><br>" + text + "</center></html>") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw solid card backing area shape surface
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));

                // Draw exterior fine-border profiling outline
                g2.setColor(new Color(220, 210, 190));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 14, 14));

                g2.dispose();

                // Let the native framework render the text lines, allowing OS-level emoji fallback routing
                super.paintComponent(g);
            }
        };
        btn.setFont(FontManager.getBoldFont(12));
        btn.setForeground(new Color(101, 31, 16));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.CENTER);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private static class DeleteButtonRenderer extends JButton implements TableCellRenderer {
        public DeleteButtonRenderer() {
            setText(LanguageManager.getString("edir.detail.remove.btn"));
            setFont(FontManager.getBoldFont(11));
            setForeground(new Color(175, 30, 20));
            setBackground(new Color(255, 235, 235));
            setBorderPainted(false);
            setFocusable(false);
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object val, boolean sel, boolean focus, int row, int col) {
            return this;
        }
    }

    private static class DeleteButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private final JButton btn;
        private final JTable table;
        private final EdirService service;

        private final int groupId;
        private final String groupDisplayName;

        private final Runnable reloadViewCallback;
        private final Component parentCtx;
        private String targetMemberName;

        public DeleteButtonEditor(JTable table, EdirService service, int groupId, String groupDisplayName, Runnable reloadViewCallback, Component parentCtx) {
            this.table = table;
            this.service = service;
            this.groupId = groupId;
            this.groupDisplayName = groupDisplayName;
            this.reloadViewCallback = reloadViewCallback;
            this.parentCtx = parentCtx;

            this.btn = new JButton(LanguageManager.getString("edir.detail.remove.btn"));
            this.btn.setFont(FontManager.getBoldFont(11));
            this.btn.setForeground(Color.WHITE);
            this.btn.setBackground(new Color(175, 30, 20));
            this.btn.setBorderPainted(false);

            this.btn.addActionListener(e -> {
                UIManager.put("OptionPane.messageFont", FontManager.getPlainFont(14));
                UIManager.put("OptionPane.buttonFont", FontManager.getPlainFont(13));

                int editingRow = table.getEditingRow();
                if (editingRow == -1) {
                    editingRow = table.getSelectedRow();
                }

                if (editingRow != -1) {
                    Object nameValue = table.getValueAt(editingRow, 1);
                    if (nameValue != null) {
                        targetMemberName = nameValue.toString().trim();
                    }
                }

                if (targetMemberName == null || targetMemberName.isEmpty() || targetMemberName.equalsIgnoreCase(LanguageManager.getString("edir.detail.remove.btn"))) {
                    JOptionPane.showMessageDialog(parentCtx, LanguageManager.getString("edir.detail.remove.err_context"), LanguageManager.getString("msg.error"), JOptionPane.ERROR_MESSAGE);
                    fireEditingStopped();
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(parentCtx,
                        LanguageManager.getFormattedString("edir.detail.remove.confirm", targetMemberName, groupDisplayName),
                        LanguageManager.getString("edir.detail.remove.title"),
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);

                if (confirm == JOptionPane.YES_OPTION) {
                    boolean ok = service.removeMemberFromGroup(this.groupId, targetMemberName);
                    if (ok) {
                        JOptionPane.showMessageDialog(parentCtx, LanguageManager.getFormattedString("edir.detail.remove.success", targetMemberName), LanguageManager.getString("msg.success"), JOptionPane.INFORMATION_MESSAGE);
                        fireEditingStopped();
                        reloadViewCallback.run();
                    } else {
                        JOptionPane.showMessageDialog(parentCtx, LanguageManager.getString("edir.detail.remove.fail"), LanguageManager.getString("msg.error"), JOptionPane.ERROR_MESSAGE);
                        fireEditingStopped();
                    }
                } else {
                    fireEditingStopped();
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable tbl, Object val, boolean isSel, int r, int c) {
            Object fallbackValue = tbl.getValueAt(r, 1);
            this.targetMemberName = (fallbackValue != null) ? fallbackValue.toString().trim() : "";
            return btn;
        }

        @Override
        public Object getCellEditorValue() {
            return targetMemberName;
        }
    }
}