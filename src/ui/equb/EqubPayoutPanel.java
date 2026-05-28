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
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        add(title); add(Box.createVerticalStrut(15));

        add(new JLabel("Recipient: " + recipient.getFullName()));
        add(new JLabel("Amount: " + (pool.getActiveMemberCount() * pool.getContributionAmount()) + " birr"));

        JButton btnConfirm = new JButton("Confirm and Save Payout Record");
        btnConfirm.addActionListener(e -> {
            double prize = pool.getActiveMemberCount() * pool.getContributionAmount();
            service.recordPayout(pool.getId(), recipient.getId(), prize, "2026-05-28", "Rotational Payout Draw Awardeded");
            containerPanel.remove(this);
            containerPanel.add(new EqubGroupDetailPanel(containerPanel, service, pool), "GroupDetail");
            ((CardLayout) containerPanel.getLayout()).show(containerPanel, "GroupDetail");
        });
        add(btnConfirm);
    }
}