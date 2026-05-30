package ui.equb;

import service.EqubService;
import model.Group;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.List;

public class EqubHomePanel extends JPanel {
    private final CardLayout cardLayout;
    private final JPanel containerPanel;
    private final EqubService equbService;
    private final DefaultTableModel tableModel;
    private final int loggedInUserId;

    public EqubHomePanel(JPanel parentContainer, EqubService equbService, int loggedInUserId) {
        this.containerPanel = parentContainer;
        this.equbService = equbService;
        this.loggedInUserId = loggedInUserId;
        this.cardLayout = (CardLayout) parentContainer.getLayout();

        setOpaque(false);
        setLayout(new BorderLayout(0, 20));
        setBorder(BorderFactory.createEmptyBorder(25, 35, 30, 35));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Equb Groups");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblTitle.setForeground(new Color(101, 53, 15));
        headerPanel.add(lblTitle, BorderLayout.WEST);

        JButton btnCreate = new JButton("+ Create New Group") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw custom rounded background
                g2.setColor(new Color(34, 100, 51));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose(); // Safely dispose clone context AFTER background painting

                // ✅ FIXED: Call super with the original, intact graphics object to paint text
                super.paintComponent(g);
            }
        };
        btnCreate.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnCreate.setForeground(Color.WHITE);
        btnCreate.setContentAreaFilled(false);
        btnCreate.setBorderPainted(false);
        btnCreate.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Give the button a fixed padding dimension so the text fits comfortably inside the custom shape
        btnCreate.setPreferredSize(new Dimension(180, 40));

        btnCreate.addActionListener(e -> {
            containerPanel.add(new CreateEqubGroupPanel(containerPanel, equbService, this.loggedInUserId), "CreateGroup");
            cardLayout.show(containerPanel, "CreateGroup");
        });
        headerPanel.add(btnCreate, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // RE-ALIGNED COLUMN METRICS MATRIX
        String[] columns = {"#", "Group Name", "Contribution", "Members Count", "Vault Capital Available", "Action"};
        tableModel = new DefaultTableModel(null, columns) {
            @Override public boolean isCellEditable(int r, int c) { return c == 5; }
        };

        JTable table = new JTable(tableModel);
        table.setRowHeight(42);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(242, 238, 228));

        table.getColumnModel().getColumn(5).setCellRenderer(new ActionButtonsRenderer());
        table.getColumnModel().getColumn(5).setCellEditor(new ActionButtonsEditor(containerPanel, equbService));

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        loadEqubGroupsData();
    }

    public void loadEqubGroupsData() {
        tableModel.setRowCount(0);

        List<Group> dynamicGroups = equbService.getEqubGroupsForUser(this.loggedInUserId);
        int count = 1;
        for (Group g : dynamicGroups) {
            double realVaultCashBalance = equbService.getActualAvailableRoundPool(g.getId());

            tableModel.addRow(new Object[]{
                    count++,
                    g.getName(),
                    String.format("%,.0f birr", g.getContributionAmount()),
                    g.getActiveMemberCount() + " active",
                    String.format("%,.2f birr", realVaultCashBalance),
                    g
            });
        }
    }

    private static class ActionButtonsRenderer extends JPanel implements TableCellRenderer {
        public ActionButtonsRenderer() {
            setOpaque(true);
            setLayout(new FlowLayout(FlowLayout.CENTER, 8, 6));
            setBackground(Color.WHITE);
            JButton bO = new JButton("Open");
            JButton bD = new JButton("Delete");
            add(bO); add(bD);
        }
        @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) {
            return this;
        }
    }

    private static class ActionButtonsEditor extends DefaultCellEditor {
        private final JPanel container;
        private final EqubService service;
        private final JPanel panel;
        private Group currentGroup;

        public ActionButtonsEditor(JPanel container, EqubService service) {
            super(new JCheckBox());
            this.container = container;
            this.service = service;
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 6));

            JButton btnOpen = new JButton("Open");
            btnOpen.addActionListener(e -> {
                fireEditingStopped();
                container.add(new EqubGroupDetailPanel(container, service, currentGroup), "GroupDetail");
                ((CardLayout) container.getLayout()).show(container, "GroupDetail");
            });

            JButton btnDelete = new JButton("Delete");
            btnDelete.addActionListener(e -> {
                fireEditingStopped();
                int option = JOptionPane.showConfirmDialog(panel, "Delete pool " + currentGroup.getName() + "?");
                if (option == JOptionPane.YES_OPTION) {
                    service.deleteEqubGroup(currentGroup.getId());
                    for (Component c : container.getComponents()) {
                        if (c instanceof EqubHomePanel) ((EqubHomePanel) c).loadEqubGroupsData();
                    }
                }
            });
            panel.add(btnOpen); panel.add(btnDelete);
        }
        @Override public Component getTableCellEditorComponent(JTable t, Object v, boolean isS, int r, int c) {
            currentGroup = (Group) v;
            return panel;
        }
        @Override public Object getCellEditorValue() { return currentGroup; }
    }
}