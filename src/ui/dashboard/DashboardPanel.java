package ui.dashboard;

import javax.swing.*;
import java.awt.*;

public class DashboardPanel extends JPanel {

    public DashboardPanel() {

        setLayout(new BorderLayout());

        JLabel label = new JLabel("DASHBOARD MODULE");
        label.setHorizontalAlignment(SwingConstants.CENTER);

        add(label, BorderLayout.CENTER);
    }
}