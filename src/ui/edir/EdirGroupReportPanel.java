package ui.edir;
import javax.swing.*;
import java.awt.*;
import service.EdirService;

public class EdirGroupReportPanel extends JPanel {
    public EdirGroupReportPanel(JPanel parent, EdirService service, String groupName) {
        setBackground(new Color(253, 247, 237));
        setLayout(new GridBagLayout());
        JButton btnBack = new JButton("← Back to Detail");
        btnBack.addActionListener(e -> ((CardLayout)parent.getLayout()).show(parent, "EdirDetail"));
        add(new JLabel("Audit Report Log Summary Generated for " + groupName), new GridBagConstraints());
        add(btnBack);
    }
}