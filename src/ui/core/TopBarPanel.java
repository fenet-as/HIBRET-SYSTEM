package ui.core;

import model.User;
import javax.swing.*;
import java.awt.*;

public class TopBarPanel extends JPanel {

    public TopBarPanel(User user) {
        // Enforcing solid styling rules natively
        setBackground(Color.WHITE);
        setOpaque(true);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(1150, 65));
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(225, 220, 205)));

        // Left Branding Section
        JPanel leftContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 12));
        leftContainer.setOpaque(false);

        JButton btnMenu = new JButton("☰");
        btnMenu.setFont(new Font("SansSerif", Font.BOLD, 20));
        btnMenu.setForeground(new Color(60, 60, 60));
        btnMenu.setContentAreaFilled(false);
        btnMenu.setBorderPainted(false);
        btnMenu.setFocusPainted(false);
        btnMenu.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel lblBrand = new JLabel("HIBRET SYSTEM");
        lblBrand.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblBrand.setForeground(new Color(34, 112, 43));

        leftContainer.add(btnMenu);
        leftContainer.add(lblBrand);

        // Right Profiles and Dropdown Operations
        JPanel rightContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 12));
        rightContainer.setOpaque(false);

        String[] languages = {"English ", "አማርኛ "};
        JComboBox<String> comboLang = new JComboBox<>(languages);
        comboLang.setFont(new Font("SansSerif", Font.PLAIN, 13));
        comboLang.setPreferredSize(new Dimension(110, 30));
        comboLang.setFocusable(false);

        JPanel avatarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(101, 53, 15));
                g2.fillOval(0, 0, 32, 32);
                g2.setColor(new Color(210, 195, 175));
                g2.fillOval(2, 2, 28, 28);
                g2.dispose();
            }
        };
        avatarPanel.setPreferredSize(new Dimension(32, 32));
        avatarPanel.setOpaque(false);

        rightContainer.add(comboLang);
        rightContainer.add(avatarPanel);

        add(leftContainer, BorderLayout.WEST);
        add(rightContainer, BorderLayout.EAST);
    }
}