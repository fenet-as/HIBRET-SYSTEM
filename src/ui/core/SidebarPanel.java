package ui.core;

import javax.swing.*;
import java.awt.*;

public class SidebarPanel extends JPanel {
    private final MainFrame parentFrame;
    private String activeItem = "Dashboard";

    public SidebarPanel(MainFrame frame) {
        this.parentFrame = frame;

        // Locking the sidebar color natively to your design guidelines
        setBackground(new Color(58, 36, 18));
        setOpaque(true);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        setPreferredSize(new Dimension(215, 655));
        setBorder(BorderFactory.createEmptyBorder(25, 0, 40, 0));

        addNavigationButton("Dashboard");
        addNavigationButton("Equb Management");
        addNavigationButton("Edir Management");
        addNavigationButton("Reports");
        addNavigationButton("Settings");

        add(Box.createVerticalGlue());

        JButton btnLogout = createMenuButton("Logout");
        btnLogout.addActionListener(e -> {
            int option = JOptionPane.showConfirmDialog(parentFrame, "Are you sure you want to logout?", "Logout Confirmation", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                parentFrame.dispose();
            }
        });
        add(btnLogout);
    }

    private void addNavigationButton(String label) {
        JButton btn = createMenuButton(label);
        btn.addActionListener(e -> {
            activeItem = label;
            String pageTarget = label.split(" ")[0];
            parentFrame.switchDashboardView(pageTarget);
            repaint();
        });
        add(btn);
        add(Box.createVerticalStrut(4));
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                if (text.equals(activeItem)) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(34, 112, 43, 220)); // Clean, solid brand green highlight
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };

        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(215, 42));
        button.setPreferredSize(new Dimension(215, 42));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

        return button;
    }
}