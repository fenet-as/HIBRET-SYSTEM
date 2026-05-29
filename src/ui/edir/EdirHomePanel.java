package ui.edir;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import model.EdirGroup;
import service.EdirService;
import service.impl.EdirServiceImpl;

public class EdirHomePanel extends JPanel {
    private final JPanel containerPanel;
    private final EdirService edirService = new EdirServiceImpl();
    private JTable table;
    private DefaultTableModel model;

    public EdirHomePanel(JPanel containerPanel) {
        this.containerPanel = containerPanel;
        setOpaque(false);
        setLayout(new BorderLayout(0, 20));
        setBorder(BorderFactory.createEmptyBorder(25, 35, 30, 35));

        // Top Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel title = new JLabel("❤️ Edir Management System");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(new Color(101, 53, 15));
        headerPanel.add(title, BorderLayout.WEST);

        JButton btnCreate = new JButton("+ Create Edir Group");
        btnCreate.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnCreate.addActionListener(e -> {
            containerPanel.add(new CreateEdirGroupPanel(containerPanel, this), "CreateEdirGroup");
            ((CardLayout) containerPanel.getLayout()).show(containerPanel, "CreateEdirGroup");
        });
        headerPanel.add(btnCreate, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Core Table Registry Layout
        model = new DefaultTableModel(new String[]{"ID", "Group Name", "Target Contribution (Birr)"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(35);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));

        // Optional: Keep double-click as a shortcut alternative, but buttons will be explicit
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    openSelectedGroup();
                }
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);

        // ⭐ NEW: Action Buttons Panel (Matches Equb Styling & Workflow)
        JPanel actionButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        actionButtonPanel.setOpaque(false);

        JButton btnOpen = new JButton("📂 Open Group Details");
        btnOpen.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnOpen.addActionListener(e -> openSelectedGroup());

        JButton btnDelete = new JButton("🗑️ Delete Selected Group");
        btnDelete.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnDelete.setBackground(new Color(180, 40, 40));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.addActionListener(e -> deleteSelectedGroup());

        actionButtonPanel.add(btnOpen);
        actionButtonPanel.add(btnDelete);

        add(actionButtonPanel, BorderLayout.SOUTH);

        loadGroups();
    }

    // Helper method to safely pull data context and switch views
    private void openSelectedGroup() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an Edir group from the table first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) table.getValueAt(row, 0);
        String name = (String) table.getValueAt(row, 1);
        double contrib = (double) table.getValueAt(row, 2);
        EdirGroup selected = new EdirGroup(id, name, contrib, null);

        containerPanel.add(new EdirGroupDetailPanel(containerPanel, selected), "EdirGroupDetail");
        ((CardLayout) containerPanel.getLayout()).show(containerPanel, "EdirGroupDetail");
    }

    // Helper method to issue safe database delete routines
    private void deleteSelectedGroup() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an Edir group to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) table.getValueAt(row, 0);
        String name = (String) table.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you absolutely sure you want to delete '" + name + "'?\nThis action will erase the group configuration registry.",
                "Confirm Delete Action",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            edirService.deleteEdirGroup(id);
            JOptionPane.showMessageDialog(this, "Edir association successfully deleted.");
            loadGroups(); // Instantly refresh table UI rows straight from PostgreSQL
        }
    }

    public void loadGroups() {
        model.setRowCount(0);
        List<EdirGroup> groups = edirService.getAllEdirGroups();
        for (EdirGroup g : groups) {
            model.addRow(new Object[]{g.getId(), g.getName(), g.getContribution()});
        }
    }
}