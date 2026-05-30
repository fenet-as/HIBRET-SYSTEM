package ui.equb;

import service.EqubService;
import model.Group;
import model.Member;
import javax.swing.*;
import java.awt.*;

public class EqubPayoutPanel extends JPanel {

    public EqubPayoutPanel(JPanel containerPanel, EqubService service, Group pool, Member recipient) {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel title = new JLabel("💰 Issue Group Payout Allocation");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
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

        JLabel lblRecipient = new JLabel("👤 Recipient Beneficiary: " + recipient.getFullName());
        lblRecipient.setFont(new Font("SansSerif", Font.BOLD, 14));
        detailsCard.add(lblRecipient);

        JLabel lblReqAmount = new JLabel(String.format("💵 Standard Target Award: %,.2f birr", requestedPrizeAmount));
        lblReqAmount.setFont(new Font("SansSerif", Font.PLAIN, 14));
        detailsCard.add(lblReqAmount);

        JLabel lblAvailable = new JLabel(String.format("🏦 Confirmed Vault Cash Present: %,.2f birr", availableVaultCash));
        lblAvailable.setFont(new Font("SansSerif", Font.BOLD, 14));

        // Color-code the availability text based on the balance health status
        if (availableVaultCash < requestedPrizeAmount) {
            lblAvailable.setForeground(new Color(195, 40, 30)); // Deep Warning Red
        } else {
            lblAvailable.setForeground(new Color(34, 112, 43)); // Verified Safe Green
        }
        detailsCard.add(lblAvailable);
        add(detailsCard);

        add(Box.createVerticalStrut(25));

        JButton btnConfirm = new JButton("Confirm and Save Payout Record");
        btnConfirm.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnConfirm.setPreferredSize(new Dimension(300, 45));

        // ✅ SECURITY STEP B: Deficit Prevention Lockout Check
        if (availableVaultCash < requestedPrizeAmount) {
            // Hard block to prevent generating an illegal negative database balance
            btnConfirm.setEnabled(false);

            JLabel lblAlertError = new JLabel("<html><body style='width: 350px;'>"
                    + "⛔ <b>TRANSACTION BLOCKED:</b> Insufficient capital collected. "
                    + "Please collect late contributions from group members before processing this disburse round."
                    + "</body></html>");
            lblAlertError.setFont(new Font("SansSerif", Font.BOLD, 13));
            lblAlertError.setForeground(new Color(195, 40, 30));
            add(lblAlertError);
            add(Box.createVerticalStrut(15));
        }

        btnConfirm.addActionListener(e -> {
            // Final fallback check to guarantee safety
            if (availableVaultCash < requestedPrizeAmount) {
                JOptionPane.showMessageDialog(this, "Operation rejected due to an insufficient cash balance.", "Security Rejection", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Route execution with timestamp parameters matching your operational timeline context
            service.recordPayout(pool.getId(), recipient.getId(), requestedPrizeAmount, "2026-05-30", "Rotational Payout Draw Awarded");

            JOptionPane.showMessageDialog(this, "Success! Disbursal transaction logged successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);

            containerPanel.remove(this);
            containerPanel.add(new EqubGroupDetailPanel(containerPanel, service, pool), "GroupDetail");
            ((CardLayout) containerPanel.getLayout()).show(containerPanel, "GroupDetail");
        });

        add(btnConfirm);
    }
}