package ui;

import ui.equb.*;
import util.UIStyle;

import javax.swing.*;
import java.awt.*;

public class SidebarPanel extends JPanel {

    public SidebarPanel(MainFrame frame) {

        setPreferredSize(new Dimension(220,700));
        setBackground(new Color(45,30,20));
        setLayout(new GridLayout(0,1,10,10));

        JLabel logo = new JLabel("HIBRET SYSTEM");
        logo.setForeground(Color.WHITE);

        JButton home = new JButton("Dashboard");
        JButton create = new JButton("Create Group");

        UIStyle.styleButton(home);
        UIStyle.styleButton(create);

        home.addActionListener(e -> frame.showScreen("home"));
        create.addActionListener(e -> frame.showScreen("create"));

        add(logo);
        add(home);
        add(create);
    }
}
