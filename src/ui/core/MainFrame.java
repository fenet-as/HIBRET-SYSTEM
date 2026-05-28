package ui.core;

import model.User;
import service.ReportService;
import service.EqubService;         // ⭐ ADDED BACKEND DEPENDENCY HOOK
import ui.equb.EqubHomePanel;       // ⭐ IMPORT THE HOME VIEW GRID MODULE
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {
    private JPanel centerViewportContainer;
    private CardLayout secondaryCardRouter;
    private SidebarPanel sidebar;
    private final ReportService reportService;
    private final EqubService equbService;   // ⭐ FIELD STORAGE REGISTERED

    // Single Source of Truth for the active page route
    private String activeRoute = "Dashboard";

    // Animation Properties
    private Timer animationTimer;
    private final int MAX_WIDTH = 240;
    private final int MIN_WIDTH = 0;
    private int currentWidth = MAX_WIDTH;
    private boolean isExpanded = true;

    // ⭐ UPDATED CONSTRUCTOR TO ACCEPT EQUBSERVICE INSTANCE AS WELL
    public MainFrame(User user, ReportService reportService, EqubService equbService) {
        this.reportService = reportService;
        this.equbService = equbService;      // ✅ ASSIGNED DEPENDENCY TRACE

        setTitle("Hibret System - Integrated Management Framework");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Make window open full screen by default
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
        contentScroll.getVerticalScrollBar().setUnitIncrement(16); // Smooth scrolling

        // --- VIEWS ROUTING MATRIX INTEGRATION LAYER ---
        centerViewportContainer.add(contentScroll, "Dashboard");

        // ⭐ REMOVED PLACEHOLDER: INJECTING DYNAMIC CARD CONTAINER BASE LAYER FOR EQUB
        JPanel equbModuleCardWrapper = new JPanel(new CardLayout());
        equbModuleCardWrapper.setOpaque(false);

        // Build the landing grid panel passing our sub-routing container & backend services
        EqubHomePanel equbGridLandingScreen = new EqubHomePanel(equbModuleCardWrapper, equbService);
        equbModuleCardWrapper.add(equbGridLandingScreen, "EqubHome");

        // Register the dynamic Equb wrapper directly onto the root viewport switcher
        centerViewportContainer.add(equbModuleCardWrapper, "Equb");

        // Remaining placeholders/modules wired up
        centerViewportContainer.add(createPlaceholderPanel("Edir Management View"), "Edir");

        // WIRED BACKEND HOOK: Injected both MainFrame reference and ReportService backend
        centerViewportContainer.add(new ui.reports.ReportHomePanel(this, reportService), "Reports");

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

    // Centralized route switcher framework
    public void switchDashboardView(String cardRouteIdentifier) {
        this.activeRoute = cardRouteIdentifier;

        // ⭐ AUTO-REFRESH TRIGGER: When switching to Equb, force data to reload immediately
        if (cardRouteIdentifier.equalsIgnoreCase("Equb")) {
            for (Component viewComponent : centerViewportContainer.getComponents()) {
                if (viewComponent instanceof JPanel && ((JPanel) viewComponent).getLayout() instanceof CardLayout) {
                    JPanel wrapperPanel = (JPanel) viewComponent;
                    for (Component subComp : wrapperPanel.getComponents()) {
                        if (subComp instanceof EqubHomePanel) {
                            ((EqubHomePanel) subComp).loadEqubGroupsData(); // Refreshes grid rows
                            ((CardLayout) wrapperPanel.getLayout()).show(wrapperPanel, "EqubHome"); // Resets state back to home grid
                        }
                    }
                }
            }
        }

        secondaryCardRouter.show(centerViewportContainer, cardRouteIdentifier);

        if (sidebar != null) {
            sidebar.repaint(); // Triggers sidebar menu items background recolor highlights
        }
    }

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