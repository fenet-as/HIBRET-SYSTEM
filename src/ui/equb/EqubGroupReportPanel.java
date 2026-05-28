package ui.equb;

import service.EqubService;
import model.Group;
import javax.swing.*;
import java.awt.*;

public class EqubGroupReportPanel extends JPanel {
    public EqubGroupReportPanel(JPanel container, EqubService service, Group target) {
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel lbl = new JLabel("📊 Performance Audit for Pool: " + target.getName());
        lbl.setFont(new Font("SansSerif", Font.BOLD, 20));
        add(lbl, BorderLayout.NORTH);

        JTextArea reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setText("====== LOG AUDIT METRICS ======\n" +
                "Pool Identifier: " + target.getId() + "\n" +
                "Base Level Matrix: " + target.getContributionAmount() + " birr\n" +
                "Dynamic Registration Index: " + target.getActiveMemberCount() + " active users\n" +
                "Liquidity Registered: " + target.getTotalCollectedCalculated() + " birr\n");
        add(new JScrollPane(reportArea), BorderLayout.CENTER);
    }
}