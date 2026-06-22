package ui.edir;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;
import java.util.Map;
import service.EdirService;
import model.Group;

public class EdirHomePanel extends JPanel {
    private JTable groupTable;
    private DefaultTableModel tableModel;
    private final JPanel parentWrapper;
    private final EdirService edirService;
    private final int loggedInUserId;

    private static final Color TEXT_DARK_BROWN = new Color(101, 53, 15);
    private static final Color BUTTON_GREEN = new Color(46, 117, 59);
    private static final Color BUTTON_RED = new Color(160, 40, 20);
    private static final Color TABLE_HEADER_BG = new Color(245, 242, 235);
    private static final Color TABLE_BORDER_COLOR = new Color(230, 225, 210);

    public EdirHomePanel(JPanel parentWrapper, EdirService edirService, int loggedInUserId) {
        this.parentWrapper = parentWrapper;
        this.edirService = edirService;
        this.loggedInUserId = loggedInUserId;

        setOpaque(false);
        setLayout(new BorderLayout(0, 22));
        setBorder(BorderFactory.createEmptyBorder(25, 35, 40, 35));

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

            tableModel.addRow(new Object[]{
                    counter++,
                    groupObj.getName(),
                    String.format("%,.0f ETB", groupObj.getContributionAmount()),
                    groupObj.getActiveMemberCount() + " Members",
                    String.format("%,.2f ETB", synchronizedNetLedgerBalance),
                    groupId
            });
        }
    }

    private void initHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Edir Dashboard"); // Simplified from "Edir Group Dashboard"
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 32));
        lblTitle.setForeground(TEXT_DARK_BROWN);

        JButton btnCreateGroup = new JButton("Create New Edir") { // Simplified from "Create New Association"
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BUTTON_GREEN);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnCreateGroup.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnCreateGroup.setForeground(Color.WHITE);
        btnCreateGroup.setContentAreaFilled(false);
        btnCreateGroup.setBorderPainted(false);
        btnCreateGroup.setFocusPainted(false);
        btnCreateGroup.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCreateGroup.setPreferredSize(new Dimension(180, 42));

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
        // --- SIMPLIFIED TABLE HEADERS ---
        String[] columns = {
                "No.",
                "Group Name",          // Changed from "Community Group Name"
                "Monthly Fee",         // Changed from "Required Monthly Fee"
                "Members",             // Changed from "Active Members"
                "Available Money",     // Changed from "Total Capital Balance"
                "Actions"              // Changed from "Actions Management"
        };
        tableModel = new DefaultTableModel(null, columns) {
            @Override public boolean isCellEditable(int row, int column) { return column == 5; }
        };

        groupTable = new JTable(tableModel);
        groupTable.setRowHeight(46);
        groupTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        groupTable.setForeground(new Color(40, 40, 40));
        groupTable.setGridColor(TABLE_BORDER_COLOR);
        groupTable.setSelectionBackground(new Color(242, 238, 228));
        groupTable.setSelectionForeground(TEXT_DARK_BROWN);
        groupTable.setFillsViewportHeight(true);

        JTableHeader header = groupTable.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 14));
        header.setBackground(TABLE_HEADER_BG);
        header.setForeground(TEXT_DARK_BROWN);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, TABLE_BORDER_COLOR));

        groupTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        groupTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        groupTable.getColumnModel().getColumn(2).setPreferredWidth(140);
        groupTable.getColumnModel().getColumn(3).setPreferredWidth(130);
        groupTable.getColumnModel().getColumn(4).setPreferredWidth(140);
        groupTable.getColumnModel().getColumn(5).setPreferredWidth(220);

        groupTable.getColumnModel().getColumn(5).setCellRenderer(new TableActionRenderer());
        groupTable.getColumnModel().getColumn(5).setCellEditor(new TableActionEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(groupTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(TABLE_BORDER_COLOR, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);
    }

    private static JButton createStyledActionButton(String text, Color background) {
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
        button.setPreferredSize(new Dimension(90, 30));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private class TableActionRenderer extends JPanel implements TableCellRenderer {
        private final JButton btnView;
        private final JButton btnDelete;

        public TableActionRenderer() {
            setOpaque(true);
            setLayout(new FlowLayout(FlowLayout.CENTER, 8, 8));
            btnView = createStyledActionButton("View", BUTTON_GREEN);
            btnDelete = createStyledActionButton("Delete", BUTTON_RED);
            add(btnView);
            add(btnDelete);
        }
        @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean f, int r, int c) {
            setBackground(isS ? t.getSelectionBackground() : Color.WHITE);
            return this;
        }
    }

    private class TableActionEditor extends DefaultCellEditor {
        private final JPanel panel;
        private int currentEditingRow;

        public TableActionEditor(JCheckBox checkBox) {
            super(checkBox);
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
            panel.setOpaque(true);

            JButton openBtn = createStyledActionButton("View", BUTTON_GREEN);
            JButton deleteBtn = createStyledActionButton("Delete", BUTTON_RED);

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
                UIManager.put("OptionPane.messageFont", new Font("SansSerif", Font.PLAIN, 14));
                UIManager.put("OptionPane.buttonFont", new Font("SansSerif", Font.PLAIN, 13));

                int row = currentEditingRow;
                fireEditingStopped();

                if (row >= 0 && row < groupTable.getRowCount()) {
                    String groupName = (String) groupTable.getValueAt(row, 1);
                    Object value = groupTable.getValueAt(row, 5);

                    if (value != null && !value.toString().trim().isEmpty()) {
                        int groupId = (value instanceof Number) ? ((Number) value).intValue() : Integer.parseInt(value.toString().trim());

                        // --- SIMPLIFIED CONFIRMATION MESSAGE ---
                        int confirm = JOptionPane.showConfirmDialog(panel,
                                "Are you sure you want to delete '" + groupName + "'? This will delete all its history.",
                                "Delete Group",
                                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                        if (confirm == JOptionPane.YES_OPTION) {
                            boolean deleted = edirService.deleteGroup(groupId);
                            if (deleted) {
                                JOptionPane.showMessageDialog(panel, "Group deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                                loadGroups();
                            } else {
                                JOptionPane.showMessageDialog(panel, "Could not delete the group.", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                }
            });

            panel.add(openBtn);
            panel.add(deleteBtn);
        }

        @Override public Component getTableCellEditorComponent(JTable t, Object v, boolean isS, int r, int c) {
            this.currentEditingRow = r;
            panel.setBackground(t.getSelectionBackground());
            return panel;
        }

        @Override public Object getCellEditorValue() {
            if (currentEditingRow >= 0 && currentEditingRow < groupTable.getRowCount()) {
                return groupTable.getValueAt(currentEditingRow, 5);
            }
            return null;
        }
    }
}