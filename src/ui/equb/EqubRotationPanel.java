package ui.equb;

import service.EqubService;
import model.Group;
import model.Member;
import util.LanguageManager;
import util.FontManager; // ✅ Imported FontManager
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

        // ✅ Localized Header Title
        JLabel lblTitle = new JLabel(LanguageManager.getString("equb.rotation.title"));
        lblTitle.setFont(FontManager.getBoldFont(22)); // ✅ Integrated FontManager
        lblTitle.setForeground(new Color(101, 53, 15));
        headPanel.add(lblTitle, BorderLayout.WEST);

        // ✅ Localized Cancel Button Label
        JButton btnCancel = new JButton(LanguageManager.getString("equb.rotation.btn_cancel"));
        btnCancel.setFont(FontManager.getBoldFont(13)); // ✅ Integrated FontManager
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

        // ✅ Localized Evaluating Status Text
        lblRoundStatus = new JLabel(LanguageManager.getString("equb.rotation.evaluating"));
        lblRoundStatus.setFont(FontManager.getBoldFont(13)); // ✅ Integrated FontManager
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
                g2.setFont(FontManager.getBoldFont(24)); // ✅ Integrated FontManager
                FontMetrics fm = g2.getFontMetrics();

                String nameStr = lblWinnerName.getText();
                String initial = (nameStr != null && !nameStr.isEmpty() && !nameStr.startsWith("Click") && !nameStr.startsWith("Awaiting") && !nameStr.equals("N/A"))
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

        // ✅ Localized Initialization Placeholder Prompt
        lblWinnerName = new JLabel(LanguageManager.getString("equb.rotation.draw_start"));
        lblWinnerName.setFont(FontManager.getBoldFont(20)); // ✅ Integrated FontManager
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

        // ✅ Localized Stripped Container Subtitle Text
        JLabel lblAmountTitle = new JLabel(LanguageManager.getString("equb.rotation.pot_size_lbl"));
        lblAmountTitle.setFont(FontManager.getPlainFont(12)); // ✅ Integrated FontManager
        lblAmountTitle.setForeground(new Color(120, 110, 100));
        lblAmountTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblPayoutAmount = new JLabel("0.00 " + LanguageManager.getString("currency.unit"));
        lblPayoutAmount.setFont(FontManager.getBoldFont(22)); // ✅ Integrated FontManager
        lblPayoutAmount.setForeground(new Color(101, 53, 15));
        lblPayoutAmount.setAlignmentX(Component.CENTER_ALIGNMENT);

        amountAlertStrip.add(lblAmountTitle);
        amountAlertStrip.add(Box.createVerticalStrut(4));
        amountAlertStrip.add(lblPayoutAmount);
        cardWinnerDisplay.add(amountAlertStrip);
        cardWinnerDisplay.add(Box.createVerticalStrut(25));

        // ✅ Localized Button Trigger Label String Context
        btnDrawWinner = new JButton(LanguageManager.getString("equb.rotation.btn_draw")) {
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
        btnDrawWinner.setFont(FontManager.getBoldFont(14)); // ✅ Integrated FontManager
        btnDrawWinner.setForeground(Color.WHITE);
        btnDrawWinner.setContentAreaFilled(false);
        btnDrawWinner.setBorderPainted(false);
        btnDrawWinner.setMaximumSize(new Dimension(400, 42));
        btnDrawWinner.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnDrawWinner.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDrawWinner.addActionListener(e -> executeLotterySelectionDraw());
        cardWinnerDisplay.add(btnDrawWinner);

        cardWinnerDisplay.add(Box.createVerticalStrut(10));

        // ✅ Localized Button Trigger Label String Context
        btnConfirmPayout = new JButton(LanguageManager.getString("equb.rotation.btn_confirm")) {
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
        btnConfirmPayout.setFont(FontManager.getBoldFont(14)); // ✅ Integrated FontManager
        btnConfirmPayout.setForeground(Color.WHITE);
        btnConfirmPayout.setContentAreaFilled(false);
        btnConfirmPayout.setBorderPainted(false);
        btnConfirmPayout.setMaximumSize(new Dimension(400, 42));
        btnConfirmPayout.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnConfirmPayout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnConfirmPayout.setEnabled(false);
        btnConfirmPayout.addActionListener(e -> commitPayoutToDatabase());
        cardWinnerDisplay.add(btnConfirmPayout);

        // ✅ Localized Completion Message Content Label Text
        lblNoEligibleStatus = new JLabel(LanguageManager.getString("equb.rotation.complete_cycle"));
        lblNoEligibleStatus.setFont(FontManager.getBoldFont(13)); // ✅ Integrated FontManager (Using Bold safely to avoid unmapped italic types causing block errors)
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

            // Calculate current round bound within cyclical circle rotation bounds (1 to N)
            if (totalMembers > 0) {
                this.currentRoundNumber = (completedRounds % totalMembers) + 1;
            } else {
                this.currentRoundNumber = 1;
            }

            // Determine the current overall cycle iteration index
            int overallCycleIteration = (totalMembers > 0) ? (completedRounds / totalMembers) + 1 : 1;

            if (overallCycleIteration > 1) {
                // ✅ Localized Multi-Cycle Iteration Tracker Metrics
                lblRoundStatus.setText(LanguageManager.getFormattedString("equb.rotation.cycle_round_text",
                        String.valueOf(overallCycleIteration), String.valueOf(currentRoundNumber), String.valueOf(totalMembers)));
            } else {
                // ✅ Localized Active Ground Initial Round Context Meta text
                lblRoundStatus.setText(LanguageManager.getFormattedString("equb.rotation.active_round_text",
                        String.valueOf(currentRoundNumber), String.valueOf(totalMembers)));
            }

            this.calculatedPayoutPool = service.getActualAvailableRoundPool(groupCtx.getId());
            lblPayoutAmount.setText(String.format("%,.2f " + LanguageManager.getString("currency.unit"), this.calculatedPayoutPool));

            if (this.calculatedPayoutPool <= 0) {
                btnDrawWinner.setEnabled(false);
                // ✅ Localized Insufficient Contributions Warning Fallbacks
                lblWinnerName.setText(LanguageManager.getString("equb.rotation.awaiting_contributions"));
                lblWinnerName.setForeground(new Color(195, 40, 30));
                lblNoEligibleStatus.setText(LanguageManager.getString("equb.rotation.err_zero_balance"));
                lblNoEligibleStatus.setVisible(true);
                return;
            } else {
                btnDrawWinner.setEnabled(true);
                lblWinnerName.setText(LanguageManager.getString("equb.rotation.draw_start"));
                lblWinnerName.setForeground(Color.DARK_GRAY);
                lblNoEligibleStatus.setVisible(false);
            }

            boolean poolHasCandidates = service.hasEligibleUnpaidMembers(groupCtx.getId());
            if (!poolHasCandidates) {
                btnDrawWinner.setEnabled(false);
                // ✅ Localized Empty Directory Constraints Handling labels
                lblWinnerName.setText(LanguageManager.getString("equb.rotation.no_members"));
                lblNoEligibleStatus.setText(LanguageManager.getString("equb.rotation.err_add_members"));
                lblNoEligibleStatus.setVisible(true);
            }
        } catch (Exception ex) {
            // ✅ Localized Error Fallback Status Title Tracking Text
            lblRoundStatus.setText(LanguageManager.getFormattedString("equb.rotation.fallback_tracker", String.valueOf(currentRoundNumber)));
        }
        avatarBox.repaint();
    }

    private void executeLotterySelectionDraw() {
        try {
            // ✅ Inject global UI configurations for JOptionPane text alerts
            UIManager.put("OptionPane.messageFont", FontManager.getPlainFont(14));
            UIManager.put("OptionPane.buttonFont", FontManager.getPlainFont(13));

            boolean completelyPaid = service.haveAllMembersPaidCurrentRound(groupCtx.getId());
            if (!completelyPaid) {
                // ✅ Localized Outstanding Overdue Payment Block Warning Alerts Popups
                JOptionPane.showMessageDialog(this,
                        LanguageManager.getString("equb.rotation.incomplete_contrib_msg"),
                        LanguageManager.getString("equb.rotation.incomplete_contrib_title"),
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
                // ✅ Localized Missing Criteria Exception Layout labels
                lblWinnerName.setText(LanguageManager.getString("equb.rotation.no_candidates"));
                btnConfirmPayout.setEnabled(false);
                lblNoEligibleStatus.setText(LanguageManager.getString("equb.rotation.err_no_candidates"));
                lblNoEligibleStatus.setVisible(true);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, LanguageManager.getString("msg.parsing_error") + ": " + ex.getLocalizedMessage(), LanguageManager.getString("msg.error"), JOptionPane.ERROR_MESSAGE);
        }
        avatarBox.repaint();
    }

    private void commitPayoutToDatabase() {
        // ✅ Inject global UI configurations for JOptionPane text alerts
        UIManager.put("OptionPane.messageFont", FontManager.getPlainFont(14));
        UIManager.put("OptionPane.buttonFont", FontManager.getPlainFont(13));

        if (selectedWinner == null || calculatedPayoutPool <= 0) {
            // ✅ Localized Parameter Extraction Guard Notification Text
            JOptionPane.showMessageDialog(this, LanguageManager.getString("equb.rotation.err_empty_ctx"), LanguageManager.getString("msg.error"), JOptionPane.ERROR_MESSAGE);
            return;
        }

        // ✅ Localized Dynamic Entry Ledger Notes Template Context String
        String descriptionText = LanguageManager.getFormattedString("equb.rotation.disbursal_desc", String.valueOf(currentRoundNumber), selectedWinner.getFullName());

        try {
            service.recordPayout(groupCtx.getId(), selectedWinner.getId(), calculatedPayoutPool, "COMPLETED", descriptionText);

            // ✅ Localized Dynamic Alert Prompts context configuration text formats
            String formattedCash = String.format("%,.2f " + LanguageManager.getString("currency.unit"), calculatedPayoutPool);
            String successMsg = LanguageManager.getFormattedString("equb.rotation.success_msg", String.valueOf(currentRoundNumber), formattedCash, selectedWinner.getFullName());

            JOptionPane.showMessageDialog(this, successMsg, LanguageManager.getString("equb.rotation.success_title"), JOptionPane.INFORMATION_MESSAGE);

            navigateBackToDetails();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, LanguageManager.getString("msg.database_error") + ": " + ex.getLocalizedMessage(), LanguageManager.getString("msg.error"), JOptionPane.ERROR_MESSAGE);
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