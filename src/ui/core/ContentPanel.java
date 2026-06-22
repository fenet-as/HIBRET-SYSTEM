package ui.core;

import javax.swing.*;
import java.awt.*;
import model.DashboardMetrics;
import service.DashboardService;
import service.impl.DashboardServiceImpl;

public class ContentPanel extends JPanel {
    private final MainFrame parentFrame;
    private final DashboardService dashboardService;
    private final int loggedInUserId;

    // Premium Color Palette
    private final Color TEXT_DARK_BROWN = new Color(101, 53, 15);
    private final Color TEXT_MUTED_GRAY = new Color(130, 125, 115);

    private final JLabel lblTotalMembersValue;
    private final JLabel lblEqubCirclesValue;
    private final JLabel lblEdirGroupsValue;
    private final JLabel lblTotalFundsValue;

    public ContentPanel(MainFrame frame, int loggedInUserId) {
        this.parentFrame = frame;
        this.loggedInUserId = loggedInUserId;
        this.dashboardService = new DashboardServiceImpl();

        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(25, 35, 40, 35));

        // --- Header Section ---
        JLabel lblHeader = new JLabel("Dashboard Overview");
        lblHeader.setFont(new Font("SansSerif", Font.BOLD, 32));
        lblHeader.setForeground(TEXT_DARK_BROWN);
        lblHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(lblHeader);
        add(Box.createVerticalStrut(25));

        // --- Metrics Row Block (2x2 Grid Layout) ---
        JPanel metricsContainer = new JPanel(new GridLayout(2, 2, 20, 20)) {
            @Override
            public Dimension getMaximumSize() {
                return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
            }
        };
        metricsContainer.setOpaque(false);
        metricsContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblTotalMembersValue = new JLabel("0");
        metricsContainer.add(createInfoCard("👤", "Total Members", lblTotalMembersValue, new Color(40, 40, 40)));

        lblTotalFundsValue = new JLabel("0.00 ETB");
        metricsContainer.add(createInfoCard("💰", "Vault Capital Balance", lblTotalFundsValue, new Color(34, 112, 43)));

        lblEqubCirclesValue = new JLabel("0");
        metricsContainer.add(createInfoCard("👥", "Active Equb Groups", lblEqubCirclesValue, new Color(160, 40, 20)));

        lblEdirGroupsValue = new JLabel("0");
        metricsContainer.add(createInfoCard("🤝", "Active Edir Groups", lblEdirGroupsValue, new Color(184, 91, 23)));

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

        // Modules Quick Routing Menu Tiles
        modulesGrid.add(createModuleTile("🏘️", "Equb Management", "Equb", new Color(46, 117, 59)));
        modulesGrid.add(createModuleTile("❤️", "Edir Management", "Edir", new Color(184, 91, 23)));
        modulesGrid.add(createModuleTile("📊", "Financial Reports", "Reports", new Color(207, 142, 19)));
        modulesGrid.add(createModuleTile("⚙️", "System Settings", "Settings", new Color(33, 91, 166)));

        add(modulesGrid);

        refreshData();
    }

    public void refreshData() {
        DashboardMetrics metrics = dashboardService.getSystemSummary(this.loggedInUserId);

        lblTotalMembersValue.setText(String.valueOf(metrics.getTotalEdirMembers()));
        lblEqubCirclesValue.setText(String.valueOf(metrics.getTotalEqubCircles()));
        lblEdirGroupsValue.setText(String.valueOf(metrics.getTotalEdirGroups()));

        double totalCombinedFunds = metrics.getTotalEqubVaultBalance() + metrics.getTotalEdirVaultBalance();
        lblTotalFundsValue.setText(String.format("%,.2f", totalCombinedFunds) + " ETB");

        revalidate();
        repaint();
    }

    private JPanel createInfoCard(String iconSymbol, String title, JLabel lblValue, Color valueColor) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Card Shadow
                g2.setColor(new Color(0, 0, 0, 10));
                g2.fillRoundRect(2, 3, getWidth() - 4, getHeight() - 5, 16, 16);

                // Solid Card Background
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 3, 16, 16);

                // Card Border Stroke
                g2.setColor(new Color(230, 225, 210));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 3, 16, 16);
                g2.dispose();
            }
        };
        card.setLayout(new BorderLayout(18, 0));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(20, 22, 20, 22));

        // Fixed dimension container for alignment integrity
        JLabel lblIcon = new JLabel(iconSymbol, SwingConstants.CENTER);
        lblIcon.setFont(new Font("SansSerif", Font.PLAIN, 34));
        lblIcon.setForeground(valueColor);
        lblIcon.setPreferredSize(new Dimension(45, 45));
        card.add(lblIcon, BorderLayout.WEST);

        JPanel textStack = new JPanel();
        textStack.setOpaque(false);
        textStack.setLayout(new BoxLayout(textStack, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblTitle.setForeground(TEXT_MUTED_GRAY);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblValue.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblValue.setForeground(valueColor);
        lblValue.setAlignmentX(Component.LEFT_ALIGNMENT);

        textStack.add(lblTitle);
        textStack.add(Box.createVerticalStrut(4));
        textStack.add(lblValue);

        card.add(textStack, BorderLayout.CENTER);
        return card;
    }

    private JButton createModuleTile(String iconSymbol, String title, String targetRoute, Color bgTheme) {
        String cleanTitle = title.replaceAll("\n", "<br>");
        String formattedTitle = "<html><body style='text-align: center;'><center>" + cleanTitle + "</center></body></html>";

        JButton tile = new JButton(formattedTitle) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw Rounded Colored Background
                g2.setColor(bgTheme);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                // Draw Icon centered above the label
                g2.setFont(new Font("SansSerif", Font.PLAIN, 44));
                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                int iconX = (getWidth() - fm.stringWidth(iconSymbol)) / 2;
                int iconY = (getHeight() / 2) - 10;
                g2.drawString(iconSymbol, iconX, iconY);

                g2.dispose();

                // Renders the button text cleanly *over* the custom graphic elements
                super.paintComponent(g);
            }
        };

        tile.setFont(new Font("SansSerif", Font.BOLD, 14));
        tile.setForeground(Color.WHITE);
        tile.setContentAreaFilled(false);
        tile.setBorderPainted(false);
        tile.setFocusPainted(false);
        tile.setPreferredSize(new Dimension(165, 165));
        tile.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Push the HTML text layout boundary strictly towards the bottom edge
        tile.setVerticalAlignment(SwingConstants.BOTTOM);
        tile.setHorizontalAlignment(SwingConstants.CENTER);
        tile.setBorder(BorderFactory.createEmptyBorder(0, 8, 16, 8));

        tile.addActionListener(e -> parentFrame.switchDashboardView(targetRoute));
        return tile;
    }
}