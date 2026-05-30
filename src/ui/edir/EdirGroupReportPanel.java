package ui.edir;

import javax.swing.*;
import java.awt.*;
import service.EdirService;
import util.LanguageManager;
import util.FontManager; // ✅ Imported FontManager

public class EdirGroupReportPanel extends JPanel {

    public EdirGroupReportPanel(JPanel parent, EdirService service, String groupName) {
        setBackground(new Color(253, 247, 237));
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;

        // ✅ Localized Generated Report Label with dynamic font fallback mappings
        JLabel lblSummary = new JLabel(LanguageManager.getFormattedString("edir.report.summary", groupName));
        lblSummary.setFont(FontManager.getBoldFont(14));
        lblSummary.setForeground(new Color(101, 31, 16));
        add(lblSummary, gbc);

        gbc.gridy = 1;
        // ✅ Localized Back Action Button Label with dynamic font fallback mappings
        JButton btnBack = new JButton(LanguageManager.getString("edir.report.btn_back"));
        btnBack.setFont(FontManager.getPlainFont(13));
        btnBack.addActionListener(e -> ((CardLayout) parent.getLayout()).show(parent, "EdirDetail"));
        add(btnBack, gbc);
    }
}