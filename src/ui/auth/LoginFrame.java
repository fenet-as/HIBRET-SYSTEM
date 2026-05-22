package ui.auth;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    public LoginFrame() {
        setTitle("Hibret System - Login Screen");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setSize(950, 680); // Default startup size
        setMinimumSize(new Dimension(800, 580)); // Prevents clipping if window gets shrunk too far
        setResizable(true); // <-- Enabled resizable behavior

        setLocationRelativeTo(null);

        // Initialize and add our centering panel
        LoginPanel loginPanel = new LoginPanel();
        add(loginPanel);

        setVisible(true);
    }
}