
package ui.auth;


import ui.auth.components.RoundedButton;
import ui.auth.components.RoundedPasswordField;
import ui.auth.components.RoundedTextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class LoginPanel extends JPanel {
    private final LoginFrame parentFrame;
    private Image backgroundImage;

    public LoginPanel(LoginFrame frame) {
        this.parentFrame = frame;
        // Load the background image
        try {
            backgroundImage = ImageIO.read(new File("src/resources/images/LoginBg.png"));
        } catch (IOException e) {
            System.err.println("Could not load background image. Using fallback color.");
        }

        setLayout(new GridBagLayout());

        JPanel centerCard = new JPanel(new GridLayout(1, 2, 40, 0));
        centerCard.setOpaque(false);
        centerCard.setPreferredSize(new Dimension(750, 500));

        JPanel leftSpacer = new JPanel();
        leftSpacer.setOpaque(false);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("HIBRET");
        lblTitle.setFont(new Font("Serif", Font.BOLD, 46));
        lblTitle.setForeground(new Color(101, 53, 15));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSubTitle = new JLabel("SYSTEM");
        lblSubTitle.setFont(new Font("SansSerif", Font.BOLD, 38));
        lblSubTitle.setForeground(new Color(34, 112, 43));
        lblSubTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTagline = new JLabel("Equb & Edir Community Management");
        lblTagline.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblTagline.setForeground(new Color(80, 80, 80));
        lblTagline.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblUser.setForeground(new Color(70, 70, 70));
        lblUser.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField txtUsername = new RoundedTextField("  Enter username", 20);
        txtUsername.setMaximumSize(new Dimension(320, 42));
        txtUsername.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblPass.setForeground(new Color(70, 70, 70));
        lblPass.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPasswordField txtPassword = new RoundedPasswordField(20);
        txtPassword.setMaximumSize(new Dimension(320, 42));
        txtPassword.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel forgotPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        forgotPanel.setOpaque(false);
        forgotPanel.setMaximumSize(new Dimension(320, 20));
        forgotPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblForgot = new JLabel("Forgot Password?");
        lblForgot.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblForgot.setForeground(new Color(101, 53, 15));
        lblForgot.setCursor(new Cursor(Cursor.HAND_CURSOR));

        lblForgot.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(null, "Redirecting to password recovery...");
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                lblForgot.setText("<html><u>Forgot Password?</u></html>");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                lblForgot.setText("Forgot Password?");
            }
        });

        forgotPanel.add(lblForgot);

        JButton btnLogin = new RoundedButton("LOGIN", new Color(34, 112, 43));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnLogin.setMaximumSize(new Dimension(320, 45));
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel registerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        registerPanel.setOpaque(false);
        registerPanel.setMaximumSize(new Dimension(320, 25));
        registerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblNoAccount = new JLabel("Don't have an account?");
        lblNoAccount.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblNoAccount.setForeground(new Color(100, 100, 100));

        JLabel lblRegister = new JLabel("Sign Up");
        lblRegister.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblRegister.setForeground(new Color(34, 112, 43));
        lblRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // FIXED: only ONE listener (no parentFrame, no duplicates)
        lblRegister.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                parentFrame.showPage("register");
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                lblRegister.setText("<html><u>Sign Up</u></html>");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                lblRegister.setText("Sign Up");
            }
        });

        registerPanel.add(lblNoAccount);
        registerPanel.add(lblRegister);

        JPanel langPanel = new JPanel(new GridLayout(1, 2));
        langPanel.setOpaque(false);
        langPanel.setMaximumSize(new Dimension(160, 35));
        langPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnEng = new JButton("English");
        btnEng.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnEng.setFocusPainted(false);

        JButton btnAmh = new JButton("አማርኛ");
        btnAmh.setFont(new Font("Nyala", Font.PLAIN, 14));
        btnAmh.setFocusPainted(false);

        langPanel.add(btnEng);
        langPanel.add(btnAmh);

        formPanel.add(lblTitle);
        formPanel.add(lblSubTitle);
        formPanel.add(lblTagline);
        formPanel.add(Box.createVerticalStrut(25));
        formPanel.add(lblUser);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(txtUsername);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(lblPass);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(txtPassword);
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(forgotPanel);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(btnLogin);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(registerPanel);
        formPanel.add(Box.createVerticalStrut(25));
        formPanel.add(langPanel);

        centerCard.add(leftSpacer);
        centerCard.add(formPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        add(centerCard, gbc);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(new Color(245, 238, 220));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}


