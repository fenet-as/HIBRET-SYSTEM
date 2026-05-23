package ui.core;

import model.User;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private JPanel centerViewportContainer;
    private CardLayout secondaryCardRouter;

    public MainFrame(User user) {
        setTitle("Hibret System - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setSize(1150, 720);
        setMinimumSize(new Dimension(1050, 650));
        setResizable(true);
        setLocationRelativeTo(null);

        // Natively rendering the solid cream canvas background color
        JPanel masterBackgroundCanvas = new JPanel(new BorderLayout(0, 0));
        masterBackgroundCanvas.setBackground(new Color(245, 238, 220));

        // Initialize structured modules
        TopBarPanel topBar = new TopBarPanel(user);
        SidebarPanel sidebar = new SidebarPanel(this);

        secondaryCardRouter = new CardLayout();
        centerViewportContainer = new JPanel(secondaryCardRouter);
        centerViewportContainer.setOpaque(false);

        // Load Content Routes
        centerViewportContainer.add(new ContentPanel(), "Dashboard");
        centerViewportContainer.add(createPlaceholderPanel("Equb Management View"), "Equb");
        centerViewportContainer.add(createPlaceholderPanel("Edir Management View"), "Edir");
        centerViewportContainer.add(createPlaceholderPanel("Reports View"), "Reports");
        centerViewportContainer.add(createPlaceholderPanel("Settings View"), "Settings");

        // Assemble structural panels onto our color canvas
        masterBackgroundCanvas.add(topBar, BorderLayout.NORTH);
        masterBackgroundCanvas.add(sidebar, BorderLayout.WEST);
        masterBackgroundCanvas.add(centerViewportContainer, BorderLayout.CENTER);

        add(masterBackgroundCanvas);
        setVisible(true);
    }

    public void switchDashboardView(String cardRouteIdentifier) {
        secondaryCardRouter.show(centerViewportContainer, cardRouteIdentifier);
    }

    private JPanel createPlaceholderPanel(String textTitle) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        JLabel label = new JLabel(textTitle);
        label.setFont(new Font("SansSerif", Font.BOLD, 20));
        label.setForeground(new Color(101, 53, 15));
        panel.add(label);
        return panel;
    }
}