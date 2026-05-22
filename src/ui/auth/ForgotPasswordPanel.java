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

public class ForgotPasswordPanel extends JPanel {
    private Image backgroundImage;
    private final LoginFrame parentFrame;
    private CardLayout formCardLayout;
    private JPanel dynamicFormContainer;

    public ForgotPasswordPanel(LoginFrame frame) {
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

        // Left Side: Spacer for Background Art
        JPanel leftSpacer = new JPanel();
        leftSpacer.setOpaque(false);

        // Right Side: Base Form Container
        JPanel baseFormPanel = new JPanel();
        baseFormPanel.setLayout(new BoxLayout(baseFormPanel, BoxLayout.Y_AXIS));
        baseFormPanel.setOpaque(false);

        // Typography
        JLabel lblTitle = new JLabel("RESET");
        lblTitle.setFont(new Font("Serif", Font.BOLD, 46));
        lblTitle.setForeground(new Color(101, 53, 15));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSubTitle = new JLabel("PASSWORD");
        lblSubTitle.setFont(new Font("SansSerif", Font.BOLD, 38));
        lblSubTitle.setForeground(new Color(34, 112, 43));
        lblSubTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        // --- Dynamic Content Container (Card Layout for Step 1 & Step 2) ---
        formCardLayout = new CardLayout();
        dynamicFormContainer = new JPanel(formCardLayout);
        dynamicFormContainer.setOpaque(false);
        dynamicFormContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Initialize Steps
        JPanel step1Panel = createSecurityQuestionStep();
        JPanel step2Panel = createNewPasswordStep();

        dynamicFormContainer.add(step1Panel, "step1");
        dynamicFormContainer.add(step2Panel, "step2");

        // --- Back to Login Link ---
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        backPanel.setOpaque(false);
        backPanel.setMaximumSize(new Dimension(320, 25));
        backPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblRemember = new JLabel("Remembered your password?");
        lblRemember.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblRemember.setForeground(new Color(100, 100, 100));

        JLabel lblLogin = new JLabel("Login");
        lblLogin.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblLogin.setForeground(new Color(101, 53, 15));
        lblLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblLogin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                formCardLayout.show(dynamicFormContainer, "step1");
                parentFrame.showPage("login");
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
        backPanel.add(lblRemember);
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
        baseFormPanel.add(lblTitle);
        baseFormPanel.add(lblSubTitle);
        baseFormPanel.add(Box.createVerticalStrut(20));
        baseFormPanel.add(dynamicFormContainer);
        baseFormPanel.add(Box.createVerticalStrut(15));
        baseFormPanel.add(backPanel);
        baseFormPanel.add(Box.createVerticalStrut(25)); // Spacing before language selector
        baseFormPanel.add(langPanel);                 // Added to the bottom

        centerCard.add(leftSpacer);
        centerCard.add(baseFormPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        add(centerCard, gbc);
    }

    // --- STEP 1: Security Verification Card ---
    private JPanel createSecurityQuestionStep() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblUser.setForeground(new Color(70, 70, 70));
        lblUser.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField txtUser = new RoundedTextField("  Enter your username", 20);
        txtUser.setMaximumSize(new Dimension(320, 42));
        txtUser.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblQuestion = new JLabel("Security Question");
        lblQuestion.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblQuestion.setForeground(new Color(70, 70, 70));
        lblQuestion.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] questions = {
                "Select a security question...",
                "1. What is your mother’s name?",
                "2. What is your father’s name?",
                "3. What is your favorite place?",
                "4. What is your hometown?",
                "5. What is your first school name?"
        };
        JComboBox<String> comboQuestions = new JComboBox<>(questions);
        comboQuestions.setFont(new Font("SansSerif", Font.PLAIN, 13));
        comboQuestions.setMaximumSize(new Dimension(320, 42));
        comboQuestions.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblAnswer = new JLabel("Your Answer");
        lblAnswer.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblAnswer.setForeground(new Color(70, 70, 70));
        lblAnswer.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField txtAnswer = new RoundedTextField("  Type your answer", 20);
        txtAnswer.setMaximumSize(new Dimension(320, 42));
        txtAnswer.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnVerify = new RoundedButton("VERIFY ANSWER", new Color(34, 112, 43));
        btnVerify.setForeground(Color.WHITE);
        btnVerify.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnVerify.setMaximumSize(new Dimension(320, 45));
        btnVerify.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnVerify.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnVerify.addActionListener(e -> {
            if (txtUser.getText().trim().isEmpty() || comboQuestions.getSelectedIndex() == 0 || txtAnswer.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fulfill all security requirements.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                formCardLayout.show(dynamicFormContainer, "step2");
            }
        });

        panel.add(lblUser);
        panel.add(Box.createVerticalStrut(5));
        panel.add(txtUser);
        panel.add(Box.createVerticalStrut(10));
        panel.add(lblQuestion);
        panel.add(Box.createVerticalStrut(5));
        panel.add(comboQuestions);
        panel.add(Box.createVerticalStrut(10));
        panel.add(lblAnswer);
        panel.add(Box.createVerticalStrut(5));
        panel.add(txtAnswer);
        panel.add(Box.createVerticalStrut(20));
        panel.add(btnVerify);

        return panel;
    }

    // --- STEP 2: New Password Input Card ---
    private JPanel createNewPasswordStep() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel lblNewPass = new JLabel("New Password");
        lblNewPass.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblNewPass.setForeground(new Color(70, 70, 70));
        lblNewPass.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPasswordField txtNewPass = new RoundedPasswordField(20);
        txtNewPass.setMaximumSize(new Dimension(320, 42));
        txtNewPass.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblConfirmPass = new JLabel("Confirm New Password");
        lblConfirmPass.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblConfirmPass.setForeground(new Color(70, 70, 70));
        lblConfirmPass.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPasswordField txtConfirmPass = new RoundedPasswordField(20);
        txtConfirmPass.setMaximumSize(new Dimension(320, 42));
        txtConfirmPass.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnReset = new RoundedButton("UPDATE PASSWORD", new Color(34, 112, 43));
        btnReset.setForeground(Color.WHITE);
        btnReset.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnReset.setMaximumSize(new Dimension(320, 45));
        btnReset.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnReset.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnReset.addActionListener(e -> {
            String newPassword = new String(txtNewPass.getPassword());
            String confirmPassword = new String(txtConfirmPass.getPassword());

            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Fields cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Password updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                formCardLayout.show(dynamicFormContainer, "step1");
                parentFrame.showPage("login");
            }
        });

        panel.add(lblNewPass);
        panel.add(Box.createVerticalStrut(5));
        panel.add(txtNewPass);
        panel.add(Box.createVerticalStrut(15));
        panel.add(lblConfirmPass);
        panel.add(Box.createVerticalStrut(5));
        panel.add(txtConfirmPass);
        panel.add(Box.createVerticalStrut(25));
        panel.add(btnReset);

        panel.add(Box.createVerticalStrut(65));

        return panel;
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