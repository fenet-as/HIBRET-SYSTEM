package ui.reports;

import ui.core.MainFrame;
import service.ReportService; // 1. Added import for ReportService
import javax.swing.*;
import java.awt.*;

public class ReportHomePanel extends JPanel {
    private final MainFrame mainFrame;
    private final JPanel cardsRouterContainer;
    private final CardLayout localSubRouter;
    private final ReportService reportService; // 2. Added private final ReportService field

    // 3. Modified constructor to accept ReportService along with MainFrame
    public ReportHomePanel(MainFrame mainFrame, ReportService reportService) {
        this.mainFrame = mainFrame;
        this.reportService = reportService; // 4. Assigned it inside the constructor

        setOpaque(false);

        localSubRouter = new CardLayout();
        cardsRouterContainer = new JPanel(localSubRouter);
        cardsRouterContainer.setOpaque(false);
        setLayout(new BorderLayout());

        // --- SUB-VIEW ROUTE DEFINITIONS ---
        JPanel selectionDashboard = createSelectionDashboard();
        cardsRouterContainer.add(selectionDashboard, "HomeSelection");

        // 5. Updated all sub-panel creations to receive the same reportService instance
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

    private JPanel createSelectionDashboard() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 35, 40, 35));

        JLabel lblTitle = new JLabel("📊 Reports");
        lblTitle.setFont(new Font("Serif", Font.BOLD, 32));
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

        grid.add(createSelectionTile("👤", "Individual Member\nReport", "MemberReport", new Color(46, 117, 59)));
        grid.add(createSelectionTile("👥", "Equb Group\nReport", "EqubReport", new Color(184, 91, 23)));
        grid.add(createSelectionTile("❤️", "Edir Group\nReport", "EdirReport", new Color(214, 60, 43)));
        grid.add(createSelectionTile("📊", "Full System\nReport", "SystemReport", new Color(33, 91, 166)));

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

                g2.setFont(new Font("SansSerif", Font.PLAIN, 46));
                g2.setColor(accentTheme);
                FontMetrics fm = g2.getFontMetrics();
                int iconX = (getWidth() - fm.stringWidth(icon)) / 2;
                g2.drawString(icon, iconX, 75);

                super.paintComponent(g2);
                g2.dispose();
            }
        };

        tile.setFont(new Font("SansSerif", Font.BOLD, 15));
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