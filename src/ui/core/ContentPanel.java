package ui.core;

import javax.swing.*;
import java.awt.*;
import model.DashboardMetrics;
import service.DashboardService;
import service.impl.DashboardServiceImpl;
import util.LanguageManager;
import util.FontManager; // ✅ Imported FontManager

public class ContentPanel extends JPanel {
    private final MainFrame parentFrame;
    private final DashboardService dashboardService;
    private final int loggedInUserId;

    private final Color TEXT_DARK_BROWN = new Color(101, 53, 15);
    private final Color TEXT_MUTED_GRAY = new Color(130, 125, 115);

    private JLabel lblTotalMembersValue;
    private JLabel lblEqubCirclesValue;
    private JLabel lblEdirGroupsValue;
    private JLabel lblTotalFundsValue;

    public ContentPanel(MainFrame frame, int loggedInUserId) {
        this.parentFrame = frame;
        this.loggedInUserId = loggedInUserId;
        this.dashboardService = new DashboardServiceImpl();

        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(25, 35, 40, 35));

        // ✅ Fixed Header Font with dynamic Amharic Font mapping
        JLabel lblHeader = new JLabel(LanguageManager.getString("dashboard.header"));
        lblHeader.setFont(FontManager.getBoldFont(32));
        lblHeader.setForeground(TEXT_DARK_BROWN);
        lblHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(lblHeader);
        add(Box.createVerticalStrut(20));

        JPanel metricsContainer = new JPanel(new GridLayout(0, 2, 20, 20)) {
            @Override
            public Dimension getMaximumSize() {
                return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
            }
        };
        metricsContainer.setOpaque(false);
        metricsContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ✅ Bound Information Metrics Cards Text properties
        lblTotalMembersValue = new JLabel("0");
        metricsContainer.add(createInfoCard("👤", LanguageManager.getString("dashboard.card.total_members"), lblTotalMembersValue, new Color(40, 40, 40)));

        lblTotalFundsValue = new JLabel("0.00 " + LanguageManager.getString("dashboard.currency_suffix"));
        metricsContainer.add(createInfoCard("💰", LanguageManager.getString("dashboard.card.vault_capital"), lblTotalFundsValue, new Color(34, 112, 43)));

        lblEqubCirclesValue = new JLabel("0");
        metricsContainer.add(createInfoCard("👥", LanguageManager.getString("dashboard.card.equb_groups"), lblEqubCirclesValue, new Color(160, 40, 20)));

        lblEdirGroupsValue = new JLabel("0");
        metricsContainer.add(createInfoCard("🤝", LanguageManager.getString("dashboard.card.edir_groups"), lblEdirGroupsValue, new Color(184, 91, 23)));

        add(metricsContainer);
        add(Box.createVerticalStrut(40));

        // --- Bottom Module Management Section ---
        JPanel modulesGrid = new JPanel(new GridLayout(1, 4, 18, 0)) {
            @Override
            public Dimension getMaximumSize() {
                return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
            }
        };
        modulesGrid.setOpaque(false);
        modulesGrid.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ✅ Bound Module Routing Menu Tiles Texts properties
        modulesGrid.add(createModuleTile("🏘️", LanguageManager.getString("dashboard.tile.equb"), "Equb", new Color(46, 117, 59)));
        modulesGrid.add(createModuleTile("❤️", LanguageManager.getString("dashboard.tile.edir"), "Edir", new Color(184, 91, 23)));
        modulesGrid.add(createModuleTile("📊", LanguageManager.getString("dashboard.tile.reports"), "Reports", new Color(207, 142, 19)));
        modulesGrid.add(createModuleTile("⚙️", LanguageManager.getString("dashboard.tile.settings"), "Settings", new Color(33, 91, 166)));

        add(modulesGrid);

        refreshData();
    }

    public void refreshData() {
        DashboardMetrics metrics = dashboardService.getSystemSummary(this.loggedInUserId);

        lblTotalMembersValue.setText(String.valueOf(metrics.getTotalEdirMembers()));
        lblEqubCirclesValue.setText(String.valueOf(metrics.getTotalEqubCircles()));
        lblEdirGroupsValue.setText(String.valueOf(metrics.getTotalEdirGroups()));

        // ✅ Dynamic Internationalized Currency Formatter injection
        double totalCombinedFunds = metrics.getTotalEqubVaultBalance() + metrics.getTotalEdirVaultBalance();
        lblTotalFundsValue.setText(String.format("%,.2f", totalCombinedFunds) + LanguageManager.getString("dashboard.currency_suffix"));

        revalidate();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    private JPanel createInfoCard(String iconSymbol, String title, JLabel lblValue, Color valueColor) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 12));
                g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 14, 14);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 3, 14, 14);
                g2.setColor(new Color(235, 230, 215));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 3, 14, 14);
                g2.dispose();
            }
        };
        card.setLayout(new BorderLayout(15, 0));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        JLabel lblIcon = new JLabel(iconSymbol);
        lblIcon.setFont(new Font("SansSerif", Font.PLAIN, 32)); // Standard Emojis can retain SansSerif mapping
        lblIcon.setForeground(valueColor);
        card.add(lblIcon, BorderLayout.WEST);

        JPanel textStack = new JPanel();
        textStack.setOpaque(false);
        textStack.setLayout(new BoxLayout(textStack, BoxLayout.Y_AXIS));

        // ✅ Replaced hardcoded title fonts with FontManager configurations
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(FontManager.getBoldFont(13));
        lblTitle.setForeground(TEXT_MUTED_GRAY);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ✅ Replaced numerical values font configurations
        lblValue.setFont(FontManager.getBoldFont(22));
        lblValue.setForeground(valueColor);
        lblValue.setAlignmentX(Component.LEFT_ALIGNMENT);

        textStack.add(lblTitle);
        textStack.add(Box.createVerticalStrut(4));
        textStack.add(lblValue);

        card.add(textStack, BorderLayout.CENTER);
        return card;
    }

    private JButton createModuleTile(String iconSymbol, String title, String targetRoute, Color bgTheme) {
        // ✅ Forcing explicit dynamic font-family properties inside the raw HTML formatting block
        String currentFontFamily = FontManager.getAmharicFontName();
        String cleanTitle = title.replaceAll("\n", "<br>");
        String formattedTitle = "<html><body style='font-family: " + currentFontFamily + "; text-align: center;'><center>" + cleanTitle + "</center></body></html>";

        JButton tile = new JButton(formattedTitle) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgTheme);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 42)); // Render graphic symbols safely
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(iconSymbol, (getWidth() - fm.stringWidth(iconSymbol)) / 2, (getHeight() / 2) - 10);
                super.paintComponent(g2);
                g2.dispose();
            }
        };

        // ✅ Applied dynamic Font configurations to JButton component defaults
        tile.setFont(FontManager.getBoldFont(14));
        tile.setForeground(Color.WHITE);
        tile.setContentAreaFilled(false);
        tile.setBorderPainted(false);
        tile.setFocusPainted(false);
        tile.setPreferredSize(new Dimension(165, 160));
        tile.setCursor(new Cursor(Cursor.HAND_CURSOR));
        tile.setVerticalAlignment(SwingConstants.BOTTOM);
        tile.setHorizontalAlignment(SwingConstants.CENTER);
        tile.setBorder(BorderFactory.createEmptyBorder(0, 5, 20, 5));
        tile.addActionListener(e -> parentFrame.switchDashboardView(targetRoute));
        return tile;
    }
}