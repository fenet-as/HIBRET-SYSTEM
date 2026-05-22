package ui.auth;

import model.User;
import service.AuthService;
import service.impl.AuthServiceImpl;
import session.Session;

import ui.core.MainFrame;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {

    private JTextField usernameField;
    private JPasswordField passwordField;

    private AuthService authService;
    private JFrame parentFrame;

    public LoginPanel(JFrame parentFrame) {

        this.parentFrame = parentFrame;
        this.authService = new AuthServiceImpl();

        setLayout(new BorderLayout());

        // TITLE
        JLabel title = new JLabel("HIBRET SYSTEM LOGIN", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        // FORM
        JPanel form = new JPanel(new GridLayout(3, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        form.add(new JLabel("Username:"));
        usernameField = new JTextField();
        form.add(usernameField);

        form.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        form.add(passwordField);

        JButton loginBtn = new JButton("Login");

        form.add(new JLabel());
        form.add(loginBtn);

        add(form, BorderLayout.CENTER);

        // ACTION
        loginBtn.addActionListener(e -> handleLogin());
    }

    private void handleLogin() {

        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        User user = authService.login(username, password);

        if (user != null) {

            Session.setCurrentUser(user);

            JOptionPane.showMessageDialog(this,
                    "Welcome " + user.getFullName());

            parentFrame.dispose();

            new MainFrame(); // next step (we will build it)

        } else {

            JOptionPane.showMessageDialog(this,
                    "Invalid username or password",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}