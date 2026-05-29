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
    private final JPanel avatarBox;

    public EqubRotationPanel(JPanel containerPanel, EqubService service, Group groupCtx) {
        this.containerPanel = containerPanel;
        this.service = service;
        this.groupCtx = groupCtx;

        // Ensure background renders correctly matching your layout constraints
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

        // Header controls tracking buttons
        JPanel actionsHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionsHeader.setOpaque(false);

        // 🛠️ DEV TOOL ACTION: Allows testing layout states even if database records are empty
        JButton btnDevSimulate = new JButton("🔧 Sim Draw (Debug Mode)");
        btnDevSimulate.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnDevSimulate.setBackground(new Color(120, 90, 40));
        btnDevSimulate.setForeground(Color.WHITE);
        btnDevSimulate.addActionListener(e -> runMockSimulationDraw());
        actionsHeader.add(btnDevSimulate);

        JButton btnCancel = new JButton("Cancel");
        btnCancel.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnCancel.addActionListener(e -> navigateBackToDetails());
        actionsHeader.add(btnCancel);

        headPanel.add(actionsHeader, BorderLayout.EAST);
        add(headPanel, BorderLayout.NORTH);

        // --- CENTRAL DISPLAY SYSTEM ---
        JPanel centralWrapper = new JPanel(new GridBagLayout());
        centralWrapper.setOpaque(false);

        // Main structural rounded rectangle dashboard display asset
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
        cardWinnerDisplay.setPreferredSize(new Dimension(460, 340));
        cardWinnerDisplay.setLayout(new BoxLayout(cardWinnerDisplay, BoxLayout.Y_AXIS));
        cardWinnerDisplay.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        JLabel lblCaption = new JLabel("NEXT SELECTED BENEFICIARY");
        lblCaption.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblCaption.setForeground(new Color(140, 130, 115));
        lblCaption.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardWinnerDisplay.add(lblCaption);
        cardWinnerDisplay.add(Box.createVerticalStrut(20));

        // Profile row tracking avatars structures layouts
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
                String initial = (nameStr != null && !nameStr.isEmpty() && !nameStr.equals("Selecting...") && !nameStr.equals("N/A"))
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

        lblWinnerName = new JLabel("Selecting...");
        lblWinnerName.setFont(new Font("SansSerif", Font.BOLD, 26));
        lblWinnerName.setForeground(new Color(34, 112, 43));
        profileRow.add(lblWinnerName);

        cardWinnerDisplay.add(profileRow);
        cardWinnerDisplay.add(Box.createVerticalStrut(25));

        // Sub-panel box displaying total payouts allocations vector fields
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

        JLabel lblAmountTitle = new JLabel("Total Rotational Grand Prize Payout Pool");
        lblAmountTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblAmountTitle.setForeground(new Color(120, 110, 100));
        lblAmountTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblPayoutAmount = new JLabel("0.00 birr");
        lblPayoutAmount.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblPayoutAmount.setForeground(new Color(101, 53, 15));
        lblPayoutAmount.setAlignmentX(Component.CENTER_ALIGNMENT);

        amountAlertStrip.add(lblAmountTitle);
        amountAlertStrip.add(Box.createVerticalStrut(4));
        amountAlertStrip.add(lblPayoutAmount);
        cardWinnerDisplay.add(amountAlertStrip);
        cardWinnerDisplay.add(Box.createVerticalStrut(20));

        // Core submission interaction action buttons execution layouts
        btnConfirmPayout = new JButton("Confirm Payout") {
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
        btnConfirmPayout.addActionListener(e -> commitPayoutToDatabase());
        cardWinnerDisplay.add(btnConfirmPayout);

        // Fallback UI status messages component layout
        lblNoEligibleStatus = new JLabel("⚠️ No eligible cycle participants found to draw from.");
        lblNoEligibleStatus.setFont(new Font("SansSerif", Font.BOLD | Font.ITALIC, 13));
        lblNoEligibleStatus.setForeground(new Color(195, 40, 30));
        lblNoEligibleStatus.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblNoEligibleStatus.setVisible(false);
        cardWinnerDisplay.add(lblNoEligibleStatus);

        centralWrapper.add(cardWinnerDisplay);
        add(centralWrapper, BorderLayout.CENTER);

        // ⭐ UI FIX: Shift calculation loop execution out of constructor synchronization lifecycle.
        // This forces Swing to draw the elements completely before evaluating conditional rendering visibility rules.
        SwingUtilities.invokeLater(this::runWheelRotationProcess);
    }

    private void runWheelRotationProcess() {
        try {
            // Attempt query draw from Database mapping state indices records
            selectedWinner = service.triggerRandomRotationalDraw(groupCtx.getId());

            if (selectedWinner != null) {
                // Compute flat arithmetic pools metrics matching your layout formula rules
                double rate = groupCtx.getContributionAmount() > 0 ? groupCtx.getContributionAmount() : 1000.0;
                int count = groupCtx.getActiveMemberCount() > 0 ? groupCtx.getActiveMemberCount() : 5;
                calculatedPayoutPool = count * rate;

                lblWinnerName.setText(selectedWinner.getFullName());
                lblPayoutAmount.setText(String.format("%,.0f birr", calculatedPayoutPool));
                btnConfirmPayout.setEnabled(true);
                btnConfirmPayout.setVisible(true);
                lblNoEligibleStatus.setVisible(false);
            } else {
                showFallbackFailureState();
            }
        } catch (Exception ex) {
            showFallbackFailureState();
        }

        // Force complete graphics hierarchy recalculation
        refreshGraphicsCanvasContext();
    }

    private void runMockSimulationDraw() {
        // Fallback testing wrapper bypass logic in case matching database ledger accounts records don't exist yet
        selectedWinner = new Member();
        selectedWinner.setId(999);
        selectedWinner.setFullName("Chala Kebede (Simulated)");

        double rate = groupCtx.getContributionAmount() > 0 ? groupCtx.getContributionAmount() : 2500.0;
        int count = groupCtx.getActiveMemberCount() > 0 ? groupCtx.getActiveMemberCount() : 12;
        calculatedPayoutPool = count * rate;

        lblWinnerName.setText(selectedWinner.getFullName());
        lblPayoutAmount.setText(String.format("%,.0f birr", calculatedPayoutPool));
        btnConfirmPayout.setEnabled(true);
        btnConfirmPayout.setVisible(true);
        lblNoEligibleStatus.setVisible(false);

        refreshGraphicsCanvasContext();
    }

    private void showFallbackFailureState() {
        lblWinnerName.setText("N/A");
        lblPayoutAmount.setText("0 birr");
        btnConfirmPayout.setEnabled(false);
        btnConfirmPayout.setVisible(false);
        lblNoEligibleStatus.setVisible(true);
    }

    private void refreshGraphicsCanvasContext() {
        avatarBox.repaint();
        cardWinnerDisplay.revalidate();
        cardWinnerDisplay.repaint();
        this.revalidate();
        this.repaint();
    }

    private void commitPayoutToDatabase() {
        if (selectedWinner == null || calculatedPayoutPool <= 0) {
            JOptionPane.showMessageDialog(this, "Cannot record payout. Winner target context data is empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String descriptionText = "Cycle Round Prize Box Payout awarded to " + selectedWinner.getFullName();

        try {
            // Write transaction down through active service layer instances boundaries
            service.recordPayout(groupCtx.getId(), selectedWinner.getId(), calculatedPayoutPool, "COMPLETED", descriptionText);

            JOptionPane.showMessageDialog(this, "Success! Payout transaction logged successfully.\nDistributed: "
                    + lblPayoutAmount.getText() + " to " + selectedWinner.getFullName(), "Cycle Complete", JOptionPane.INFORMATION_MESSAGE);

            navigateBackToDetails();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database storage execution pipeline failed: " + ex.getMessage());
        }
    }

    private void navigateBackToDetails() {
        containerPanel.remove(this);

        // Re-query update properties states from live systems storage tables
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
        containerPanel.add(detailsScreenView, "GroupDetail");

        containerPanel.revalidate();
        containerPanel.repaint();

        ((CardLayout) containerPanel.getLayout()).show(containerPanel, "GroupDetail");
    }
}