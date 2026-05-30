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

        JLabel lbl = new JLabel("Financial Statement Audit: " + target.getName());
        lbl.setFont(new Font("SansSerif", Font.BOLD, 20));
        add(lbl, BorderLayout.NORTH);

        JTextArea reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setFont(new Font("SansSerif", Font.PLAIN, 14));

        StringBuilder sb = new StringBuilder();
        sb.append("=========================================\n");
        sb.append("      EQUB SYSTEM GROUP AUDIT REPORT     \n");
        sb.append("=========================================\n\n");
        sb.append("Group Reference ID: ").append(target.getId()).append("\n");
        sb.append("Cycle Contribution Rate: ").append(String.format("%,.2f", target.getContributionAmount())).append(" ETB\n");
        sb.append("Active Registered Members: ").append(target.getActiveMemberCount()).append("\n");
        sb.append("Total Projected Liquidity: ").append(String.format("%,.2f", target.getTotalCollectedCalculated())).append(" ETB\n");
        sb.append("\n=========================================");

        reportArea.setText(sb.toString());
        add(new JScrollPane(reportArea), BorderLayout.CENTER);
    }
}