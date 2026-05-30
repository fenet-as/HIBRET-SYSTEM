package ui.edir;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import java.util.Map;
import service.EdirService;
import model.Group;
import util.LanguageManager;
import util.FontManager;

public class EdirHomePanel extends JPanel {
    private JTable groupTable;
    private DefaultTableModel tableModel;
    private JButton btnCreateGroup;

    private JPanel parentWrapper;
    private EdirService edirService;
    private int loggedInUserId;

    public EdirHomePanel(JPanel parentWrapper, EdirService edirService, int loggedInUserId) {
        this.parentWrapper = parentWrapper;
        this.edirService = edirService;
        this.loggedInUserId = loggedInUserId;

        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(253, 247, 237));
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        initHeader();
        initTable();
        loadGroups();
    }

    public void loadGroups() {
        tableModel.setRowCount(0);

        List<Map<String, String>> rawGroups = edirService.getEdirGroupsForUser(this.loggedInUserId);
        int counter = 1;

        for (Map<String, String> rowMap : rawGroups) {
            Group groupObj = Group.fromMap(rowMap);

            String rawId = rowMap.get("id");
            int groupId = (rawId == null || rawId.trim().isEmpty()) ? 0 : Integer.parseInt(rawId.trim());

            double synchronizedNetLedgerBalance = edirService.getGroupBalance(groupId);

            // Clean number formatting with commas
            String feeStr = String.format("%,.0f", groupObj.getContributionAmount());
            String balanceStr = String.format("%,.2f", synchronizedNetLedgerBalance);

            // ✅ FIXED: We are passing just 'feeStr' and 'balanceStr' directly.
            // This bypasses LanguageManager entirely for these columns, displaying only numbers.
            tableModel.addRow(new Object[]{
                    String.valueOf(counter++),
                    groupObj.getName(),
                    feeStr,
                    String.valueOf(groupObj.getActiveMemberCount()),
                    balanceStr,
                    groupId
            });
        }
    }

    private void initHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel(LanguageManager.getString("edir.home.title"));
        lblTitle.setFont(FontManager.getBoldFont(26));
        lblTitle.setForeground(new Color(101, 31, 16));

        btnCreateGroup = new JButton(LanguageManager.getString("edir.home.btn_create")) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(28, 85, 163));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose();

                super.paintComponent(g);
            }
        };
        btnCreateGroup.setFont(FontManager.getBoldFont(14));
        btnCreateGroup.setForeground(Color.WHITE);
        btnCreateGroup.setContentAreaFilled(false);
        btnCreateGroup.setBorderPainted(false);
        btnCreateGroup.setFocusPainted(false);
        btnCreateGroup.setPreferredSize(new Dimension(200, 40));

        btnCreateGroup.addActionListener(e -> {
            boolean found = false;
            for (Component c : parentWrapper.getComponents()) {
                if (c instanceof CreateEdirGroupPanel) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                parentWrapper.add(new CreateEdirGroupPanel(parentWrapper, edirService, this.loggedInUserId), "CreateEdirGroup");
            }

            CardLayout innerLayout = (CardLayout) parentWrapper.getLayout();
            innerLayout.show(parentWrapper, "CreateEdirGroup");
        });

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(btnCreateGroup, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);
    }

    private void initTable() {
        String[] columns = {
                LanguageManager.getString("edir.home.col.index"),
                LanguageManager.getString("edir.home.col.name"),
                LanguageManager.getString("edir.home.col.fee"),
                LanguageManager.getString("edir.home.col.members"),
                LanguageManager.getString("edir.home.col.balance"),
                LanguageManager.getString("edir.home.col.actions")
        };
        tableModel = new DefaultTableModel(null, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };

        groupTable = new JTable(tableModel);
        groupTable.setRowHeight(50);
        groupTable.setBackground(Color.WHITE);
        groupTable.setShowGrid(false);
        groupTable.setIntercellSpacing(new Dimension(0, 0));
        groupTable.setFont(FontManager.getPlainFont(14));
        groupTable.getTableHeader().setFont(FontManager.getBoldFont(14));
        groupTable.getTableHeader().setBackground(new Color(249, 237, 222));
        groupTable.getTableHeader().setForeground(new Color(101, 31, 16));

        // Center rendering configuration
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        centerRenderer.setFont(FontManager.getPlainFont(14));

        groupTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        groupTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        groupTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        groupTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

        groupTable.getColumnModel().getColumn(5).setCellRenderer(new TableActionRenderer());
        groupTable.getColumnModel().getColumn(5).setCellEditor(new TableActionEditor(new JCheckBox()));

        groupTable.getColumnModel().getColumn(5).setPreferredWidth(210);
        groupTable.getColumnModel().getColumn(5).setMinWidth(210);

        JScrollPane scrollPane = new JScrollPane(groupTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 215, 195), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);
    }

    class TableActionRenderer extends JPanel implements TableCellRenderer {
        public TableActionRenderer() {
            setOpaque(true);
            setBackground(Color.WHITE);
            setLayout(new FlowLayout(FlowLayout.CENTER, 8, 10));
            add(createActionButton(LanguageManager.getString("edir.home.action.open"), new Color(46, 117, 89)));
            add(createActionButton(LanguageManager.getString("edir.home.action.delete"), new Color(217, 83, 79)));
        }
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
            return this;
        }
    }

    class TableActionEditor extends DefaultCellEditor {
        private final JPanel panel;
        private int currentEditingRow;

        public TableActionEditor(JCheckBox checkBox) {
            super(checkBox);
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 10));
            panel.setBackground(Color.WHITE);

            JButton openBtn = createActionButton(LanguageManager.getString("edir.home.action.open"), new Color(46, 117, 89));
            JButton deleteBtn = createActionButton(LanguageManager.getString("edir.home.action.delete"), new Color(217, 83, 79));

            openBtn.addActionListener(e -> {
                int row = currentEditingRow;
                fireEditingStopped();

                if (row >= 0 && row < groupTable.getRowCount()) {
                    Object value = groupTable.getValueAt(row, 5);
                    if (value != null && !value.toString().trim().isEmpty()) {
                        int groupId = (value instanceof Number) ? ((Number) value).intValue() : Integer.parseInt(value.toString().trim());

                        EdirGroupDetailPanel detailPanel = new EdirGroupDetailPanel(parentWrapper, edirService, groupId);
                        parentWrapper.add(detailPanel, "EdirDetail");

                        CardLayout innerLayout = (CardLayout) parentWrapper.getLayout();
                        innerLayout.show(parentWrapper, "EdirDetail");
                    }
                }
            });

            deleteBtn.addActionListener(e -> {
                UIManager.put("OptionPane.messageFont", FontManager.getPlainFont(14));
                UIManager.put("OptionPane.buttonFont", FontManager.getPlainFont(13));

                int row = currentEditingRow;
                fireEditingStopped();

                if (row >= 0 && row < groupTable.getRowCount()) {
                    String groupName = (String) groupTable.getValueAt(row, 1);

                    Object value = groupTable.getValueAt(row, 5);
                    if (value != null && !value.toString().trim().isEmpty()) {
                        int groupId = (value instanceof Number) ? ((Number) value).intValue() : Integer.parseInt(value.toString().trim());

                        int confirm = JOptionPane.showConfirmDialog(panel,
                                LanguageManager.getFormattedString("edir.home.delete.confirm", groupName),
                                LanguageManager.getString("edir.home.delete.title"),
                                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                        if (confirm == JOptionPane.YES_OPTION) {
                            boolean deleted = edirService.deleteGroup(groupId);
                            if (deleted) {
                                JOptionPane.showMessageDialog(panel, LanguageManager.getString("edir.home.delete.success"), LanguageManager.getString("msg.success"), JOptionPane.INFORMATION_MESSAGE);
                                loadGroups();
                            } else {
                                JOptionPane.showMessageDialog(panel, LanguageManager.getString("edir.home.delete.fail"), LanguageManager.getString("msg.error"), JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                }
            });

            panel.add(openBtn);
            panel.add(deleteBtn);
        }

        @Override
        public Component getTableCellEditorComponent(JTable t, Object v, boolean s, int r, int c) {
            this.currentEditingRow = r;
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            if (currentEditingRow >= 0 && currentEditingRow < groupTable.getRowCount()) {
                return groupTable.getValueAt(currentEditingRow, 5);
            }
            return null;
        }
    }

    private static JButton createActionButton(String text, Color color) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 8, 8));

                g2.setColor(color);
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 8, 8));
                g2.dispose();

                super.paintComponent(g);
            }
        };
        btn.setFont(FontManager.getBoldFont(12));
        btn.setForeground(color);
        btn.setBackground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(90, 30));
        return btn;
    }
}