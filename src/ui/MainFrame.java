package ui;

import javax.swing.*;
import java.awt.*;

/**
 * Main application window using CardLayout for navigation.
 */
public class MainFrame extends JFrame {
    private JPanel mainPanel;
    private CardLayout cardLayout;

    public MainFrame() {
        setTitle("HIBRET SYSTEM - Community Management");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Add layout components
        setLayout(new BorderLayout());
        add(new SidebarPanel(this), BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);
    }

    public void addScreen(JPanel panel, String name) {
        mainPanel.add(panel, name);
    }

    public void showScreen(String name) {
        cardLayout.show(mainPanel, "create");
    }

    public void refreshHome() {
        // Logic to refresh dashboard statistics
        System.out.println("Refreshing dashboard...");
    }
}