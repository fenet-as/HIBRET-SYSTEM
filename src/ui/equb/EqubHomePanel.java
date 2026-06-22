package ui.equb;

import service.EqubService;
import model.Group;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class EqubHomePanel extends JPanel {
    private final CardLayout cardLayout;
    private final JPanel containerPanel;
    private final EqubService equbService;
    private final DefaultTableModel tableModel;
    private final int loggedInUserId;

    private static final Color TEXT_DARK_BROWN = new Color(101, 53, 15);
    private static final Color BUTTON_GREEN = new Color(46, 117, 59);
    private static final Color BUTTON_RED = new Color(160, 40, 20);
    private static final Color TABLE_HEADER_BG = new Color(245, 242, 235);
    private static final Color TABLE_BORDER_COLOR = new Color(230, 225, 210);

    public EqubHomePanel(JPanel parentContainer, EqubService equbService, int loggedInUserId) {
        this.containerPanel = parentContainer;
        this.equbService = equbService;
        this.loggedInUserId = loggedInUserId;
        this.cardLayout = (CardLayout) parentContainer.getLayout();

        setOpaque(false);
        setLayout(new BorderLayout(0, 22));
        setBorder(BorderFactory.createEmptyBorder(25, 35, 40, 35));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Equb Dashboard");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 32));
        lblTitle.setForeground(TEXT_DARK_BROWN);
        headerPanel.add(lblTitle, BorderLayout.WEST);

        JButton btnCreate = new JButton("Create New Equb") {
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
        btnCreate.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnCreate.setForeground(Color.WHITE);
        btnCreate.setContentAreaFilled(false);
        btnCreate.setBorderPainted(false);
        btnCreate.setFocusPainted(false);
        btnCreate.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCreate.setPreferredSize(new Dimension(180, 42));

        btnCreate.addActionListener(e -> {
            containerPanel.add(new CreateEqubGroupPanel(containerPanel, equbService, this.loggedInUserId), "CreateGroup");
            cardLayout.show(containerPanel, "CreateGroup");
        });
        headerPanel.add(btnCreate, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        String[] columns = {
                "No.",
                "Group Name",
                "Amount",
                "Members",
                "Available Money",
                "Actions"
        };
        tableModel = new DefaultTableModel(null, columns) {
            @Override public boolean isCellEditable(int r, int c) { return c == 5; }
        };

        JTable table = new JTable(tableModel);
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

        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(2).setPreferredWidth(120);
        table.getColumnModel().getColumn(3).setPreferredWidth(150);
        table.getColumnModel().getColumn(4).setPreferredWidth(140);
        table.getColumnModel().getColumn(5).setPreferredWidth(220);

        table.getColumnModel().getColumn(5).setCellRenderer(new ActionButtonsRenderer());
        table.getColumnModel().getColumn(5).setCellEditor(new ActionButtonsEditor(containerPanel, equbService));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(TABLE_BORDER_COLOR, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);

        // FIXED: Added here so it fills the table immediately when opened
        loadEqubGroupsData();
    } // Constructor ends here

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

    private static class ActionButtonsRenderer extends JPanel implements TableCellRenderer {
        private final JButton btnView;
        private final JButton btnDelete;

        public ActionButtonsRenderer() {
            setOpaque(true);
            setLayout(new FlowLayout(FlowLayout.CENTER, 8, 8));
            btnView = createStyledActionButton("View", BUTTON_GREEN);
            btnDelete = createStyledActionButton("Delete", BUTTON_RED);
            add(btnView);
            add(btnDelete);
        }
        @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) {
            setBackground(isS ? t.getSelectionBackground() : Color.WHITE);
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

            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
            panel.setOpaque(true);

            JButton btnOpen = createStyledActionButton("View", BUTTON_GREEN);
            btnOpen.addActionListener(e -> {
                Group g = currentGroup;
                fireEditingStopped();
                if (g != null) {
                    container.add(new EqubGroupDetailPanel(container, service, g), "GroupDetail");
                    ((CardLayout) container.getLayout()).show(container, "GroupDetail");
                }
            });

            JButton btnDelete = createStyledActionButton("Delete", BUTTON_RED);
            btnDelete.addActionListener(e -> {
                Group g = currentGroup;
                fireEditingStopped();
                if (g == null) return;

                UIManager.put("OptionPane.messageFont", new Font("SansSerif", Font.PLAIN, 14));
                UIManager.put("OptionPane.buttonFont", new Font("SansSerif", Font.PLAIN, 13));

                int option = JOptionPane.showConfirmDialog(panel,
                        "Are you sure you want to delete '" + g.getName() + "'? This will delete all its history.",
                        "Delete Group",
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

        @Override public Object getCellEditorValue() { return currentGroup; }
    }
}