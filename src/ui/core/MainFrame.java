package ui.core;

import model.User;
import service.ReportService;
import service.EqubService;
import service.EdirService;          // ⭐ HOOKED EDIR SERVICE INTERFACE
import ui.equb.EqubHomePanel;
import ui.edir.EdirHomePanel;       // ⭐ IMPORTED EDIR HOME PANEL MODULE
import ui.settings.SettingsPanel;   // ✅ IMPORTED NEW SETTINGS MODULE

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {
    private JPanel centerViewportContainer;
    private CardLayout secondaryCardRouter;
    private SidebarPanel sidebar;
    private final ReportService reportService;
    private final EqubService equbService;
    private final EdirService edirService;   // ⭐ EDIR SERVICE STORAGE FIELD REGISTERED

    // Single Source of Truth for the active page route
    private String activeRoute = "Dashboard";

    // Animation Properties
    private Timer animationTimer;
    private final int MAX_WIDTH = 240;
    private final int MIN_WIDTH = 0;
    private int currentWidth = MAX_WIDTH;
    private boolean isExpanded = true;

    // ⭐ UPDATED CONSTRUCTOR TO ACCEPT EDIRSERVICE INSTANCE AS WELL
    public MainFrame(User user, ReportService reportService, EqubService equbService, EdirService edirService) {
        this.reportService = reportService;
        this.equbService = equbService;
        this.edirService = edirService;      // ✅ ASSIGNED DEPENDENCY TRACE

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

        ContentPanel dashboardContent = new ContentPanel(this, user.getId());
        JScrollPane contentScroll = new JScrollPane(dashboardContent);
        contentScroll.setOpaque(false);
        contentScroll.getViewport().setOpaque(false);
        contentScroll.setBorder(null);
        contentScroll.getVerticalScrollBar().setUnitIncrement(16); // Smooth scrolling

        // --- VIEWS ROUTING MATRIX INTEGRATION LAYER ---
        centerViewportContainer.add(contentScroll, "Dashboard");

        // --- EQUB MODULE CARD ROUTING WRAPPER ---
        JPanel equbModuleCardWrapper = new JPanel(new CardLayout());
        equbModuleCardWrapper.setOpaque(false);

        // ✅ FIXED: Extracted user.getId() context and passed it as the 3rd argument
        EqubHomePanel equbGridLandingScreen = new EqubHomePanel(equbModuleCardWrapper, equbService, user.getId());
        equbModuleCardWrapper.add(equbGridLandingScreen, "EqubHome");
        centerViewportContainer.add(equbModuleCardWrapper, "Equb");

        // --- ⭐ EDIR MODULE DYNAMIC CARD CONTAINER ROUTING WRAPPER ---
        JPanel edirModuleCardWrapper = new JPanel(new CardLayout());
        edirModuleCardWrapper.setOpaque(false);

        // ✅ FIXED: Extracted user.getId() context and passed it as the 3rd argument here as well
        EdirHomePanel edirGridLandingScreen = new EdirHomePanel(edirModuleCardWrapper, edirService, user.getId());
        edirModuleCardWrapper.add(edirGridLandingScreen, "EdirHome");

        // Register the dynamic Edir wrapper directly onto the root viewport switcher (Replacing placeholder)
        centerViewportContainer.add(edirModuleCardWrapper, "Edir");

        // Remaining placeholders/modules wired up
        centerViewportContainer.add(new ui.reports.ReportHomePanel(this, reportService), "Reports");

        // --- ✅ CONNECTED LIVE SETTINGS PANEL (WITH USER CONTEXT PASSED) ---
        SettingsPanel liveSettingsView = new SettingsPanel(centerViewportContainer, user);
        centerViewportContainer.add(liveSettingsView, "Settings");

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

        // AUTO-REFRESH TRIGGER: When switching to Equb, force data to reload immediately
        if (cardRouteIdentifier.equalsIgnoreCase("Equb")) {
            for (Component viewComponent : centerViewportContainer.getComponents()) {
                if (viewComponent instanceof JPanel && ((JPanel) viewComponent).getLayout() instanceof CardLayout) {
                    JPanel wrapperPanel = (JPanel) viewComponent;
                    for (Component subComp : wrapperPanel.getComponents()) {
                        if (subComp instanceof EqubHomePanel) {
                            ((EqubHomePanel) subComp).loadEqubGroupsData();
                            ((CardLayout) wrapperPanel.getLayout()).show(wrapperPanel, "EqubHome");
                        }
                    }
                }
            }
        }

        // ⭐ AUTO-REFRESH TRIGGER: When switching to Edir, reload database groups dynamically on screen
        else if (cardRouteIdentifier.equalsIgnoreCase("Edir")) {
            for (Component viewComponent : centerViewportContainer.getComponents()) {
                if (viewComponent instanceof JPanel && ((JPanel) viewComponent).getLayout() instanceof CardLayout) {
                    JPanel wrapperPanel = (JPanel) viewComponent;
                    for (Component subComp : wrapperPanel.getComponents()) {
                        if (subComp instanceof EdirHomePanel) {
                            ((EdirHomePanel) subComp).loadGroups(); // Refreshes table rows straight from PostgreSQL
                            ((CardLayout) wrapperPanel.getLayout()).show(wrapperPanel, "EdirHome"); // Resets layout back to grid home view
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