package ui.core;

import javax.swing.*;
import java.awt.*;

public class SidebarPanel extends JPanel {
    private final MainFrame parentFrame;

    public SidebarPanel(MainFrame frame) {
        this.parentFrame = frame;

        setBackground(new Color(58, 36, 18));
        setOpaque(true);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        setPreferredSize(new Dimension(240, 655));
        setBorder(BorderFactory.createEmptyBorder(25, 0, 40, 0));

        // Note: The second parameter is the precise routing ID name used in CardLayout
        addNavigationButton("Dashboard", "Dashboard");
        addNavigationButton("Equb Management", "Equb");
        addNavigationButton("Edir Management", "Edir");
        addNavigationButton("Reports", "Reports");
        addNavigationButton("Settings", "Settings");

        add(Box.createVerticalGlue());

        JButton btnLogout = createMenuButton("Logout", "Logout");
        btnLogout.addActionListener(e -> {
            int option = JOptionPane.showConfirmDialog(parentFrame, "Are you sure you want to logout?", "Logout Confirmation", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                parentFrame.dispose();
            }
        });
        add(btnLogout);
    }

    private void addNavigationButton(String label, String routeTarget) {
        JButton btn = createMenuButton(label, routeTarget);
        btn.addActionListener(e -> {
            // Simply invoke the centralized layout framework router
            parentFrame.switchDashboardView(routeTarget);
        });
        add(btn);
        add(Box.createVerticalStrut(4));
    }

    private JButton createMenuButton(String text, String routeTarget) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                // CROSS REFERENCE: Check if this button's target matches the global active layout route
                if (routeTarget.equals(parentFrame.getActiveRoute())) {
                    g2.setColor(new Color(34, 112, 43, 220)); // Clean, solid brand green highlight
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }

                super.paintComponent(g2);
                g2.dispose();
            }
        };

        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        button.setMinimumSize(new Dimension(0, 42));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        button.setPreferredSize(new Dimension(240, 42));

        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

        return button;
    }
}