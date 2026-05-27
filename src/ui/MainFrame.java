package ui;
import ui.equb.EqubPayoutPanel;
import ui.equb.EqubGroupReportPanel;
import ui.SidebarPanel;
import ui.equb.EqubGroupDetailPanel;
import ui.equb.CreateEqubGroupPanel;
import ui.equb.EqubHomePanel;
import ui.equb.EqubPaymentPanel;
import ui.equb.EqubRotationPanel;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private CardLayout layout;
    private JPanel mainPanel;

    public MainFrame() {

        setTitle("HIBRET SYSTEM");
        setSize(1100,700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        layout = new CardLayout();
        mainPanel = new JPanel(layout);

        mainPanel.add(new EqubHomePanel(this), "home");
        mainPanel.add(new CreateEqubGroupPanel(this), "create");
        mainPanel.add(new EqubPaymentPanel(this), "payment");
        mainPanel.add(new EqubRotationPanel(this), "rotation");
        mainPanel.add(new EqubGroupDetailPanel(this), "detail");
        mainPanel.add(new EqubPayoutPanel(this), "payout");

        setLayout(new BorderLayout());
        add(new SidebarPanel(this), BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);

        layout.show(mainPanel, "home");
    }

    public void showScreen(String name) {
        layout.show(mainPanel, name);
    }
}