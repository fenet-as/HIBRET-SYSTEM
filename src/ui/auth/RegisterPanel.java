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

public class RegisterPanel extends JPanel {
    private Image backgroundImage;
    private final LoginFrame parentFrame; // To trigger page switching

    public RegisterPanel(LoginFrame frame) {
        this.parentFrame = frame;

        try {
            backgroundImage = ImageIO.read(new File("src/resources/images/LoginBg.png"));
        } catch (IOException e) {
            System.err.println("Background image not found.");
        }

        setLayout(new GridBagLayout());

        // --- Center Container ---
        JPanel centerCard = new JPanel(new GridLayout(1, 2, 40, 0));
        centerCard.setOpaque(false);
        centerCard.setPreferredSize(new Dimension(750, 520));

        // Left Side: Spacer (Preserves space for coffee pot graphic art)
        JPanel leftSpacer = new JPanel();
        leftSpacer.setOpaque(false);

        // Right Side: Form
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);

        // Typography
        JLabel lblTitle = new JLabel("CREATE");
        lblTitle.setFont(new Font("Serif", Font.BOLD, 46));
        lblTitle.setForeground(new Color(101, 53, 15));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSubTitle = new JLabel("ACCOUNT");
        lblSubTitle.setFont(new Font("SansSerif", Font.BOLD, 38));
        lblSubTitle.setForeground(new Color(34, 112, 43));
        lblSubTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        // --- Inputs ---
        JLabel lblName = new JLabel("Full Name");
        lblName.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblName.setForeground(new Color(70, 70, 70));
        lblName.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField txtName = new RoundedTextField("  Enter full name", 20);
        txtName.setMaximumSize(new Dimension(320, 42));
        txtName.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblUser.setForeground(new Color(70, 70, 70));
        lblUser.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField txtUser = new RoundedTextField("  Choose username", 20);
        txtUser.setMaximumSize(new Dimension(320, 42));
        txtUser.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblPass.setForeground(new Color(70, 70, 70));
        lblPass.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPasswordField txtPass = new RoundedPasswordField(20);
        txtPass.setMaximumSize(new Dimension(320, 42));
        txtPass.setAlignmentX(Component.LEFT_ALIGNMENT);

        // --- Action Button ---
        JButton btnRegister = new RoundedButton("REGISTER", new Color(34, 112, 43));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnRegister.setMaximumSize(new Dimension(320, 45));
        btnRegister.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // --- Back to Login Link ---
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        backPanel.setOpaque(false);
        backPanel.setMaximumSize(new Dimension(320, 25));
        backPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblHaveAccount = new JLabel("Already have an account?");
        lblHaveAccount.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblHaveAccount.setForeground(new Color(100, 100, 100));

        JLabel lblLogin = new JLabel("Login");
        lblLogin.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblLogin.setForeground(new Color(101, 53, 15));
        lblLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));

        lblLogin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                parentFrame.showPage("login"); // Route back safely
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                lblLogin.setText("<html><u>Login</u></html>");
            }
            @Override
            public void mouseExited(MouseEvent e) {
                lblLogin.setText("Login");
            }
        });

        backPanel.add(lblHaveAccount);
        backPanel.add(lblLogin);

        // --- Language Toggle Panel (ADDED) ---
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

        // --- Assembly ---
        formPanel.add(lblTitle);
        formPanel.add(lblSubTitle);
        formPanel.add(Box.createVerticalStrut(25));
        formPanel.add(lblName);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(txtName);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(lblUser);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(txtUser);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(lblPass);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(txtPass);
        formPanel.add(Box.createVerticalStrut(25));
        formPanel.add(btnRegister);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(backPanel);
        formPanel.add(Box.createVerticalStrut(25)); // Spacing between link and language options
        formPanel.add(langPanel);                 // Added at the bottom

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