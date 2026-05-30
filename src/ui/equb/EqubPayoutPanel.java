package ui.equb;

import service.EqubService;
import model.Group;
import model.Member;
import util.LanguageManager;
import util.FontManager; // ✅ Imported FontManager
import javax.swing.*;
import java.awt.*;

public class EqubPayoutPanel extends JPanel {

    public EqubPayoutPanel(JPanel containerPanel, EqubService service, Group pool, Member recipient) {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // ✅ Localized Header Title using dynamic typography metrics
        JLabel title = new JLabel(LanguageManager.getString("equb.payout.title"));
        title.setFont(FontManager.getBoldFont(22));
        title.setForeground(new Color(101, 53, 15));
        add(title);
        add(Box.createVerticalStrut(20));

        // ✅ SECURITY STEP A: Pull actual verified vault cash reserves from the transaction logs
        double availableVaultCash = service.getActualAvailableRoundPool(pool.getId());

        // This is what the recipient wants to draw out
        double requestedPrizeAmount = pool.getActiveMemberCount() * pool.getContributionAmount();

        JPanel detailsCard = new JPanel();
        detailsCard.setLayout(new GridLayout(4, 1, 0, 10));
        detailsCard.setOpaque(false);

        // ✅ Localized Card Entries with modular plain/bold font layer lookups
        JLabel lblRecipient = new JLabel(LanguageManager.getFormattedString("equb.payout.recipient", recipient.getFullName()));
        lblRecipient.setFont(FontManager.getBoldFont(14));
        detailsCard.add(lblRecipient);

        String formattedTarget = String.format("%,.2f", requestedPrizeAmount);
        JLabel lblReqAmount = new JLabel(LanguageManager.getFormattedString("equb.payout.target_award", formattedTarget));
        lblReqAmount.setFont(FontManager.getPlainFont(14));
        detailsCard.add(lblReqAmount);

        String formattedVault = String.format("%,.2f", availableVaultCash);
        JLabel lblAvailable = new JLabel(LanguageManager.getFormattedString("equb.payout.vault_cash", formattedVault));
        lblAvailable.setFont(FontManager.getBoldFont(14));

        // Color-code the availability text based on the balance health status
        if (availableVaultCash < requestedPrizeAmount) {
            lblAvailable.setForeground(new Color(195, 40, 30)); // Deep Warning Red
        } else {
            lblAvailable.setForeground(new Color(34, 112, 43)); // Verified Safe Green
        }
        detailsCard.add(lblAvailable);
        add(detailsCard);

        add(Box.createVerticalStrut(25));

        // ✅ Localized Core Action Button
        JButton btnConfirm = new JButton(LanguageManager.getString("equb.payout.btn_confirm"));
        btnConfirm.setFont(FontManager.getBoldFont(14));
        btnConfirm.setPreferredSize(new Dimension(300, 45));

        // ✅ SECURITY STEP B: Deficit Prevention Lockout Check
        if (availableVaultCash < requestedPrizeAmount) {
            // Hard block to prevent generating an illegal negative database balance
            btnConfirm.setEnabled(false);

            // ✅ Localized Error Block Warning Block Message Layout
            JLabel lblAlertError = new JLabel(LanguageManager.getString("equb.payout.blocked_msg"));
            lblAlertError.setFont(FontManager.getBoldFont(13));
            lblAlertError.setForeground(new Color(195, 40, 30));
            add(lblAlertError);
            add(Box.createVerticalStrut(15));
        }

        btnConfirm.addActionListener(e -> {
            // Reconfigure active message prompt schemas to securely render dynamic font assets
            UIManager.put("OptionPane.messageFont", FontManager.getPlainFont(14));
            UIManager.put("OptionPane.buttonFont", FontManager.getPlainFont(13));

            // Final fallback check to guarantee safety
            if (availableVaultCash < requestedPrizeAmount) {
                JOptionPane.showMessageDialog(this, LanguageManager.getString("equb.payout.err.insufficient"), LanguageManager.getString("equb.payout.err.title"), JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Route execution with timestamp parameters matching your operational timeline context
            service.recordPayout(pool.getId(), recipient.getId(), requestedPrizeAmount, "2026-05-30", "Rotational Payout Draw Awarded");

            JOptionPane.showMessageDialog(this, LanguageManager.getString("equb.payout.success"), LanguageManager.getString("msg.success"), JOptionPane.INFORMATION_MESSAGE);

            containerPanel.remove(this);
            containerPanel.add(new EqubGroupDetailPanel(containerPanel, service, pool), "GroupDetail");
            ((CardLayout) containerPanel.getLayout()).show(containerPanel, "GroupDetail");
        });

        add(btnConfirm);
    }
}