package ui.core;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ContentPanel extends JPanel {
    private final MainFrame parentFrame; // Added to handle navigation routes
    private Image jebenaImageAsset;

    private final Color TEXT_DARK_BROWN = new Color(101, 53, 15);
    private final Color TEXT_MUTED_GRAY = new Color(130, 125, 115);

    // Updated Constructor to accept MainFrame
    public ContentPanel(MainFrame frame) {
        this.parentFrame = frame;

        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(25, 35, 40, 35));

        try {
            jebenaImageAsset = ImageIO.read(new File("src/resources/images/Jebena.png"));
        } catch (IOException e) {
            System.err.println("Jebena illustration element not found.");
        }

        // --- View Header Title ---
        JLabel lblHeader = new JLabel("Dashboard");
        lblHeader.setFont(new Font("Serif", Font.BOLD, 32));
        lblHeader.setForeground(TEXT_DARK_BROWN);
        lblHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(lblHeader);
        add(Box.createVerticalStrut(20));

        // --- Metric Cards Section (Responsive Grid) ---
        JPanel metricsContainer = new JPanel(new GridLayout(0, 3, 15, 15)) {
            @Override
            public Dimension getMaximumSize() {
                return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
            }
        };
        metricsContainer.setOpaque(false);
        metricsContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

        metricsContainer.add(createInfoCard("👤", "Total Members", "120", new Color(40, 40, 40)));
        metricsContainer.add(createInfoCard("👥", "Equb Groups", "3", new Color(160, 40, 20)));
        metricsContainer.add(createInfoCard("🤝", "Edir Groups", "2", new Color(160, 40, 20)));
        metricsContainer.add(createInfoCard("💰", "Total Funds", "350,000 birr", new Color(34, 112, 43)));
        metricsContainer.add(createInfoCard("📋", "Pending Payments", "18", new Color(160, 40, 20)));

        JPanel emptyGridSpacer = new JPanel();
        emptyGridSpacer.setOpaque(false);
        metricsContainer.add(emptyGridSpacer);

        add(metricsContainer);
        add(Box.createVerticalStrut(30));

        // --- Bottom Module Management Section (Responsive Row Grid) ---
        JPanel modulesGrid = new JPanel(new GridLayout(1, 4, 18, 0)) {
            @Override
            public Dimension getMaximumSize() {
                return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
            }
        };
        modulesGrid.setOpaque(false);
        modulesGrid.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Added targets matching your routing system strings ("Equb", "Edir", "Reports", "Settings")
        modulesGrid.add(createModuleTile("🏘️", "Equb\nManagement", "Equb", new Color(46, 117, 59)));
        modulesGrid.add(createModuleTile("❤️", "Edir\nManagement", "Edir", new Color(184, 91, 23)));
        modulesGrid.add(createModuleTile("📊", "Reports", "Reports", new Color(207, 142, 19)));
        modulesGrid.add(createModuleTile("⚙️", "Settings", "Settings", new Color(33, 91, 166)));

        add(modulesGrid);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (jebenaImageAsset != null) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            int imgWidth = 240;
            int imgHeight = 240;
            int xPosition = getWidth() - imgWidth - 10;
            int yPosition = getHeight() - imgHeight - 10;

            g2.drawImage(jebenaImageAsset, xPosition, yPosition, imgWidth, imgHeight, this);
            g2.dispose();
        }
    }

    private JPanel createInfoCard(String iconSymbol, String title, String value, Color valueColor) {
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
        card.setLayout(new BorderLayout(12, 0));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 12));
        card.setPreferredSize(new Dimension(200, 95));
        card.setMinimumSize(new Dimension(120, 85));

        JLabel lblIcon = new JLabel(iconSymbol);
        lblIcon.setFont(new Font("SansSerif", Font.PLAIN, 28));
        lblIcon.setForeground(valueColor);
        card.add(lblIcon, BorderLayout.WEST);

        JPanel textStack = new JPanel();
        textStack.setOpaque(false);
        textStack.setLayout(new BoxLayout(textStack, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblTitle.setForeground(TEXT_MUTED_GRAY);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblValue.setForeground(valueColor);
        lblValue.setAlignmentX(Component.LEFT_ALIGNMENT);

        textStack.add(lblTitle);
        textStack.add(Box.createVerticalStrut(4));
        textStack.add(lblValue);

        card.add(textStack, BorderLayout.CENTER);
        return card;
    }

    // Accepts a target card route identifier parameter string now
    private JButton createModuleTile(String iconSymbol, String title, String targetRoute, Color bgTheme) {
        String formattedTitle = "<html><center>" + title.replaceAll("\n", "<br>") + "</center></html>";

        JButton tile = new JButton(formattedTitle) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(bgTheme);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);

                g2.setFont(new Font("SansSerif", Font.PLAIN, 42));
                FontMetrics fm = g2.getFontMetrics();
                int iconX = (getWidth() - fm.stringWidth(iconSymbol)) / 2;
                int iconY = (getHeight() / 2) - 10;
                g2.drawString(iconSymbol, iconX, iconY);

                super.paintComponent(g2);
                g2.dispose();
            }
        };

        tile.setFont(new Font("SansSerif", Font.BOLD, 15));
        tile.setForeground(Color.WHITE);
        tile.setContentAreaFilled(false);
        tile.setBorderPainted(false);
        tile.setFocusPainted(false);

        tile.setPreferredSize(new Dimension(165, 160));
        tile.setMinimumSize(new Dimension(110, 120));
        tile.setCursor(new Cursor(Cursor.HAND_CURSOR));

        tile.setVerticalAlignment(SwingConstants.BOTTOM);
        tile.setHorizontalAlignment(SwingConstants.CENTER);
        tile.setBorder(BorderFactory.createEmptyBorder(0, 5, 20, 5));

        // Redirects application view layout container via MainFrame reference
        tile.addActionListener(e -> parentFrame.switchDashboardView(targetRoute));

        return tile;
    }
}