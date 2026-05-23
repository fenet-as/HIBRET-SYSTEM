package ui.core;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ContentPanel extends JPanel {
    private Image jebenaImageAsset;

    public ContentPanel() {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(25, 30, 40, 30));

        // Load isolated coffee pot art asset
        try {
            jebenaImageAsset = ImageIO.read(new File("src/resources/images/Jebena.png"));
        } catch (IOException e) {
            System.err.println("Jebena illustration element not found.");
        }

        // --- View Header Title ---
        JLabel lblHeader = new JLabel("Dashboard");
        lblHeader.setFont(new Font("Serif", Font.BOLD, 28));
        lblHeader.setForeground(new Color(101, 53, 15));
        lblHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(lblHeader);
        add(Box.createVerticalStrut(20));

        // --- Metric Informative Cards Row ---
        JPanel metricsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        metricsPanel.setOpaque(false);
        metricsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        metricsPanel.add(createInfoCard("Total Members", "120", new Color(40, 40, 40)));
        metricsPanel.add(createInfoCard("Equb Groups", "3", new Color(160, 40, 20)));
        metricsPanel.add(createInfoCard("Edir Groups", "2", new Color(160, 40, 20)));
        metricsPanel.add(createInfoCard("Total Funds", "350,000 birr", new Color(34, 112, 43)));
        metricsPanel.add(createInfoCard("Pending Payments", "18", new Color(160, 40, 20)));

        add(metricsPanel);
        add(Box.createVerticalStrut(40));

        // --- Bottom Operation Management Modules ---
        JPanel modulesGrid = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        modulesGrid.setOpaque(false);
        modulesGrid.setAlignmentX(Component.LEFT_ALIGNMENT);

        modulesGrid.add(createModuleTile("Equb\nManagement", new Color(46, 117, 59)));
        modulesGrid.add(createModuleTile("Edir\nManagement", new Color(184, 91, 23)));
        modulesGrid.add(createModuleTile("Reports", new Color(207, 142, 19)));
        modulesGrid.add(createModuleTile("Settings", new Color(33, 91, 166)));

        add(modulesGrid);
    }

    // Paint component handles drawing the corner graphic elements safely
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (jebenaImageAsset != null) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            // Anchors illustration cleanly to the bottom-right corner without stretching
            int imgWidth = 240;
            int imgHeight = 240;
            int xPosition = getWidth() - imgWidth - 10;
            int yPosition = getHeight() - imgHeight - 10;

            g2.drawImage(jebenaImageAsset, xPosition, yPosition, imgWidth, imgHeight, this);
            g2.dispose();
        }
    }

    private JPanel createInfoCard(String title, String value, Color valueColor) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(new Color(230, 225, 210));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(162, 80));
        card.setMaximumSize(new Dimension(162, 80));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblTitle.setForeground(new Color(130, 125, 115));

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblValue.setForeground(valueColor);

        card.add(lblTitle);
        card.add(Box.createVerticalStrut(6));
        card.add(lblValue);
        return card;
    }

    private JButton createModuleTile(String title, Color bgTheme) {
        JButton tile = new JButton("<html><center>" + title.replaceAll("\n", "<br>") + "</center></html>") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgTheme);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                super.paintComponent(g2);
                g2.dispose();
            }
        };

        tile.setFont(new Font("SansSerif", Font.BOLD, 14));
        tile.setForeground(Color.WHITE);
        tile.setContentAreaFilled(false);
        tile.setBorderPainted(false);
        tile.setFocusPainted(false);
        tile.setPreferredSize(new Dimension(168, 155));
        tile.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return tile;
    }
}