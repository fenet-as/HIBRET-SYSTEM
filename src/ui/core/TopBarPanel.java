package ui.core;

import model.User;
import javax.swing.*;
import java.awt.*;

public class TopBarPanel extends JPanel {
    private final JLabel lblBrand;

    public TopBarPanel(User user, MainFrame frame) {
        setBackground(Color.WHITE);
        setOpaque(true);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(1150, 65));
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(225, 220, 205)));

        JPanel leftContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 12));
        leftContainer.setOpaque(false);

        JButton btnMenu = new JButton("☰");
        btnMenu.setFont(new Font("SansSerif", Font.BOLD, 20));
        btnMenu.setForeground(new Color(60, 60, 60));
        btnMenu.setContentAreaFilled(false);
        btnMenu.setBorderPainted(false);
        btnMenu.setFocusPainted(false);
        btnMenu.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Connect the button to trigger our smooth animation loop
        btnMenu.addActionListener(e -> frame.toggleSidebar());

        // Hardcoded standard English brand heading and typography configuration
        lblBrand = new JLabel("HIBRET SYSTEM");
        lblBrand.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblBrand.setForeground(new Color(34, 112, 43));

        leftContainer.add(btnMenu);
        leftContainer.add(lblBrand);

        add(leftContainer, BorderLayout.WEST);
    }
}