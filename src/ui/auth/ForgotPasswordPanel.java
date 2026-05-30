package ui.auth;

import service.AuthService;
import service.impl.AuthServiceImpl;
import model.User;
import util.LanguageManager;
import util.FontManager;

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

        JLabel lblTitle = new JLabel(LanguageManager.getString("forgot.title"));
        lblTitle.setFont(FontManager.getBoldFont(46));
        lblTitle.setForeground(new Color(101, 53, 15));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSubTitle = new JLabel(LanguageManager.getString("forgot.subtitle"));
        lblSubTitle.setFont(FontManager.getBoldFont(38));
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

        JLabel lblRemember = new JLabel(LanguageManager.getString("forgot.remember"));
        lblRemember.setFont(FontManager.getPlainFont(13));
        lblRemember.setForeground(new Color(100, 100, 100));

        JLabel lblLogin = new JLabel(LanguageManager.getString("forgot.login_link"));
        lblLogin.setFont(FontManager.getBoldFont(13));
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
                lblLogin.setText("<html><u>" + LanguageManager.getString("forgot.login_link") + "</u></html>");
            }
            @Override
            public void mouseExited(MouseEvent e) {
                lblLogin.setText(LanguageManager.getString("forgot.login_link"));
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

        JLabel lblUser = new JLabel(LanguageManager.getString("forgot.username"));
        lblUser.setFont(FontManager.getBoldFont(13));
        lblUser.setForeground(new Color(70, 70, 70));
        lblUser.setAlignmentX(Component.LEFT_ALIGNMENT);

        String usernameHint = LanguageManager.getString("forgot.hint.username");
        JTextField txtUser = new RoundedTextField(usernameHint, 20);
        txtUser.setFont(FontManager.getPlainFont(13));
        txtUser.setForeground(Color.GRAY);
        txtUser.setMaximumSize(new Dimension(320, 38));
        txtUser.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtUser.addFocusListener(new java.awt.event.FocusListener() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (txtUser.getText().equals(LanguageManager.getString("forgot.hint.username"))) {
                    txtUser.setText("");
                    txtUser.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (txtUser.getText().trim().isEmpty()) {
                    txtUser.setText(LanguageManager.getString("forgot.hint.username"));
                    txtUser.setForeground(Color.GRAY);
                }
            }
        });

        JLabel lblQuestion = new JLabel(LanguageManager.getString("forgot.security_question"));
        lblQuestion.setFont(FontManager.getBoldFont(13));
        lblQuestion.setForeground(new Color(70, 70, 70));
        lblQuestion.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] questions = {
                LanguageManager.getString("register.q0"),
                LanguageManager.getString("register.q1"),
                LanguageManager.getString("register.q2"),
                LanguageManager.getString("register.q3"),
                LanguageManager.getString("register.q4"),
                LanguageManager.getString("register.q5")
        };
        JComboBox<String> comboQuestions = new JComboBox<>(questions);
        comboQuestions.setFont(FontManager.getPlainFont(13));

        Object cellRenderer = comboQuestions.getRenderer();
        if (cellRenderer instanceof JComponent) {
            ((JComponent) cellRenderer).setFont(FontManager.getPlainFont(13));
        }

        comboQuestions.setMaximumSize(new Dimension(320, 42));
        comboQuestions.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblAnswer = new JLabel(LanguageManager.getString("forgot.answer"));
        lblAnswer.setFont(FontManager.getBoldFont(13));
        lblAnswer.setForeground(new Color(70, 70, 70));
        lblAnswer.setAlignmentX(Component.LEFT_ALIGNMENT);

        String answerHint = LanguageManager.getString("forgot.hint.answer");
        JTextField txtAnswer = new RoundedTextField(answerHint, 20);
        txtAnswer.setFont(FontManager.getPlainFont(13));
        txtAnswer.setForeground(Color.GRAY);
        txtAnswer.setMaximumSize(new Dimension(320, 42));
        txtAnswer.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtAnswer.addFocusListener(new java.awt.event.FocusListener() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (txtAnswer.getText().equals(LanguageManager.getString("forgot.hint.answer"))) {
                    txtAnswer.setText("");
                    txtAnswer.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (txtAnswer.getText().trim().isEmpty()) {
                    txtAnswer.setText(LanguageManager.getString("forgot.hint.answer"));
                    txtAnswer.setForeground(Color.GRAY);
                }
            }
        });

        JButton btnVerify = new RoundedButton(LanguageManager.getString("forgot.btn_verify"), new Color(34, 112, 43));
        btnVerify.setForeground(Color.WHITE);
        btnVerify.setFont(FontManager.getBoldFont(14));
        btnVerify.setMaximumSize(new Dimension(320, 45));
        btnVerify.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnVerify.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnVerify.addActionListener(e -> {
            UIManager.put("OptionPane.messageFont", FontManager.getPlainFont(14));
            UIManager.put("OptionPane.buttonFont", FontManager.getPlainFont(13));

            String username = txtUser.getText().trim();
            String selectedQuestion = (String) comboQuestions.getSelectedItem();
            String answer = txtAnswer.getText().trim();

            if (username.isEmpty() || username.equals(usernameHint) || comboQuestions.getSelectedIndex() == 0 || answer.isEmpty() || answer.equals(answerHint)) {
                JOptionPane.showMessageDialog(this, LanguageManager.getString("forgot.err.missing_step1"), LanguageManager.getString("msg.error"), JOptionPane.ERROR_MESSAGE);
                return;
            }

            currentUser = authService.findUser(username);

            if (currentUser == null) {
                JOptionPane.showMessageDialog(this, LanguageManager.getString("forgot.err.not_found"), LanguageManager.getString("msg.error"), JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!currentUser.getSecurityQuestion().equals(selectedQuestion)) {
                JOptionPane.showMessageDialog(this, LanguageManager.getString("forgot.err.question_mismatch"), LanguageManager.getString("msg.error"), JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!currentUser.getSecurityAnswer().equalsIgnoreCase(answer)) {
                JOptionPane.showMessageDialog(this, LanguageManager.getString("forgot.err.wrong_answer"), LanguageManager.getString("msg.error"), JOptionPane.ERROR_MESSAGE);
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

        JLabel lblNewPass = new JLabel(LanguageManager.getString("forgot.new_password"));
        lblNewPass.setFont(FontManager.getBoldFont(13));
        lblNewPass.setForeground(new Color(70, 70, 70));
        lblNewPass.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPasswordField txtNewPass = new RoundedPasswordField(20);
        txtNewPass.setFont(FontManager.getPlainFont(13));
        txtNewPass.setMaximumSize(new Dimension(320, 42));
        txtNewPass.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblConfirmPass = new JLabel(LanguageManager.getString("forgot.confirm_password"));
        lblConfirmPass.setFont(FontManager.getBoldFont(13));
        lblConfirmPass.setForeground(new Color(70, 70, 70));
        lblConfirmPass.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPasswordField txtConfirmPass = new RoundedPasswordField(20);
        txtConfirmPass.setFont(FontManager.getPlainFont(13));
        txtConfirmPass.setMaximumSize(new Dimension(320, 42));
        txtConfirmPass.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnReset = new RoundedButton(LanguageManager.getString("forgot.btn_reset"), new Color(34, 112, 43));
        btnReset.setForeground(Color.WHITE);
        btnReset.setFont(FontManager.getBoldFont(14));
        btnReset.setMaximumSize(new Dimension(320, 45));
        btnReset.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnReset.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnReset.addActionListener(e -> {
            UIManager.put("OptionPane.messageFont", FontManager.getPlainFont(14));
            UIManager.put("OptionPane.buttonFont", FontManager.getPlainFont(13));

            String newPassword = new String(txtNewPass.getPassword());
            String confirmPassword = new String(txtConfirmPass.getPassword());

            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this, LanguageManager.getString("forgot.err.empty_fields"), LanguageManager.getString("msg.error"), JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, LanguageManager.getString("forgot.err.mismatch_pass"), LanguageManager.getString("msg.error"), JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (currentUser == null) {
                JOptionPane.showMessageDialog(this, LanguageManager.getString("forgot.err.session_expired"), LanguageManager.getString("msg.error"), JOptionPane.ERROR_MESSAGE);
                formCardLayout.show(dynamicFormContainer, "step1");
                return;
            }

            authService.resetPassword(currentUser.getUsername(), newPassword);

            JOptionPane.showMessageDialog(this, LanguageManager.getString("forgot.success.msg"), LanguageManager.getString("msg.success"), JOptionPane.INFORMATION_MESSAGE);

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