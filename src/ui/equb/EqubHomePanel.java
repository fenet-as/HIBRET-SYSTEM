package ui.equb;

import service.EqubService;
import model.Group;
import util.LanguageManager;
import util.FontManager; // ✅ Imported FontManager
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

        // ✅ Dynamic Language Localization with safe dynamic typography metrics
        JLabel lblTitle = new JLabel(LanguageManager.getString("equb.home.title"));
        lblTitle.setFont(FontManager.getBoldFont(28));
        lblTitle.setForeground(new Color(101, 53, 15));
        headerPanel.add(lblTitle, BorderLayout.WEST);

        // ✅ Dynamic Language Localization
        JButton btnCreate = new JButton(LanguageManager.getString("equb.home.btn_create")) {
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
        btnCreate.setFont(FontManager.getBoldFont(14));
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

        // RE-ALIGNED COLUMN METRICS MATRIX - ✅ Dynamic Language Localization
        String[] columns = {
                LanguageManager.getString("equb.home.col.num"),
                LanguageManager.getString("equb.home.col.name"),
                LanguageManager.getString("equb.home.col.contribution"),
                LanguageManager.getString("equb.home.col.members"),
                LanguageManager.getString("equb.home.col.vault"),
                LanguageManager.getString("equb.home.col.action")
        };
        tableModel = new DefaultTableModel(null, columns) {
            @Override public boolean isCellEditable(int r, int c) { return c == 5; }
        };

        JTable table = new JTable(tableModel);
        table.setRowHeight(42);
        table.setFont(FontManager.getPlainFont(13)); // ✅ Standardized safe table row layout font
        table.getTableHeader().setFont(FontManager.getBoldFont(14));
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

            // ✅ Dynamic Language Localization for formatting rules
            tableModel.addRow(new Object[]{
                    count++,
                    g.getName(),
                    String.format("%,.0f " + LanguageManager.getString("currency.unit"), g.getContributionAmount()),
                    LanguageManager.getFormattedString("equb.home.txt.active", String.valueOf(g.getActiveMemberCount())),
                    String.format("%,.2f " + LanguageManager.getString("currency.unit"), realVaultCashBalance),
                    g
            });
        }
    }

    private static class ActionButtonsRenderer extends JPanel implements TableCellRenderer {
        public ActionButtonsRenderer() {
            setOpaque(true);
            setLayout(new FlowLayout(FlowLayout.CENTER, 8, 6));
            setBackground(Color.WHITE);
            // ✅ Dynamic Language Localization mapped to dynamic typography pipeline layout bindings
            JButton bO = new JButton(LanguageManager.getString("equb.home.btn.open"));
            JButton bD = new JButton(LanguageManager.getString("equb.home.btn.delete"));

            bO.setFont(FontManager.getBoldFont(12));
            bD.setFont(FontManager.getBoldFont(12));
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

            // ✅ Dynamic Language Localization
            JButton btnOpen = new JButton(LanguageManager.getString("equb.home.btn.open"));
            btnOpen.setFont(FontManager.getBoldFont(12));
            btnOpen.addActionListener(e -> {
                fireEditingStopped();
                container.add(new EqubGroupDetailPanel(container, service, currentGroup), "GroupDetail");
                ((CardLayout) container.getLayout()).show(container, "GroupDetail");
            });

            // ✅ Dynamic Language Localization
            JButton btnDelete = new JButton(LanguageManager.getString("equb.home.btn.delete"));
            btnDelete.setFont(FontManager.getBoldFont(12));
            btnDelete.addActionListener(e -> {
                fireEditingStopped();

                // Map prompt options before rendering JOptionPane validation popup container
                UIManager.put("OptionPane.messageFont", FontManager.getPlainFont(14));
                UIManager.put("OptionPane.buttonFont", FontManager.getPlainFont(13));

                // ✅ Dynamic Language Localization
                int option = JOptionPane.showConfirmDialog(panel, LanguageManager.getFormattedString("equb.home.delete.confirm", currentGroup.getName()));
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