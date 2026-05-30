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

        JLabel lblTitle = new JLabel("Equb Group Dashboard");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblTitle.setForeground(new Color(101, 53, 15));
        headerPanel.add(lblTitle, BorderLayout.WEST);

        JButton btnCreate = new JButton("Create New Equb") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(34, 100, 51));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnCreate.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnCreate.setForeground(Color.WHITE);
        btnCreate.setContentAreaFilled(false);
        btnCreate.setBorderPainted(false);
        btnCreate.setFocusPainted(false);
        btnCreate.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCreate.setPreferredSize(new Dimension(180, 40));

        btnCreate.addActionListener(e -> {
            containerPanel.add(new CreateEqubGroupPanel(containerPanel, equbService, this.loggedInUserId), "CreateGroup");
            cardLayout.show(containerPanel, "CreateGroup");
        });
        headerPanel.add(btnCreate, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        String[] columns = {
                "No.",
                "Group Pool Name",
                "Cycle Rate",
                "Active Subscriptions",
                "Vault Balance",
                "Actions Management"
        };
        tableModel = new DefaultTableModel(null, columns) {
            @Override public boolean isCellEditable(int r, int c) { return c == 5; }
        };

        JTable table = new JTable(tableModel);
        table.setRowHeight(42);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(242, 238, 228));

        // Setup the Action Button custom renderer and editors
        table.getColumnModel().getColumn(5).setCellRenderer(new ActionButtonsRenderer());
        table.getColumnModel().getColumn(5).setCellEditor(new ActionButtonsEditor(containerPanel, equbService));

        // ✅ FIXED: Enforce explicit column widths so the buttons fit perfectly side-by-side
        table.getColumnModel().getColumn(0).setPreferredWidth(50);   // No.
        table.getColumnModel().getColumn(1).setPreferredWidth(180);  // Group Pool Name
        table.getColumnModel().getColumn(2).setPreferredWidth(110);  // Cycle Rate
        table.getColumnModel().getColumn(3).setPreferredWidth(140);  // Active Subscriptions
        table.getColumnModel().getColumn(4).setPreferredWidth(130);  // Vault Balance
        table.getColumnModel().getColumn(5).setPreferredWidth(230);  // Actions Management (Wide enough for buttons side-by-side)

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
                    String.format("%,.0f ETB", g.getContributionAmount()),
                    g.getActiveMemberCount() + " Members",
                    String.format("%,.2f ETB", realVaultCashBalance),
                    g
            });
        }
    }

    private static class ActionButtonsRenderer extends JPanel implements TableCellRenderer {
        private final JButton bO;
        private final JButton bD;

        public ActionButtonsRenderer() {
            setOpaque(true);
            // ✅ FIXED: Using FlowLayout with minimal vertical gap keeps them perfectly adjacent on 1 row
            setLayout(new FlowLayout(FlowLayout.CENTER, 8, 4));

            bO = new JButton("View Ledger");
            bD = new JButton("Purge Pool");

            bO.setFont(new Font("SansSerif", Font.BOLD, 12));
            bD.setFont(new Font("SansSerif", Font.BOLD, 12));

            bO.setFocusPainted(false);
            bD.setFocusPainted(false);

            add(bO);
            add(bD);
        }

        @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) {
            if (isS) {
                setBackground(t.getSelectionBackground());
            } else {
                setBackground(t.getBackground());
            }
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

            // ✅ FIXED: Match the flow layout gap configurations used in the renderer
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
            panel.setOpaque(true);

            JButton btnOpen = new JButton("View Ledger");
            btnOpen.setFont(new Font("SansSerif", Font.BOLD, 12));
            btnOpen.setFocusPainted(false);
            btnOpen.addActionListener(e -> {
                Group g = currentGroup;
                fireEditingStopped();
                if (g != null) {
                    container.add(new EqubGroupDetailPanel(container, service, g), "GroupDetail");
                    ((CardLayout) container.getLayout()).show(container, "GroupDetail");
                }
            });

            JButton btnDelete = new JButton("Purge Pool");
            btnDelete.setFont(new Font("SansSerif", Font.BOLD, 12));
            btnDelete.setFocusPainted(false);
            btnDelete.addActionListener(e -> {
                Group g = currentGroup;
                fireEditingStopped();

                if (g == null) return;

                UIManager.put("OptionPane.messageFont", new Font("SansSerif", Font.PLAIN, 14));
                UIManager.put("OptionPane.buttonFont", new Font("SansSerif", Font.PLAIN, 13));

                int option = JOptionPane.showConfirmDialog(panel,
                        "Are you certain you want to permanently delete '" + g.getName() + "' and clear all associated records?",
                        "Confirm Deletion",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                if (option == JOptionPane.YES_OPTION) {
                    service.deleteEqubGroup(g.getId());

                    for (Component comp : container.getComponents()) {
                        if (comp instanceof EqubHomePanel) {
                            ((EqubHomePanel) comp).loadEqubGroupsData();
                        }
                    }
                }
            });
            panel.add(btnOpen);
            panel.add(btnDelete);
        }

        @Override public Component getTableCellEditorComponent(JTable t, Object v, boolean isS, int r, int c) {
            currentGroup = (Group) v;
            panel.setBackground(t.getSelectionBackground());
            return panel;
        }

        @Override public Object getCellEditorValue() {
            return currentGroup;
        }
    }
}