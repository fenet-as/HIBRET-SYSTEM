package ui.auth;

import javax.swing.*;

public class LoginFrame extends JFrame {

    public LoginFrame() {

        setTitle("Hibret System - Login");
        setSize(450, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setContentPane(new LoginPanel(this));

        setVisible(true);
    }
}