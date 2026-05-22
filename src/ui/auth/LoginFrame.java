
package ui.auth;



import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainContainer;

    public LoginFrame() {
        setTitle("Hibret System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 680);
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        // FIX: removed "this" because LoginPanel/RegisterPanel don't accept it
        LoginPanel loginPanel = new LoginPanel(this);
        RegisterPanel registerPanel = new RegisterPanel(this);

        mainContainer.add(loginPanel, "login");
        mainContainer.add(registerPanel, "register");

        add(mainContainer);
        setVisible(true);
    }

    public void showPage(String pageName) {
        cardLayout.show(mainContainer, pageName);
    }
}