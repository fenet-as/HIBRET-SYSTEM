package ui.reports;

import ui.core.MainFrame;
import service.ReportService;
import util.LanguageManager;
import util.FontManager; // ✅ Imported FontManager
import javax.swing.*;
import java.awt.*;

public class ReportHomePanel extends JPanel {
    private final MainFrame mainFrame;
    private final JPanel cardsRouterContainer;
    private final CardLayout localSubRouter;
    private final ReportService reportService;

    public ReportHomePanel(MainFrame mainFrame, ReportService reportService) {
        this.mainFrame = mainFrame;
        this.reportService = reportService;

        setOpaque(false);

        localSubRouter = new CardLayout();
        cardsRouterContainer = new JPanel(localSubRouter);
        cardsRouterContainer.setOpaque(false);
        setLayout(new BorderLayout());

        // --- SUB-VIEW ROUTE DEFINITIONS ---
        JPanel selectionDashboard = createSelectionDashboard();
        cardsRouterContainer.add(selectionDashboard, "HomeSelection");

        cardsRouterContainer.add(new MemberReportPanel(this, reportService), "MemberReport");
        cardsRouterContainer.add(new EqubReportPanel(this, reportService), "EqubReport");
        cardsRouterContainer.add(new EdirReportPanel(this, reportService), "EdirReport");
        cardsRouterContainer.add(new SystemReportPanel(this, reportService), "SystemReport");

        add(cardsRouterContainer, BorderLayout.CENTER);
        localSubRouter.show(cardsRouterContainer, "HomeSelection");
    }

    public void navigateToSubReport(String subReportRouteKey) {
        localSubRouter.show(cardsRouterContainer, subReportRouteKey);
    }

    /**
     * ✅ LIFECYCLE HOOK DETECTOR:
     * Invoked automatically by MainFrame when a user clicks the Sidebar menu tab.
     * Iterates down to notify the inner member panel to drop old indices and fetch fresh rows.
     */
    /**
     * UPDATED LIFECYCLE HOOKS:
     * Forwards notifications cleanly down to any active subview dashboards.
     */
    public void refreshSubReportsContext() {
        if (cardsRouterContainer != null) {
            for (Component comp : cardsRouterContainer.getComponents()) {
                if (comp instanceof MemberReportPanel) {
                    ((MemberReportPanel) comp).refreshViewOnLifecycleSignal();
                } else if (comp instanceof SystemReportPanel) {
                    ((SystemReportPanel) comp).refreshViewOnLifecycleSignal();
                } else if (comp instanceof EqubReportPanel) {
                    ((EqubReportPanel) comp).refreshViewOnLifecycleSignal();
                } else if (comp instanceof EdirReportPanel) {
                    // ✅ FIXED: Forwards the notification signal down to the Edir dashboard view
                    ((EdirReportPanel) comp).refreshViewOnLifecycleSignal();
                }
            }
        }
    }

    private JPanel createSelectionDashboard() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 35, 40, 35));

        // Top visual tracking details - Localized Title Label Context
        JLabel lblTitle = new JLabel(LanguageManager.getString("report.home.title"));
        lblTitle.setFont(FontManager.getBoldFont(32)); // ✅ Integrated FontManager
        lblTitle.setForeground(new Color(101, 53, 15));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblTitle);
        panel.add(Box.createVerticalStrut(35));

        JPanel grid = new JPanel(new GridLayout(1, 4, 20, 0)) {
            @Override
            public Dimension getMaximumSize() {
                return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
            }
        };
        grid.setOpaque(false);
        grid.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ✅ Bound Tiles to Language Resource String Bundles
        grid.add(createSelectionTile("👤", LanguageManager.getString("report.home.tile.member"), "MemberReport", new Color(46, 117, 59)));
        grid.add(createSelectionTile("👥", LanguageManager.getString("report.home.tile.equb"), "EqubReport", new Color(184, 91, 23)));
        grid.add(createSelectionTile("❤️", LanguageManager.getString("report.home.tile.edir"), "EdirReport", new Color(214, 60, 43)));
        grid.add(createSelectionTile("📊", LanguageManager.getString("report.home.tile.system"), "SystemReport", new Color(33, 91, 166)));

        panel.add(grid);
        return panel;
    }

    private JButton createSelectionTile(String icon, String title, String targetRoute, Color accentTheme) {
        String htmlTitle = "<html><center>" + title.replaceAll("\n", "<br>") + "</center></html>";
        JButton tile = new JButton(htmlTitle) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 2, 16, 16);
                g2.setColor(new Color(230, 224, 210));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 2, 16, 16);

                g2.setFont(FontManager.getPlainFont(46)); // ✅ Integrated FontManager
                g2.setColor(accentTheme);
                FontMetrics fm = g2.getFontMetrics();
                int iconX = (getWidth() - fm.stringWidth(icon)) / 2;
                g2.drawString(icon, iconX, 75);

                super.paintComponent(g2);
                g2.dispose();
            }
        };

        tile.setFont(FontManager.getBoldFont(15)); // ✅ Integrated FontManager
        tile.setForeground(new Color(101, 53, 15));
        tile.setContentAreaFilled(false);
        tile.setBorderPainted(false);
        tile.setFocusPainted(false);
        tile.setPreferredSize(new Dimension(190, 220));
        tile.setMinimumSize(new Dimension(130, 180));
        tile.setCursor(new Cursor(Cursor.HAND_CURSOR));

        tile.setVerticalAlignment(SwingConstants.BOTTOM);
        tile.setHorizontalAlignment(SwingConstants.CENTER);
        tile.setBorder(BorderFactory.createEmptyBorder(0, 10, 25, 10));

        tile.addActionListener(e -> navigateToSubReport(targetRoute));
        return tile;
    }
}