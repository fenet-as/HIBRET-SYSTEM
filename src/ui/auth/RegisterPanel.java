package ui.auth;

import model.User;
import service.AuthService;
import service.impl.AuthServiceImpl;

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
    private final LoginFrame parentFrame;

    private final AuthService authService = new AuthServiceImpl();

    private final String NAME_HINT = "Enter your full name";
    private final String USER_HINT = "Choose a unique username";
    private final String ANSWER_HINT = "Enter your security answer";

    public RegisterPanel(LoginFrame frame) {
        this.parentFrame = frame;

        try {
            backgroundImage = ImageIO.read(new File("src/resources/images/LoginBg.png"));
        } catch (IOException e) {
            System.err.println("Background image not found.");
        }

        setLayout(new GridBagLayout());

        JPanel centerCard = new JPanel(new GridLayout(1, 2, 40, 0));
        centerCard.setOpaque(false);
        centerCard.setPreferredSize(new Dimension(750, 560));

        JPanel leftSpacer = new JPanel();
        leftSpacer.setOpaque(false);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Create Account");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 36));
        lblTitle.setForeground(new Color(101, 53, 15));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSubTitle = new JLabel("Join Us Today");
        lblSubTitle.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblSubTitle.setForeground(new Color(34, 112, 43));
        lblSubTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblName = new JLabel("Full Name");
        lblName.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblName.setForeground(new Color(70, 70, 70));
        lblName.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField txtName = new RoundedTextField("", 20);
        txtName.setFont(new Font("SansSerif", Font.PLAIN, 13));
        configurePlaceholder(txtName, NAME_HINT);
        txtName.setMaximumSize(new Dimension(320, 38));
        txtName.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblUser.setForeground(new Color(70, 70, 70));
        lblUser.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField txtUser = new RoundedTextField("", 20);
        txtUser.setFont(new Font("SansSerif", Font.PLAIN, 13));
        configurePlaceholder(txtUser, USER_HINT);
        txtUser.setMaximumSize(new Dimension(320, 38));
        txtUser.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblPass.setForeground(new Color(70, 70, 70));
        lblPass.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPasswordField txtPass = new RoundedPasswordField(20);
        txtPass.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtPass.setMaximumSize(new Dimension(320, 38));
        txtPass.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSecurityQuestion = new JLabel("Security Question");
        lblSecurityQuestion.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblSecurityQuestion.setForeground(new Color(70, 70, 70));
        lblSecurityQuestion.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] questions = {
                "-- Select a Security Question --",
                "What was the name of your first pet?",
                "What is your mother's maiden name?",
                "What was the name of your elementary school?",
                "In what city or town were you born?",
                "What is your favorite movie?"
        };
        JComboBox<String> comboQuestions = new JComboBox<>(questions);
        comboQuestions.setMaximumSize(new Dimension(320, 38));
        comboQuestions.setAlignmentX(Component.LEFT_ALIGNMENT);
        comboQuestions.setBackground(Color.WHITE);
        comboQuestions.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JLabel lblSecurityAnswer = new JLabel("Security Answer");
        lblSecurityAnswer.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblSecurityAnswer.setForeground(new Color(70, 70, 70));
        lblSecurityAnswer.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField txtSecurityAnswer = new RoundedTextField("", 20);
        txtSecurityAnswer.setFont(new Font("SansSerif", Font.PLAIN, 13));
        configurePlaceholder(txtSecurityAnswer, ANSWER_HINT);
        txtSecurityAnswer.setMaximumSize(new Dimension(320, 38));
        txtSecurityAnswer.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnRegister = new RoundedButton("Register", new Color(34, 112, 43));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnRegister.setMaximumSize(new Dimension(320, 42));
        btnRegister.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnRegister.addActionListener(e -> {
            String fullName = txtName.getText().trim();
            String username = txtUser.getText().trim();
            String password = new String(txtPass.getPassword());
            String question = comboQuestions.getSelectedItem() != null ? comboQuestions.getSelectedItem().toString() : "";
            String answer = txtSecurityAnswer.getText().trim();

            if (fullName.isEmpty() || fullName.equals(NAME_HINT) ||
                    username.isEmpty() || username.equals(USER_HINT) ||
                    answer.isEmpty() || answer.equals(ANSWER_HINT) ||
                    password.isEmpty() || comboQuestions.getSelectedIndex() == 0) {

                JOptionPane.showMessageDialog(this,
                        "Please fill in all required fields and select a security question.",
                        "Registration Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            User user = new User();
            user.setFullName(fullName);
            user.setUsername(username);
            user.setPassword(password);
            user.setSecurityQuestion(question);
            user.setSecurityAnswer(answer);

            boolean success = authService.register(user);

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Registration completed successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                parentFrame.showPage("login");
            } else {
                JOptionPane.showMessageDialog(this,
                        "Registration failed. Username might already be taken.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        backPanel.setOpaque(false);
        backPanel.setMaximumSize(new Dimension(320, 22));
        backPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblHaveAccount = new JLabel("Already have an account?");
        lblHaveAccount.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblHaveAccount.setForeground(new Color(100, 100, 100));

        JLabel lblLogin = new JLabel("Login here");
        lblLogin.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblLogin.setForeground(new Color(101, 53, 15));
        lblLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));

        lblLogin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                parentFrame.showPage("login");
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                lblLogin.setText("<html><u>Login here</u></html>");
            }
            @Override
            public void mouseExited(MouseEvent e) {
                lblLogin.setText("Login here");
            }
        });

        backPanel.add(lblHaveAccount);
        backPanel.add(lblLogin);

        formPanel.add(lblTitle);
        formPanel.add(lblSubTitle);
        formPanel.add(Box.createVerticalStrut(12));
        formPanel.add(lblName);
        formPanel.add(Box.createVerticalStrut(2));
        formPanel.add(txtName);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(lblUser);
        formPanel.add(Box.createVerticalStrut(2));
        formPanel.add(txtUser);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(lblPass);
        formPanel.add(Box.createVerticalStrut(2));
        formPanel.add(txtPass);

        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(lblSecurityQuestion);
        formPanel.add(Box.createVerticalStrut(2));
        formPanel.add(comboQuestions);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(lblSecurityAnswer);
        formPanel.add(Box.createVerticalStrut(2));
        formPanel.add(txtSecurityAnswer);

        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(btnRegister);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(backPanel);

        centerCard.add(leftSpacer);
        centerCard.add(formPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        add(centerCard, gbc);
    }

    private void configurePlaceholder(JTextField textField, String placeholder) {
        textField.setText(placeholder);
        textField.setForeground(Color.GRAY);

        textField.addFocusListener(new java.awt.event.FocusListener() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (textField.getText().trim().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setForeground(Color.GRAY);
                }
            }
        });
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