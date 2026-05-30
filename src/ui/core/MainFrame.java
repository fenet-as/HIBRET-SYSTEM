package ui.core;

import model.User;
import service.ReportService;
import service.EqubService;
import service.EdirService;
import ui.equb.EqubHomePanel;
import ui.edir.EdirHomePanel;
import ui.settings.SettingsPanel;
import session.Session;
import ui.reports.ReportHomePanel;
import util.LanguageManager;
import util.FontManager; // ✅ Imported FontManager

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {
    private JPanel centerViewportContainer;
    private CardLayout secondaryCardRouter;
    private SidebarPanel sidebar;
    private TopBarPanel topBar;
    private final ReportService reportService;
    private final EqubService equbService;
    private final EdirService edirService;
    private final User loggedInUser;

    // Single Source of Truth for the active page route
    private String activeRoute = "Dashboard";

    // Animation Properties
    private Timer animationTimer;
    private final int MAX_WIDTH = 240;
    private final int MIN_WIDTH = 0;
    private int currentWidth = MAX_WIDTH;
    private boolean isExpanded = true;

    public MainFrame(User user, ReportService reportService, EqubService equbService, EdirService edirService) {
        this.loggedInUser = user;
        this.reportService = reportService;
        this.equbService = equbService;
        this.edirService = edirService;

        // Attach the logged-in User profile directly to the Global Session context immediately
        Session.setCurrentUser(user);

        // ✅ Dynamic Internationalized Frame Title
        updateFrameTitle();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Make window open full screen by default
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1050, 700));
        setLocationRelativeTo(null);

        JPanel masterBackgroundCanvas = new JPanel(new BorderLayout(0, 0));
        masterBackgroundCanvas.setBackground(new Color(245, 238, 220));

        topBar = new TopBarPanel(user, this);
        sidebar = new SidebarPanel(this);

        secondaryCardRouter = new CardLayout();
        centerViewportContainer = new JPanel(secondaryCardRouter);
        centerViewportContainer.setOpaque(false);

        // Build initial components
        rebuildCenterViewport();

        masterBackgroundCanvas.add(topBar, BorderLayout.NORTH);
        masterBackgroundCanvas.add(sidebar, BorderLayout.WEST);
        masterBackgroundCanvas.add(centerViewportContainer, BorderLayout.CENTER);

        add(masterBackgroundCanvas);
        setVisible(true);
    }

    /**
     * ✅ Helper method to set window title dynamically based on local language and explicit font mapping configuration
     */
    private void updateFrameTitle() {
        // Enforce fallback mappings inside the main window native OS handle
        String titleText = LanguageManager.getString("app.title") + LanguageManager.getString("frame.title_suffix");
        setTitle(titleText);

        // Globally configures any dynamic Swing popup windows (like JOptionPanes) to match the dynamic text script
        UIManager.put("OptionPane.messageFont", FontManager.getPlainFont(14));
        UIManager.put("OptionPane.buttonFont", FontManager.getPlainFont(13));
    }

    /**
     * ✅ Re-creates panel instances in the CardLayout matrix to seamlessly pick up fresh strings on the fly
     */
    private void rebuildCenterViewport() {
        centerViewportContainer.removeAll();

        ContentPanel dashboardContent = new ContentPanel(this, loggedInUser.getId());
        JScrollPane contentScroll = new JScrollPane(dashboardContent);
        contentScroll.setOpaque(false);
        contentScroll.getViewport().setOpaque(false);
        contentScroll.setBorder(null);
        contentScroll.getVerticalScrollBar().setUnitIncrement(16);

        // --- VIEWS ROUTING MATRIX INTEGRATION LAYER ---
        centerViewportContainer.add(contentScroll, "Dashboard");

        // --- EQUB MODULE CARD ROUTING WRAPPER ---
        JPanel equbModuleCardWrapper = new JPanel(new CardLayout());
        equbModuleCardWrapper.setOpaque(false);

        EqubHomePanel equbGridLandingScreen = new EqubHomePanel(equbModuleCardWrapper, equbService, loggedInUser.getId());
        equbModuleCardWrapper.add(equbGridLandingScreen, "EqubHome");
        centerViewportContainer.add(equbModuleCardWrapper, "Equb");

        // --- EDIR MODULE DYNAMIC CARD CONTAINER ROUTING WRAPPER ---
        JPanel edirModuleCardWrapper = new JPanel(new CardLayout());
        edirModuleCardWrapper.setOpaque(false);

        EdirHomePanel edirGridLandingScreen = new EdirHomePanel(edirModuleCardWrapper, edirService, loggedInUser.getId());
        edirModuleCardWrapper.add(edirGridLandingScreen, "EdirHome");
        centerViewportContainer.add(edirModuleCardWrapper, "Edir");

        // --- REPORTS MODULE CARD ROUTING LAYER ---
        centerViewportContainer.add(new ReportHomePanel(this, reportService), "Reports");

        // --- LIVE SETTINGS PANEL ---
        SettingsPanel liveSettingsView = new SettingsPanel(centerViewportContainer, loggedInUser);
        centerViewportContainer.add(liveSettingsView, "Settings");

        centerViewportContainer.revalidate();
        centerViewportContainer.repaint();
    }

    /**
     * ✅ PUBLIC ACCESS SWEEPER: Called whenever language properties are changed from anywhere in the system
     */
    public void reloadLanguageContext() {
        updateFrameTitle();

        // Rebuild and refresh panels to instantly match chosen language locale properties
        rebuildCenterViewport();

        // Re-route clean display mapping to active workspace route context
        switchDashboardView(activeRoute);

        // ✅ Notifies top navigation layout panels to refresh structural text bundles
        if (topBar != null) {
            // If topBar has a rebuild or language refresh method, execute it here:
            topBar.revalidate();
            topBar.repaint();
        }

        if (sidebar != null) {
            // If sidebar has an internal menu item re-layout strategy, force refresh context updates
            sidebar.revalidate();
            sidebar.repaint();
        }
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

        // AUTO-REFRESH TRIGGER: When switching to Dashboard, refresh live metrics
        if (cardRouteIdentifier.equalsIgnoreCase("Dashboard")) {
            for (Component viewComponent : centerViewportContainer.getComponents()) {
                if (viewComponent instanceof JScrollPane) {
                    JScrollPane scrollPane = (JScrollPane) viewComponent;
                    Component innerView = scrollPane.getViewport().getView();
                    if (innerView instanceof ContentPanel) {
                        ((ContentPanel) innerView).refreshData();
                    }
                }
            }
        }

        // AUTO-REFRESH TRIGGER: When switching to Equb, force data to reload immediately
        else if (cardRouteIdentifier.equalsIgnoreCase("Equb")) {
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

        // AUTO-REFRESH TRIGGER: When switching to Edir, reload database groups dynamically on screen
        else if (cardRouteIdentifier.equalsIgnoreCase("Edir")) {
            for (Component viewComponent : centerViewportContainer.getComponents()) {
                if (viewComponent instanceof JPanel && ((JPanel) viewComponent).getLayout() instanceof CardLayout) {
                    JPanel wrapperPanel = (JPanel) viewComponent;
                    for (Component subComp : wrapperPanel.getComponents()) {
                        if (subComp instanceof EdirHomePanel) {
                            ((EdirHomePanel) subComp).loadGroups();
                            ((CardLayout) wrapperPanel.getLayout()).show(wrapperPanel, "EdirHome");
                        }
                    }
                }
            }
        }

        // AUTO-REFRESH TRIGGER: When a user enters Reports, force the inner panels to reload fresh database entries
        else if (cardRouteIdentifier.equalsIgnoreCase("Reports")) {
            for (Component viewComponent : centerViewportContainer.getComponents()) {
                if (viewComponent instanceof ReportHomePanel) {
                    ((ReportHomePanel) viewComponent).refreshSubReportsContext();
                }
            }
        }

        secondaryCardRouter.show(centerViewportContainer, cardRouteIdentifier);

        if (sidebar != null) {
            sidebar.repaint();
        }
    }

    public String getActiveRoute() {
        return this.activeRoute;
    }

    private JPanel createPlaceholderPanel(String textTitle) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        JLabel label = new JLabel(textTitle);
        // ✅ Replaced hardcoded font family declaration with FontManager mappings
        label.setFont(FontManager.getBoldFont(20));
        label.setForeground(new Color(101, 53, 15));
        panel.add(label);
        return panel;
    }
}