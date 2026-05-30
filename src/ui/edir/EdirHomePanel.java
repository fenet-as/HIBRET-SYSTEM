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

public class EdirHomePanel extends JPanel {
    private JTable groupTable;
    private DefaultTableModel tableModel;
    private JButton btnCreateGroup;

    // Core references for routing & database
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

    // Database dynamic loading function directly mapped with your transactions Postgres backend
    public void loadGroups() {
        tableModel.setRowCount(0);

        List<Map<String, String>> rawGroups = edirService.getEdirGroupsForUser(this.loggedInUserId);
        int counter = 1;

        for (Map<String, String> rowMap : rawGroups) {
            Group groupObj = Group.fromMap(rowMap);

            // ✅ FIXED: References the functional context string lookup getGroupBalance(String) to match your stack implementation
            double synchronizedNetLedgerBalance = edirService.getGroupBalance(groupObj.getName());

            tableModel.addRow(new Object[]{
                    String.valueOf(counter++),
                    groupObj.getName(),
                    String.format("%,.0f birr", groupObj.getContributionAmount()),
                    String.valueOf(groupObj.getActiveMemberCount()),
                    String.format("%,.2f birr", synchronizedNetLedgerBalance), // Displays real dynamic live ledger balance
                    ""
            });
        }
    }

    private void initHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Edir Groups");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 26));
        lblTitle.setForeground(new Color(101, 31, 16));

        btnCreateGroup = new JButton("+ Create New Group") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(28, 85, 163));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose(); // Safely dispose custom context clone background asset layer

                // ✅ FIXED: Calls the native text layout painter safely on the default intact canvas pipeline context
                super.paintComponent(g);
            }
        };
        btnCreateGroup.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnCreateGroup.setForeground(Color.WHITE);
        btnCreateGroup.setContentAreaFilled(false);
        btnCreateGroup.setBorderPainted(false);
        btnCreateGroup.setFocusPainted(false);
        btnCreateGroup.setPreferredSize(new Dimension(180, 40));

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
        String[] columns = {"#", "Group Name", "Monthly Fee", "Members", "Fund Balance", "Action"};
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
        groupTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        groupTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        groupTable.getTableHeader().setBackground(new Color(249, 237, 222));
        groupTable.getTableHeader().setForeground(new Color(101, 31, 16));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        groupTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        groupTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        groupTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        groupTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

        groupTable.getColumnModel().getColumn(5).setCellRenderer(new TableActionRenderer());
        groupTable.getColumnModel().getColumn(5).setCellEditor(new TableActionEditor(new JCheckBox()));

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
            add(createActionButton("Open", new Color(46, 117, 89)));
            add(createActionButton("Delete", new Color(217, 83, 79)));
        }
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
            return this;
        }
    }

    class TableActionEditor extends DefaultCellEditor {
        private JPanel panel;

        public TableActionEditor(JCheckBox checkBox) {
            super(checkBox);
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 10));
            panel.setBackground(Color.WHITE);

            JButton openBtn = createActionButton("Open", new Color(46, 117, 89));
            JButton deleteBtn = createActionButton("Delete", new Color(217, 83, 79));

            openBtn.addActionListener(e -> {
                int selectedRow = groupTable.getSelectedRow();
                fireEditingStopped();
                if(selectedRow != -1) {
                    String groupName = (String) groupTable.getValueAt(selectedRow, 1);

                    EdirGroupDetailPanel detailPanel = new EdirGroupDetailPanel(parentWrapper, edirService, groupName);
                    parentWrapper.add(detailPanel, "EdirDetail");

                    CardLayout innerLayout = (CardLayout) parentWrapper.getLayout();
                    innerLayout.show(parentWrapper, "EdirDetail");
                }
            });

            deleteBtn.addActionListener(e -> {
                int selectedRow = groupTable.getSelectedRow();
                fireEditingStopped();
                if (selectedRow != -1) {
                    String groupName = (String) groupTable.getValueAt(selectedRow, 1);

                    int confirm = JOptionPane.showConfirmDialog(panel,
                            "Are you completely sure you want to delete '" + groupName + "'? All financial histories will be removed.",
                            "Confirm Group Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                    if (confirm == JOptionPane.YES_OPTION) {
                        boolean deleted = edirService.deleteGroup(groupName);
                        if (deleted) {
                            JOptionPane.showMessageDialog(panel, "Group deleted successfully.");
                            loadGroups();
                        } else {
                            JOptionPane.showMessageDialog(panel, "Failed to remove group record.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            });

            panel.add(openBtn);
            panel.add(deleteBtn);
        }
        @Override
        public Component getTableCellEditorComponent(JTable t, Object v, boolean s, int r, int c) {
            return panel;
        }
    }

    private static JButton createActionButton(String text, Color color) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 8, 8));
                g2.dispose();

                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setForeground(color);
        btn.setBackground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(70, 30));
        return btn;
    }
}