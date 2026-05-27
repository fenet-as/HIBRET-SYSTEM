package ui.core;

import model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {
    private JPanel centerViewportContainer;
    private CardLayout secondaryCardRouter;
    private SidebarPanel sidebar;

    // Single Source of Truth for the active page route
    private String activeRoute = "Dashboard";

    // Animation Properties
    private Timer animationTimer;
    private final int MAX_WIDTH = 240;
    private final int MIN_WIDTH = 0;
    private int currentWidth = MAX_WIDTH;
    private boolean isExpanded = true;

    public MainFrame(User user) {
        setTitle("Hibret System - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1050, 700));
        setLocationRelativeTo(null);

        JPanel masterBackgroundCanvas = new JPanel(new BorderLayout(0, 0));
        masterBackgroundCanvas.setBackground(new Color(245, 238, 220));

        TopBarPanel topBar = new TopBarPanel(user, this);
        sidebar = new SidebarPanel(this); // Sidebar reads state directly from 'this' frame

        secondaryCardRouter = new CardLayout();
        centerViewportContainer = new JPanel(secondaryCardRouter);
        centerViewportContainer.setOpaque(false);

        ContentPanel dashboardContent = new ContentPanel(this);
        JScrollPane contentScroll = new JScrollPane(dashboardContent);
        contentScroll.setOpaque(false);
        contentScroll.getViewport().setOpaque(false);
        contentScroll.setBorder(null);
        contentScroll.getVerticalScrollBar().setUnitIncrement(16);

        centerViewportContainer.add(contentScroll, "Dashboard");
        centerViewportContainer.add(createPlaceholderPanel("Equb Management View"), "Equb");
        centerViewportContainer.add(createPlaceholderPanel("Edir Management View"), "Edir");
        centerViewportContainer.add(createPlaceholderPanel("Reports View"), "Reports");
        centerViewportContainer.add(createPlaceholderPanel("Settings View"), "Settings");

        masterBackgroundCanvas.add(topBar, BorderLayout.NORTH);
        masterBackgroundCanvas.add(sidebar, BorderLayout.WEST);
        masterBackgroundCanvas.add(centerViewportContainer, BorderLayout.CENTER);

        add(masterBackgroundCanvas);
        setVisible(true);
    }

    public void toggleSidebar() {
        if (animationTimer != null && animationTimer.isRunning()) {
            return;
        }

        int targetWidth = isExpanded ? MIN_WIDTH : MAX_WIDTH;
        int step = isExpanded ? -20 : 20;

        animationTimer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentWidth += step;

                if ((step < 0 && currentWidth <= targetWidth) || (step > 0 && currentWidth >= targetWidth)) {
                    currentWidth = targetWidth;
                    animationTimer.stop();
                    isExpanded = !isExpanded;
                }

                sidebar.setPreferredSize(new Dimension(currentWidth, sidebar.getHeight()));
                sidebar.revalidate();
            }
        });

        animationTimer.start();
    }

    // Dynamic global view routing mechanism
    public void switchDashboardView(String cardRouteIdentifier) {
        this.activeRoute = cardRouteIdentifier; // Keep tracking updated state
        secondaryCardRouter.show(centerViewportContainer, cardRouteIdentifier);

        // Force the sidebar component to repaint itself to refresh highlight highlights
        if (sidebar != null) {
            sidebar.repaint();
        }
    }

    // Getter so SidebarPanel can check what page is currently visible
    public String getActiveRoute() {
        return this.activeRoute;
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