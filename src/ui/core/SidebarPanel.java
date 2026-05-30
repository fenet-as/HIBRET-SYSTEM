package ui.core;

import javax.swing.*;
import java.awt.*;
import util.LanguageManager;
import util.FontManager; // ✅ Imported FontManager

public class SidebarPanel extends JPanel {
    private final MainFrame parentFrame;

    public SidebarPanel(MainFrame frame) {
        this.parentFrame = frame;

        setBackground(new Color(58, 36, 18));
        setOpaque(true);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        setPreferredSize(new Dimension(240, 655));
        setBorder(BorderFactory.createEmptyBorder(25, 0, 40, 0));

        // Build the localized UI layout structure
        rebuildMenu();
    }

    /**
     * ✅ Clears old buttons and rebuilds them with the active language and target font mapping
     */
    public void rebuildMenu() {
        this.removeAll();

        // 1. Re-add navigation items bound to the dynamic Amharic font
        addNavigationButton(LanguageManager.getString("sidebar.dashboard"), "Dashboard");
        addNavigationButton(LanguageManager.getString("sidebar.equb"), "Equb");
        addNavigationButton(LanguageManager.getString("sidebar.edir"), "Edir");
        addNavigationButton(LanguageManager.getString("sidebar.reports"), "Reports");
        addNavigationButton(LanguageManager.getString("sidebar.settings"), "Settings");

        add(Box.createVerticalGlue());

        // 2. Localized Logout Control
        JButton btnLogout = createMenuButton(LanguageManager.getString("sidebar.logout"), "Logout");
        btnLogout.addActionListener(e -> {
            // Apply dynamic fonts directly to the runtime confirmation dialog text
            UIManager.put("OptionPane.messageFont", FontManager.getPlainFont(14));
            UIManager.put("OptionPane.buttonFont", FontManager.getPlainFont(13));

            int option = JOptionPane.showConfirmDialog(
                    parentFrame,
                    LanguageManager.getString("sidebar.logout.confirm"),
                    LanguageManager.getString("sidebar.logout.title"),
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

    private void addNavigationButton(String localizedLabel, String routeTarget) {
        JButton btn = createMenuButton(localizedLabel, routeTarget);
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

        // ✅ Replaced hardcoded "SansSerif" with dynamic FontManager mapping
        button.setFont(FontManager.getBoldFont(13));
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