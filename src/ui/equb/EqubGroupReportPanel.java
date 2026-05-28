package ui.equb;

import model.SelectedGroup;
import javax.swing.*;

/**
 * Displays summary reports for the selected group.
 */
public class EqubGroupReportPanel extends JPanel {
    public EqubGroupReportPanel() {
        if (SelectedGroup.currentGroup != null) {
            add(new JLabel("Total Members: " + SelectedGroup.currentGroup.getMembersList().size()));
            add(new JLabel("Total Funds: " + SelectedGroup.currentGroup.getTotal()));
        }
    }
}
