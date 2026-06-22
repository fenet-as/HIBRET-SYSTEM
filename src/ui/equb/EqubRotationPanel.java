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
    private int currentRoundNumber = 1;

    private final JPanel cardWinnerDisplay;
    private final JLabel lblWinnerName;
    private final JLabel lblPayoutAmount;
    private final JLabel lblRoundStatus;
    private final JButton btnDrawWinner;
    private final JButton btnConfirmPayout;
    private final JLabel lblNoEligibleStatus;
    private final JPanel avatarBox;

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

        JLabel lblTitle = new JLabel("Pick a Winner");
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
        cardWinnerDisplay.setPreferredSize(new Dimension(480, 420));
        cardWinnerDisplay.setLayout(new BoxLayout(cardWinnerDisplay, BoxLayout.Y_AXIS));
        cardWinnerDisplay.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        lblRoundStatus = new JLabel("Checking group data...");
        lblRoundStatus.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblRoundStatus.setForeground(new Color(120, 90, 40));
        lblRoundStatus.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardWinnerDisplay.add(lblRoundStatus);
        cardWinnerDisplay.add(Box.createVerticalStrut(15));

        JPanel profileRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        profileRow.setOpaque(false);
        profileRow.setAlignmentX(Component.CENTER_ALIGNMENT);

        avatarBox = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(242, 235, 220));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(101, 53, 15));
                g2.setFont(new Font("SansSerif", Font.BOLD, 24));
                FontMetrics fm = g2.getFontMetrics();

                String nameStr = lblWinnerName.getText();
                String initial = (nameStr != null && !nameStr.isEmpty() && !nameStr.startsWith("Click") && !nameStr.startsWith("Waiting") && !nameStr.equals("N/A"))
                        ? nameStr.substring(0, 1).toUpperCase()
                        : "?";

                int x = (getWidth() - fm.stringWidth(initial)) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(initial, x, y);
                g2.dispose();
            }
        };

        avatarBox.setPreferredSize(new Dimension(70, 70));
        profileRow.add(avatarBox);

        lblWinnerName = new JLabel("Click the button below to draw a winner");
        lblWinnerName.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblWinnerName.setForeground(Color.DARK_GRAY);
        profileRow.add(lblWinnerName);

        cardWinnerDisplay.add(profileRow);
        cardWinnerDisplay.add(Box.createVerticalStrut(25));

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

        JLabel lblAmountTitle = new JLabel("TOTAL MONEY TO GIVE AWAY");
        lblAmountTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblAmountTitle.setForeground(new Color(120, 110, 100));
        lblAmountTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblPayoutAmount = new JLabel("0.00 ETB");
        lblPayoutAmount.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblPayoutAmount.setForeground(new Color(101, 53, 15));
        lblPayoutAmount.setAlignmentX(Component.CENTER_ALIGNMENT);

        amountAlertStrip.add(lblAmountTitle);
        amountAlertStrip.add(Box.createVerticalStrut(4));
        amountAlertStrip.add(lblPayoutAmount);
        cardWinnerDisplay.add(amountAlertStrip);
        cardWinnerDisplay.add(Box.createVerticalStrut(25));

        btnDrawWinner = new JButton("Draw Winner") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isEnabled() ? new Color(120, 90, 40) : Color.LIGHT_GRAY);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnDrawWinner.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnDrawWinner.setForeground(Color.WHITE);
        btnDrawWinner.setContentAreaFilled(false);
        btnDrawWinner.setBorderPainted(false);
        btnDrawWinner.setMaximumSize(new Dimension(400, 42));
        btnDrawWinner.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnDrawWinner.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDrawWinner.addActionListener(e -> executeLotterySelectionDraw());
        cardWinnerDisplay.add(btnDrawWinner);

        cardWinnerDisplay.add(Box.createVerticalStrut(10));

        btnConfirmPayout = new JButton("Confirm Winner & Give Money") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isEnabled() ? new Color(34, 112, 43) : Color.LIGHT_GRAY);
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
        btnConfirmPayout.setEnabled(false);
        btnConfirmPayout.addActionListener(e -> commitPayoutToDatabase());
        cardWinnerDisplay.add(btnConfirmPayout);

        lblNoEligibleStatus = new JLabel("All rounds have been successfully finished.");
        lblNoEligibleStatus.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblNoEligibleStatus.setForeground(new Color(195, 40, 30));
        lblNoEligibleStatus.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblNoEligibleStatus.setVisible(false);
        cardWinnerDisplay.add(lblNoEligibleStatus);

        centralWrapper.add(cardWinnerDisplay);
        add(centralWrapper, BorderLayout.CENTER);

        SwingUtilities.invokeLater(this::evaluateCycleStatusContext);
    }

    private void evaluateCycleStatusContext() {
        try {
            int completedRounds = service.getCompletedRoundsCount(groupCtx.getId());
            int totalMembers = groupCtx.getActiveMemberCount();

            if (totalMembers > 0) {
                this.currentRoundNumber = (completedRounds % totalMembers) + 1;
            } else {
                this.currentRoundNumber = 1;
            }

            int overallCycleIteration = (totalMembers > 0) ? (completedRounds / totalMembers) + 1 : 1;

            if (overallCycleIteration > 1) {
                lblRoundStatus.setText("Group Turn: " + overallCycleIteration + " | Current Round: " + currentRoundNumber + " of " + totalMembers);
            } else {
                lblRoundStatus.setText("Current Round: " + currentRoundNumber + " of " + totalMembers);
            }

            this.calculatedPayoutPool = service.getActualAvailableRoundPool(groupCtx.getId());
            lblPayoutAmount.setText(String.format("%,.2f ETB", this.calculatedPayoutPool));

            if (this.calculatedPayoutPool <= 0) {
                btnDrawWinner.setEnabled(false);
                lblWinnerName.setText("Waiting for member payments");
                lblWinnerName.setForeground(new Color(195, 40, 30));
                lblNoEligibleStatus.setText("Draw Locked: There is no money in the pool right now.");
                lblNoEligibleStatus.setVisible(true);
                return;
            } else {
                btnDrawWinner.setEnabled(true);
                lblWinnerName.setText("Click the button below to draw a winner");
                lblWinnerName.setForeground(Color.DARK_GRAY);
                lblNoEligibleStatus.setVisible(false);
            }

            boolean poolHasCandidates = service.hasEligibleUnpaidMembers(groupCtx.getId());
            if (!poolHasCandidates) {
                btnDrawWinner.setEnabled(false);
                lblWinnerName.setText("No names left to pick");
                lblNoEligibleStatus.setText("Everyone in this group has already won a round.");
                lblNoEligibleStatus.setVisible(true);
            }
        } catch (Exception ex) {
            lblRoundStatus.setText("Checking status for Round #" + currentRoundNumber);
        }
        avatarBox.repaint();
    }

    private void executeLotterySelectionDraw() {
        try {
            UIManager.put("OptionPane.messageFont", new Font("SansSerif", Font.PLAIN, 14));
            UIManager.put("OptionPane.buttonFont", new Font("SansSerif", Font.PLAIN, 13));

            boolean completelyPaid = service.haveAllMembersPaidCurrentRound(groupCtx.getId());
            if (!completelyPaid) {
                JOptionPane.showMessageDialog(this,
                        "Draw Blocked: Some members still have unpaid dues for this round.",
                        "Missing Payments",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            selectedWinner = service.triggerRandomRotationalDraw(groupCtx.getId());

            if (selectedWinner != null) {
                lblWinnerName.setText(selectedWinner.getFullName());
                lblWinnerName.setForeground(new Color(34, 112, 43));
                btnConfirmPayout.setEnabled(true);
                btnDrawWinner.setEnabled(false);
            } else {
                lblWinnerName.setText("No names found");
                btnConfirmPayout.setEnabled(false);
                lblNoEligibleStatus.setText("No members qualify to win right now.");
                lblNoEligibleStatus.setVisible(true);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error drawing name: " + ex.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        avatarBox.repaint();
    }

    private void commitPayoutToDatabase() {
        UIManager.put("OptionPane.messageFont", new Font("SansSerif", Font.PLAIN, 14));
        UIManager.put("OptionPane.buttonFont", new Font("SansSerif", Font.PLAIN, 13));

        if (selectedWinner == null || calculatedPayoutPool <= 0) {
            JOptionPane.showMessageDialog(this, "Information is missing or incorrect.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String descriptionText = "Round " + currentRoundNumber + " payment given to " + selectedWinner.getFullName();

        try {
            service.recordPayout(groupCtx.getId(), selectedWinner.getId(), calculatedPayoutPool, "COMPLETED", descriptionText);

            String formattedCash = String.format("%,.2f ETB", calculatedPayoutPool);
            String successMsg = "Round " + currentRoundNumber + " money of " + formattedCash + " successfully given to " + selectedWinner.getFullName() + ".";

            JOptionPane.showMessageDialog(this, successMsg, "Success", JOptionPane.INFORMATION_MESSAGE);

            navigateBackToDetails();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Could not save to database: " + ex.getLocalizedMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void navigateBackToDetails() {
        containerPanel.remove(this);

        List<Group> activeGroupsList = service.getAllEqubGroups();
        Group refreshedGroupCtx = groupCtx;
        if (activeGroupsList != null) {
            for (Group lookup : activeGroupsList) {
                if (lookup.getId() == groupCtx.getId()) {
                    refreshedGroupCtx = lookup;
                    break;
                }
            }
        }

        EqubGroupDetailPanel detailsScreenView = new EqubGroupDetailPanel(containerPanel, service, refreshedGroupCtx);
        detailsScreenView.refreshViewGridData();

        containerPanel.add(detailsScreenView, "GroupDetail");
        containerPanel.revalidate();
        containerPanel.repaint();
        ((CardLayout) containerPanel.getLayout()).show(containerPanel, "GroupDetail");
    }
}