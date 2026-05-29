package ui.edir;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import service.EdirService;

public class EdirHomePanel extends JPanel {
    private JTable groupTable;
    private DefaultTableModel tableModel;
    private JButton btnCreateGroup;

    private final JPanel parentWrapper;
    private final EdirService edirService;

    public EdirHomePanel(JPanel parentWrapper, EdirService edirService) {
        this.parentWrapper = parentWrapper;
        this.edirService = edirService;

        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(253, 247, 237));
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        initHeader();
        initTable();
        loadGroups();
    }

    public void loadGroups() {
        tableModel.setRowCount(0);

        // Mock data populating columns: #, Group Name, Monthly Fee, Members, Fund Balance, Action
        tableModel.addRow(new Object[]{"1", "Neighborhood Edir", "200 birr", "35", "80,000 birr", ""});
        tableModel.addRow(new Object[]{"2", "Family Edir", "300 birr", "20", "45,000 birr", ""});
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
                g2.dispose();
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
            CreateEdirGroupPanel createPanel = new CreateEdirGroupPanel(parentWrapper, edirService);
            parentWrapper.add(createPanel, "CreateEdirGroup");
            CardLayout cl = (CardLayout) parentWrapper.getLayout();
            cl.show(parentWrapper, "CreateEdirGroup");
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
        private final JPanel panel;
        public TableActionEditor(JCheckBox checkBox) {
            super(checkBox);
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 10));
            panel.setBackground(Color.WHITE);
            JButton openBtn = createActionButton("Open", new Color(46, 117, 89));
            JButton deleteBtn = createActionButton("Delete", new Color(217, 83, 79));

            openBtn.addActionListener(e -> {
                fireEditingStopped();
                int selectedRow = groupTable.getSelectedRow();
                if(selectedRow != -1) {
                    String groupName = (String) groupTable.getValueAt(selectedRow, 1);
                    EdirGroupDetailPanel detailPanel = new EdirGroupDetailPanel(parentWrapper, edirService, groupName);
                    parentWrapper.add(detailPanel, "EdirDetail");
                    CardLayout innerLayout = (CardLayout) parentWrapper.getLayout();
                    innerLayout.show(parentWrapper, "EdirDetail");
                }
            });

            deleteBtn.addActionListener(e -> {
                fireEditingStopped();
                int selectedRow = groupTable.getSelectedRow();
                if(selectedRow != -1) {
                    String groupName = (String) groupTable.getValueAt(selectedRow, 1);
                    int res = JOptionPane.showConfirmDialog(panel, "Are you sure you want to delete " + groupName + "?", "Confirm Action", JOptionPane.YES_NO_OPTION);
                    if(res == JOptionPane.YES_OPTION) {
                        loadGroups();
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
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(70, 30));
        return btn;
    }
}