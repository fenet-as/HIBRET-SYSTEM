package ui;

import javax.swing.*;
import java.awt.*;

/**
 * Dark-themed navigation sidebar.
 */
public class SidebarPanel extends JPanel {
    public SidebarPanel(MainFrame frame) {
        setPreferredSize(new Dimension(250, 800));
        setBackground(new Color(44, 62, 80));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("HIBRET SYSTEM");
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(title);

        JButton btnDashboard = createNavButton("Dashboard", e -> frame.showScreen("Dashboard"));
        JButton btnCreateGroup = createNavButton("Create Equb Group", e -> frame.showScreen("CreateGroup"));

        add(btnDashboard);
        add(btnCreateGroup);
    }

    private JButton createNavButton(String text, java.awt.event.ActionListener action) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(230, 40));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(52, 73, 94));
        btn.setFocusPainted(false);
        btn.addActionListener(action);
        return btn;
    }
}
