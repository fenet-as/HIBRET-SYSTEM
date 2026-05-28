package ui.equb;

import service.EqubService;
import model.Group;
import model.Member;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class EqubRotationPanel extends JPanel {
    private final JPanel containerPanel;
    private final EqubService service;
    private final Group groupCtx;

    private Member selectedWinner = null;
    private double calculatedPayoutPool = 0.0;

    private final JPanel cardWinnerDisplay;
    private final JLabel lblWinnerName;
    private final JLabel lblPayoutAmount;
    private final JButton btnConfirmPayout;
    private final JLabel lblNoEligibleStatus;

    public EqubRotationPanel(JPanel containerPanel, EqubService service, Group groupCtx) {
        this.containerPanel = containerPanel;
        this.service = service;
        this.groupCtx = groupCtx;

        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(25, 35, 30, 35));

        // --- TOP ROW HEADER ---
        JPanel headPanel = new JPanel(new BorderLayout());
        headPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("🔄 Payout Rotation Wheel");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitle.setForeground(new Color(101, 53, 15));
        headPanel.add(lblTitle, BorderLayout.WEST);

        JButton btnCancel = new JButton("Cancel");
        btnCancel.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnCancel.addActionListener(e -> navigateBackToDetails());
        headPanel.add(btnCancel, BorderLayout.EAST);
        add(headPanel, BorderLayout.NORTH);

        // --- CENTRAL DISPLAY SYSTEM ---
        JPanel centralWrapper = new JPanel(new GridBagLayout());
        centralWrapper.setOpaque(false);

        // Main white layout block following your visual reference mockup
        cardWinnerDisplay = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(new Color(230, 225, 215));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
            }
        };
        cardWinnerDisplay.setOpaque(false);
        cardWinnerDisplay.setPreferredSize(new Dimension(460, 320));
        cardWinnerDisplay.setLayout(new BoxLayout(cardWinnerDisplay, BoxLayout.Y_AXIS));
        cardWinnerDisplay.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        JLabel lblCaption = new JLabel("Next Receiver");
        lblCaption.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblCaption.setForeground(new Color(120, 115, 105));
        lblCaption.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardWinnerDisplay.add(lblCaption);
        cardWinnerDisplay.add(Box.createVerticalStrut(20));

        // Layout horizontal container for avatar image and name field
        JPanel profileRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        profileRow.setOpaque(false);
        profileRow.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Decorative structural avatar placeholder component
        JPanel avatarBox = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(240, 230, 210));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(139, 69, 19));
                g2.setFont(new Font("SansSerif", Font.BOLD, 22));
                FontMetrics fm = g2.getFontMetrics();
                String initial = (selectedWinner != null) ? selectedWinner.getFullName().substring(0,1).toUpperCase() : "?";
                int x = (getWidth() - fm.stringWidth(initial)) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(initial, x, y);
                g2.dispose();
            }
        };
        avatarBox.setPreferredSize(new Dimension(65, 65));
        profileRow.add(avatarBox);

        lblWinnerName = new JLabel("Selecting...");
        lblWinnerName.setFont(new Font("SansSerif", Font.BOLD, 26));
        lblWinnerName.setForeground(new Color(34, 112, 43));
        profileRow.add(lblWinnerName);

        cardWinnerDisplay.add(profileRow);
        cardWinnerDisplay.add(Box.createVerticalStrut(25));

        // Sub-panel box to clearly highlight the total prize payout amount
        JPanel amountAlertStrip = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(254, 248, 235));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(new Color(245, 220, 180));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
            }
        };
        amountAlertStrip.setOpaque(false);
        amountAlertStrip.setMaximumSize(new Dimension(400, 75));
        amountAlertStrip.setLayout(new BoxLayout(amountAlertStrip, BoxLayout.Y_AXIS));
        amountAlertStrip.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));

        JLabel lblAmountTitle = new JLabel("Amount to be Paid");
        lblAmountTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblAmountTitle.setForeground(new Color(110, 100, 90));
        lblAmountTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblPayoutAmount = new JLabel("0.00 birr");
        lblPayoutAmount.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblPayoutAmount.setForeground(new Color(101, 53, 15));
        lblPayoutAmount.setAlignmentX(Component.CENTER_ALIGNMENT);

        amountAlertStrip.add(lblAmountTitle);
        amountAlertStrip.add(Box.createVerticalStrut(4));
        amountAlertStrip.add(lblPayoutAmount);
        cardWinnerDisplay.add(amountAlertStrip);
        cardWinnerDisplay.add(Box.createVerticalStrut(25));

        // Action Confirmation Button
        btnConfirmPayout = new JButton("Confirm Payout") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(btnConfirmPayout.isEnabled() ? new Color(34, 112, 43) : Color.LIGHT_GRAY);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnConfirmPayout.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnConfirmPayout.setForeground(Color.WHITE);
        btnConfirmPayout.setContentAreaFilled(false);
        btnConfirmPayout.setBorderPainted(false);
        btnConfirmPayout.setMaximumSize(new Dimension(400, 42));
        btnConfirmPayout.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnConfirmPayout.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnConfirmPayout.addActionListener(e -> commitPayoutToDatabase());
        cardWinnerDisplay.add(btnConfirmPayout);

        // Fallback placeholder notice in case no members qualify for selection
        lblNoEligibleStatus = new JLabel("No eligible cycle participants found to draw from.");
        lblNoEligibleStatus.setFont(new Font("SansSerif", Font.ITALIC, 13));
        lblNoEligibleStatus.setForeground(Color.RED);
        lblNoEligibleStatus.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblNoEligibleStatus.setVisible(false);
        cardWinnerDisplay.add(lblNoEligibleStatus);

        centralWrapper.add(cardWinnerDisplay);
        add(centralWrapper, BorderLayout.CENTER);

        // Run selection roll logic right away when opening the screen context panel
        runWheelRotationProcess();
    }

    private void runWheelRotationProcess() {
        // Query a randomized eligible profile using our DAO layer logic
        selectedWinner = service.triggerRandomRotationalDraw(groupCtx.getId());

        if (selectedWinner != null) {
            // Prize collection pool calculation rule: (Active Group Count) x (Group Contribution Flat Fee)
            calculatedPayoutPool = groupCtx.getActiveMemberCount() * groupCtx.getContributionAmount();

            lblWinnerName.setText(selectedWinner.getFullName());
            lblPayoutAmount.setText(String.format("%,.0f birr", calculatedPayoutPool));
            btnConfirmPayout.setEnabled(true);
            lblNoEligibleStatus.setVisible(false);
        } else {
            lblWinnerName.setText("N/A");
            lblPayoutAmount.setText("0 birr");
            btnConfirmPayout.setEnabled(false);
            btnConfirmPayout.setVisible(false);
            lblNoEligibleStatus.setVisible(true);
        }
    }

    private void commitPayoutToDatabase() {
        if (selectedWinner == null || calculatedPayoutPool <= 0) return;

        String descriptionText = "Cycle Round Prize Box Payout awarded to " + selectedWinner.getFullName();

        // Write transaction entity straight down through service layers into PostgreSQL
        service.recordPayout(groupCtx.getId(), selectedWinner.getId(), calculatedPayoutPool, "", descriptionText);

        JOptionPane.showMessageDialog(this, "Success! Payout transaction logged successfully.\nDistributed: "
                + lblPayoutAmount.getText() + " to " + selectedWinner.getFullName(), "Cycle Complete", JOptionPane.INFORMATION_MESSAGE);

        navigateBackToDetails();
    }

    private void navigateBackToDetails() {
        containerPanel.remove(this);

        // Re-query updated values from database to ensure dashboard updates seamlessly
        List<Group> activeGroupsList = service.getAllEqubGroups();
        Group refreshedGroupCtx = groupCtx;
        for (Group lookup : activeGroupsList) {
            if (lookup.getId() == groupCtx.getId()) {
                refreshedGroupCtx = lookup;
                break;
            }
        }

        EqubGroupDetailPanel detailsScreenView = new EqubGroupDetailPanel(containerPanel, service, refreshedGroupCtx);
        containerPanel.add(detailsScreenView, "GroupDetail");
        ((CardLayout) containerPanel.getLayout()).show(containerPanel, "GroupDetail");
    }
}