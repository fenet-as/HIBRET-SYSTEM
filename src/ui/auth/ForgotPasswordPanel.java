package ui.auth;

import service.AuthService;
import service.impl.AuthServiceImpl;
import model.User;

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

    private final AuthService authService = new AuthServiceImpl();
    private User currentUser;

    public ForgotPasswordPanel(LoginFrame frame) {
        this.parentFrame = frame;

        try {
            backgroundImage = ImageIO.read(new File("src/resources/images/LoginBg.png"));
        } catch (IOException e) {
            System.err.println("Background image not found.");
        }

        setLayout(new GridBagLayout());

        JPanel centerCard = new JPanel(new GridLayout(1, 2, 40, 0));
        centerCard.setOpaque(false);
        centerCard.setPreferredSize(new Dimension(750, 520));

        JPanel leftSpacer = new JPanel();
        leftSpacer.setOpaque(false);

        JPanel baseFormPanel = new JPanel();
        baseFormPanel.setLayout(new BoxLayout(baseFormPanel, BoxLayout.Y_AXIS));
        baseFormPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Forgot Password");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 46));
        lblTitle.setForeground(new Color(101, 53, 15));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSubTitle = new JLabel("Reset Account");
        lblSubTitle.setFont(new Font("SansSerif", Font.BOLD, 38));
        lblSubTitle.setForeground(new Color(34, 112, 43));
        lblSubTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        formCardLayout = new CardLayout();
        dynamicFormContainer = new JPanel(formCardLayout);
        dynamicFormContainer.setOpaque(false);
        dynamicFormContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel step1Panel = createSecurityQuestionStep();
        JPanel step2Panel = createNewPasswordStep();

        dynamicFormContainer.add(step1Panel, "step1");
        dynamicFormContainer.add(step2Panel, "step2");

        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        backPanel.setOpaque(false);
        backPanel.setMaximumSize(new Dimension(320, 25));
        backPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblRemember = new JLabel("Remembered your credentials?");
        lblRemember.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblRemember.setForeground(new Color(100, 100, 100));

        JLabel lblLogin = new JLabel("Login here");
        lblLogin.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblLogin.setForeground(new Color(101, 53, 15));
        lblLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblLogin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                currentUser = null;
                formCardLayout.show(dynamicFormContainer, "step1");
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
        backPanel.add(lblRemember);
        backPanel.add(lblLogin);

        baseFormPanel.add(lblTitle);
        baseFormPanel.add(lblSubTitle);
        baseFormPanel.add(Box.createVerticalStrut(20));
        baseFormPanel.add(dynamicFormContainer);
        baseFormPanel.add(Box.createVerticalStrut(15));
        baseFormPanel.add(backPanel);

        centerCard.add(leftSpacer);
        centerCard.add(baseFormPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        add(centerCard, gbc);
    }

    private JPanel createSecurityQuestionStep() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblUser.setForeground(new Color(70, 70, 70));
        lblUser.setAlignmentX(Component.LEFT_ALIGNMENT);

        String usernameHint = "Enter your username";
        JTextField txtUser = new RoundedTextField(usernameHint, 20);
        txtUser.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtUser.setForeground(Color.GRAY);
        txtUser.setMaximumSize(new Dimension(320, 38));
        txtUser.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtUser.addFocusListener(new java.awt.event.FocusListener() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (txtUser.getText().equals(usernameHint)) {
                    txtUser.setText("");
                    txtUser.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (txtUser.getText().trim().isEmpty()) {
                    txtUser.setText(usernameHint);
                    txtUser.setForeground(Color.GRAY);
                }
            }
        });

        JLabel lblQuestion = new JLabel("Security Question");
        lblQuestion.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblQuestion.setForeground(new Color(70, 70, 70));
        lblQuestion.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Security questions updated to precisely match RegisterPanel options
        String[] questions = {
                "-- Select a Security Question --",
                "What was the name of your first pet?",
                "What is your mother's maiden name?",
                "What was the name of your elementary school?",
                "In what city or town were you born?",
                "What is your favorite movie?"
        };
        JComboBox<String> comboQuestions = new JComboBox<>(questions);
        comboQuestions.setFont(new Font("SansSerif", Font.PLAIN, 13));

        Object cellRenderer = comboQuestions.getRenderer();
        if (cellRenderer instanceof JComponent) {
            ((JComponent) cellRenderer).setFont(new Font("SansSerif", Font.PLAIN, 13));
        }

        comboQuestions.setMaximumSize(new Dimension(320, 42));
        comboQuestions.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblAnswer = new JLabel("Security Answer");
        lblAnswer.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblAnswer.setForeground(new Color(70, 70, 70));
        lblAnswer.setAlignmentX(Component.LEFT_ALIGNMENT);

        String answerHint = "Enter your answer";
        JTextField txtAnswer = new RoundedTextField(answerHint, 20);
        txtAnswer.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtAnswer.setForeground(Color.GRAY);
        txtAnswer.setMaximumSize(new Dimension(320, 42));
        txtAnswer.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtAnswer.addFocusListener(new java.awt.event.FocusListener() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (txtAnswer.getText().equals(answerHint)) {
                    txtAnswer.setText("");
                    txtAnswer.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (txtAnswer.getText().trim().isEmpty()) {
                    txtAnswer.setText(answerHint);
                    txtAnswer.setForeground(Color.GRAY);
                }
            }
        });

        JButton btnVerify = new RoundedButton("Verify Identity", new Color(34, 112, 43));
        btnVerify.setForeground(Color.WHITE);
        btnVerify.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnVerify.setMaximumSize(new Dimension(320, 45));
        btnVerify.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnVerify.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnVerify.addActionListener(e -> {
            UIManager.put("OptionPane.messageFont", new Font("SansSerif", Font.PLAIN, 14));
            UIManager.put("OptionPane.buttonFont", new Font("SansSerif", Font.PLAIN, 13));

            String username = txtUser.getText().trim();
            String selectedQuestion = (String) comboQuestions.getSelectedItem();
            String answer = txtAnswer.getText().trim();

            if (username.isEmpty() || username.equals(usernameHint) || comboQuestions.getSelectedIndex() == 0 || answer.isEmpty() || answer.equals(answerHint)) {
                JOptionPane.showMessageDialog(this, "Please fulfill all inputs correctly.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            currentUser = authService.findUser(username);

            if (currentUser == null) {
                JOptionPane.showMessageDialog(this, "No matching account discovered.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!currentUser.getSecurityQuestion().equals(selectedQuestion)) {
                JOptionPane.showMessageDialog(this, "Security profile does not match registration logs.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!currentUser.getSecurityAnswer().equalsIgnoreCase(answer)) {
                JOptionPane.showMessageDialog(this, "Incorrect answer supplied.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            formCardLayout.show(dynamicFormContainer, "step2");
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

    private JPanel createNewPasswordStep() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel lblNewPass = new JLabel("New Password");
        lblNewPass.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblNewPass.setForeground(new Color(70, 70, 70));
        lblNewPass.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPasswordField txtNewPass = new RoundedPasswordField(20);
        txtNewPass.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtNewPass.setMaximumSize(new Dimension(320, 42));
        txtNewPass.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblConfirmPass = new JLabel("Confirm Password");
        lblConfirmPass.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblConfirmPass.setForeground(new Color(70, 70, 70));
        lblConfirmPass.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPasswordField txtConfirmPass = new RoundedPasswordField(20);
        txtConfirmPass.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtConfirmPass.setMaximumSize(new Dimension(320, 42));
        txtConfirmPass.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnReset = new RoundedButton("Update Password", new Color(34, 112, 43));
        btnReset.setForeground(Color.WHITE);
        btnReset.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnReset.setMaximumSize(new Dimension(320, 45));
        btnReset.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnReset.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnReset.addActionListener(e -> {
            UIManager.put("OptionPane.messageFont", new Font("SansSerif", Font.PLAIN, 14));
            UIManager.put("OptionPane.buttonFont", new Font("SansSerif", Font.PLAIN, 13));

            String newPassword = new String(txtNewPass.getPassword());
            String confirmPassword = new String(txtConfirmPass.getPassword());

            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Fields cannot be left blank.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (currentUser == null) {
                JOptionPane.showMessageDialog(this, "Session expired. Please restart sequence.", "Error", JOptionPane.ERROR_MESSAGE);
                formCardLayout.show(dynamicFormContainer, "step1");
                return;
            }

            authService.resetPassword(currentUser.getUsername(), newPassword);

            JOptionPane.showMessageDialog(this, "Password reset successful!", "Success", JOptionPane.INFORMATION_MESSAGE);

            txtNewPass.setText("");
            txtConfirmPass.setText("");
            currentUser = null;
            formCardLayout.show(dynamicFormContainer, "step1");
            parentFrame.showPage("login");
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