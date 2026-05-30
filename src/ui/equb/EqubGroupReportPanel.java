package ui.equb;

import service.EqubService;
import model.Group;
import util.LanguageManager;
import util.FontManager; // ✅ Imported FontManager
import javax.swing.*;
import java.awt.*;

public class EqubGroupReportPanel extends JPanel {
    public EqubGroupReportPanel(JPanel container, EqubService service, Group target) {
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // ✅ Localized Panel Header Title using dynamic typography metrics
        JLabel lbl = new JLabel(LanguageManager.getFormattedString("equb.report.title", target.getName()));
        lbl.setFont(FontManager.getBoldFont(20));
        add(lbl, BorderLayout.NORTH);

        JTextArea reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setFont(FontManager.getPlainFont(14)); // ✅ Added safe font constraint for generated report metrics

        // ✅ Dynamically localized multi-line string build context layout
        StringBuilder sb = new StringBuilder();
        sb.append(LanguageManager.getString("equb.report.header")).append("\n");
        sb.append(LanguageManager.getFormattedString("equb.report.id", String.valueOf(target.getId()))).append("\n");
        sb.append(LanguageManager.getFormattedString("equb.report.rate", String.format("%,.2f", target.getContributionAmount()))).append("\n");
        sb.append(LanguageManager.getFormattedString("equb.report.members", String.valueOf(target.getActiveMemberCount()))).append("\n");
        sb.append(LanguageManager.getFormattedString("equb.report.liquidity", String.format("%,.2f", target.getTotalCollectedCalculated()))).append("\n");

        reportArea.setText(sb.toString());
        add(new JScrollPane(reportArea), BorderLayout.CENTER);
    }
}