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

        // Build the English UI layout structure
        rebuildMenu();
    }

    /**
     * Clears old buttons and rebuilds them with plain English strings and standard fonts
     */
    public void rebuildMenu() {
        this.removeAll();

        // 1. Add navigation items using plain English labels
        addNavigationButton("Dashboard", "Dashboard");
        addNavigationButton("Equb Management", "Equb");
        addNavigationButton("Edir Management", "Edir");
        addNavigationButton("Financial Reports", "Reports");
        addNavigationButton("System Settings", "Settings");

        add(Box.createVerticalGlue());

        // 2. Standard Logout Control
        JButton btnLogout = createMenuButton("Logout", "Logout");
        btnLogout.addActionListener(e -> {
            UIManager.put("OptionPane.messageFont", new Font("SansSerif", Font.PLAIN, 14));
            UIManager.put("OptionPane.buttonFont", new Font("SansSerif", Font.PLAIN, 13));

            int option = JOptionPane.showConfirmDialog(
                    parentFrame,
                    "Are you sure you want to log out of the system?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION
            );
            if (option == JOptionPane.YES_OPTION) {
                parentFrame.dispose();
            }
        });
        add(btnLogout);

        this.revalidate();
        this.repaint();
    }

    private void addNavigationButton(String plainTextLabel, String routeTarget) {
        JButton btn = createMenuButton(plainTextLabel, routeTarget);
        btn.addActionListener(e -> {
            // Invoke the centralized dashboard layout router framework
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

                // Check if this button's internal target matches the global active workspace layout route
                if (routeTarget.equals(parentFrame.getActiveRoute())) {
                    g2.setColor(new Color(34, 112, 43, 220)); // Brand green highlight block
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