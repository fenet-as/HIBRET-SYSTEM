package ui.auth;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainContainer;

    // Maintain references to panels
    private RegisterPanel registerPanel;
    private ForgotPasswordPanel forgotPanel;

    public LoginFrame() {
        // Configure standard English window title and basic defaults
        updateFrameMetadata();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 680);
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        // Initialize panels
        LoginPanel loginPanel = new LoginPanel(this);
        registerPanel = new RegisterPanel(this);
        forgotPanel = new ForgotPasswordPanel(this);

        // Add them to the CardLayout container
        mainContainer.add(loginPanel, "login");
        mainContainer.add(registerPanel, "register");
        mainContainer.add(forgotPanel, "forgot");

        add(mainContainer);
        setVisible(true);
    }

    /**
     * Set window title and configure default option pane typography settings
     */
    private void updateFrameMetadata() {
        setTitle("Hibret System");

        // Globally configures standard fonts for popups/dialog notifications
        UIManager.put("OptionPane.messageFont", new Font("SansSerif", Font.PLAIN, 14));
        UIManager.put("OptionPane.buttonFont", new Font("SansSerif", Font.PLAIN, 13));
    }

    /**
     * Swaps the visible view panel.
     */
    public void showPage(String pageName) {
        updateFrameMetadata();

        if ("login".equalsIgnoreCase(pageName)) {
            // Remove the stale login panel instance
            Component[] components = mainContainer.getComponents();
            for (Component comp : components) {
                if (comp instanceof LoginPanel) {
                    mainContainer.remove(comp);
                    break;
                }
            }
            // Add a fresh clean instance
            mainContainer.add(new LoginPanel(this), "login");
        }
        else if ("forgot".equalsIgnoreCase(pageName)) {
            // Remove stale forgot panel instance
            Component[] components = mainContainer.getComponents();
            for (Component comp : components) {
                if (comp instanceof ForgotPasswordPanel) {
                    mainContainer.remove(comp);
                    break;
                }
            }
            mainContainer.add(new ForgotPasswordPanel(this), "forgot");
        }
        else if ("register".equalsIgnoreCase(pageName)) {
            // Remove stale register panel instance
            Component[] components = mainContainer.getComponents();
            for (Component comp : components) {
                if (comp instanceof RegisterPanel) {
                    mainContainer.remove(comp);
                    break;
                }
            }
            mainContainer.add(new RegisterPanel(this), "register");
        }

        cardLayout.show(mainContainer, pageName);
    }
}