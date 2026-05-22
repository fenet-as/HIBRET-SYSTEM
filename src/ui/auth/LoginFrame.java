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

        // 1. Declare and initialize all three panels first
        LoginPanel loginPanel = new LoginPanel(this);
        RegisterPanel registerPanel = new RegisterPanel(this);
        ForgotPasswordPanel forgotPanel = new ForgotPasswordPanel(this); // <-- MAKE SURE THIS LINE EXISTS

        // 2. Add them to the CardLayout container
        mainContainer.add(loginPanel, "login");
        mainContainer.add(registerPanel, "register");
        mainContainer.add(forgotPanel, "forgot"); // <-- Now Java knows what 'forgotPanel' is!

        add(mainContainer);
        setVisible(true);
    }

    public void showPage(String pageName) {
        cardLayout.show(mainContainer, pageName);
    }
}