package ui.auth;

import javax.swing.*;
import java.awt.*;
import util.LanguageManager;
import util.FontManager; // ✅ Imported FontManager

public class LoginFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainContainer;

    // Maintain references to panels that might need dynamic structural refreshes
    private RegisterPanel registerPanel;
    private ForgotPasswordPanel forgotPanel;

    public LoginFrame() {
        // ✅ i18n Bound Window Title and Dialog Box Typography configuration
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
     * ✅ Internal helper method to set window title dynamically and prepare JOptionPane font defaults
     */
    private void updateFrameMetadata() {
        setTitle(LanguageManager.getString("app.title"));

        // Globally configures any dialog box notifications thrown during auth steps to read Amharic correctly
        UIManager.put("OptionPane.messageFont", FontManager.getPlainFont(14));
        UIManager.put("OptionPane.buttonFont", FontManager.getPlainFont(13));
    }

    /**
     * Swaps the visible view.
     * ✅ UPDATED: Automatically updates frame titles and refreshes sibling views to pick up font properties.
     */
    public void showPage(String pageName) {
        // Enforce native typography properties and title layouts at runtime
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
            // Add a fresh instance using the new language locale configuration properties
            mainContainer.add(new LoginPanel(this), "login");
        }
        else if ("forgot".equalsIgnoreCase(pageName)) {
            // Remove stale forgot panel instance to catch runtime language translations instantly
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
            // Remove stale register panel instance to clear placeholder boxes instantly
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