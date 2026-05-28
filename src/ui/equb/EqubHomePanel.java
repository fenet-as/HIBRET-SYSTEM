package ui.equb;

import model.GroupStore;
import model.SelectedGroup;
import ui.MainFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Dashboard screen showing Equb groups.
 */
public class EqubHomePanel extends JPanel {
    private JTable groupsTable;
    private DefaultTableModel tableModel;

    public EqubHomePanel(MainFrame frame) {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Equb Groups List", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);

        String[] columns = {"#", "Group Name", "Contribution", "Members"};
        tableModel = new DefaultTableModel(columns, 0);
        groupsTable = new JTable(tableModel);

        groupsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && groupsTable.getSelectedRow() != -1) {
                int row = groupsTable.getSelectedRow();
                SelectedGroup.currentGroup = GroupStore.groups.get(row);
                frame.showScreen("Detail");
            }
        });
        loadGroups();
        add(new JScrollPane(groupsTable), BorderLayout.CENTER);

        JButton btnCreate = new JButton("Create New Group");
        btnCreate.addActionListener(e -> frame.showScreen("CreateGroup"));
        add(btnCreate, BorderLayout.SOUTH);

    }
    private void loadGroups() {

        tableModel.setRowCount(0);

        for (int i = 0; i < GroupStore.groups.size(); i++) {

            tableModel.addRow(new Object[]{

                    i + 1,

                    GroupStore.groups.get(i).getName(),

                    GroupStore.groups.get(i).getContribution(),

                    GroupStore.groups.get(i).getMembersCount()
            });
        }
    }
}
