package ui.edir;

import javax.swing.*;
import java.awt.*;
import service.EdirService;

public class EdirGroupReportPanel extends JPanel {

    public EdirGroupReportPanel(JPanel parent, EdirService service, String groupName) {
        setBackground(new Color(253, 247, 237));
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel lblSummary = new JLabel("Generated Financial Activity Report Summary for " + groupName);
        lblSummary.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblSummary.setForeground(new Color(101, 31, 16));
        add(lblSummary, gbc);

        gbc.gridy = 1;
        JButton btnBack = new JButton("Back to Group Details");
        btnBack.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btnBack.addActionListener(e -> ((CardLayout) parent.getLayout()).show(parent, "EdirDetail"));
        add(btnBack, gbc);
    }
}