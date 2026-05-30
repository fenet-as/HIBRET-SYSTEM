package ui.auth;

import model.User;
import service.AuthService;
import service.impl.AuthServiceImpl;
import util.LanguageManager;
import util.FontManager; // ✅ Imported FontManager

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

    // ✅ Dynamic Hints via Translation bundle properties
    private final String NAME_HINT = LanguageManager.getString("register.hint.fullname");
    private final String USER_HINT = LanguageManager.getString("register.hint.username");
    private final String ANSWER_HINT = LanguageManager.getString("register.hint.answer");

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

        // ✅ Fixed Headers Font with dynamic FontManager mapping configurations
        JLabel lblTitle = new JLabel(LanguageManager.getString("register.title"));
        lblTitle.setFont(FontManager.getBoldFont(36));
        lblTitle.setForeground(new Color(101, 53, 15));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSubTitle = new JLabel(LanguageManager.getString("register.subtitle"));
        lblSubTitle.setFont(FontManager.getBoldFont(28));
        lblSubTitle.setForeground(new Color(34, 112, 43));
        lblSubTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ✅ Fixed Form Field Annotations and Input Components Typography
        JLabel lblName = new JLabel(LanguageManager.getString("register.fullname"));
        lblName.setFont(FontManager.getBoldFont(13));
        lblName.setForeground(new Color(70, 70, 70));
        lblName.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField txtName = new RoundedTextField("", 20);
        txtName.setFont(FontManager.getPlainFont(13));
        configurePlaceholder(txtName, NAME_HINT);
        txtName.setMaximumSize(new Dimension(320, 38));
        txtName.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblUser = new JLabel(LanguageManager.getString("register.username"));
        lblUser.setFont(FontManager.getBoldFont(13));
        lblUser.setForeground(new Color(70, 70, 70));
        lblUser.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField txtUser = new RoundedTextField("", 20);
        txtUser.setFont(FontManager.getPlainFont(13));
        configurePlaceholder(txtUser, USER_HINT);
        txtUser.setMaximumSize(new Dimension(320, 38));
        txtUser.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblPass = new JLabel(LanguageManager.getString("register.password"));
        lblPass.setFont(FontManager.getBoldFont(13));
        lblPass.setForeground(new Color(70, 70, 70));
        lblPass.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPasswordField txtPass = new RoundedPasswordField(20);
        txtPass.setFont(FontManager.getPlainFont(13));
        txtPass.setMaximumSize(new Dimension(320, 38));
        txtPass.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSecurityQuestion = new JLabel(LanguageManager.getString("register.security_question"));
        lblSecurityQuestion.setFont(FontManager.getBoldFont(13));
        lblSecurityQuestion.setForeground(new Color(70, 70, 70));
        lblSecurityQuestion.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ✅ Bound Drop-down Security Options Array
        String[] questions = {
                LanguageManager.getString("register.q0"),
                LanguageManager.getString("register.q1"),
                LanguageManager.getString("register.q2"),
                LanguageManager.getString("register.q3"),
                LanguageManager.getString("register.q4"),
                LanguageManager.getString("register.q5")
        };
        JComboBox<String> comboQuestions = new JComboBox<>(questions);
        comboQuestions.setMaximumSize(new Dimension(320, 38));
        comboQuestions.setAlignmentX(Component.LEFT_ALIGNMENT);
        comboQuestions.setBackground(Color.WHITE);

        // ✅ CRUCIAL FIX: Forcing dropdown selection list overlay elements to inherit Amharic font rendering
        comboQuestions.setFont(FontManager.getPlainFont(13));
        Object listRenderer = comboQuestions.getRenderer();
        if (listRenderer instanceof JComponent) {
            ((JComponent) listRenderer).setFont(FontManager.getPlainFont(12));
        }

        JLabel lblSecurityAnswer = new JLabel(LanguageManager.getString("register.security_answer"));
        lblSecurityAnswer.setFont(FontManager.getBoldFont(13));
        lblSecurityAnswer.setForeground(new Color(70, 70, 70));
        lblSecurityAnswer.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField txtSecurityAnswer = new RoundedTextField("", 20);
        txtSecurityAnswer.setFont(FontManager.getPlainFont(13));
        configurePlaceholder(txtSecurityAnswer, ANSWER_HINT);
        txtSecurityAnswer.setMaximumSize(new Dimension(320, 38));
        txtSecurityAnswer.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnRegister = new RoundedButton(LanguageManager.getString("register.btn_register"), new Color(34, 112, 43));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFont(FontManager.getBoldFont(14));
        btnRegister.setMaximumSize(new Dimension(320, 42));
        btnRegister.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnRegister.addActionListener(e -> {
            // Apply dynamic dialog fonts inside actions
            UIManager.put("OptionPane.messageFont", FontManager.getPlainFont(14));
            UIManager.put("OptionPane.buttonFont", FontManager.getPlainFont(13));

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
                        LanguageManager.getString("register.err.required"),
                        LanguageManager.getString("register.err.title"),
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
                        LanguageManager.getString("register.success.msg"),
                        LanguageManager.getString("msg.success"),
                        JOptionPane.INFORMATION_MESSAGE);
                parentFrame.showPage("login");
            } else {
                JOptionPane.showMessageDialog(this,
                        LanguageManager.getString("register.err.failed"),
                        LanguageManager.getString("msg.error"),
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        backPanel.setOpaque(false);
        backPanel.setMaximumSize(new Dimension(320, 22));
        backPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblHaveAccount = new JLabel(LanguageManager.getString("register.have_account"));
        lblHaveAccount.setFont(FontManager.getPlainFont(13));
        lblHaveAccount.setForeground(new Color(100, 100, 100));

        JLabel lblLogin = new JLabel(LanguageManager.getString("register.login_link"));
        lblLogin.setFont(FontManager.getBoldFont(13));
        lblLogin.setForeground(new Color(101, 53, 15));
        lblLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));

        lblLogin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                parentFrame.showPage("login");
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                lblLogin.setText("<html><u>" + LanguageManager.getString("register.login_link") + "</u></html>");
            }
            @Override
            public void mouseExited(MouseEvent e) {
                lblLogin.setText(LanguageManager.getString("register.login_link"));
            }
        });

        backPanel.add(lblHaveAccount);
        backPanel.add(lblLogin);

        JPanel langPanel = new JPanel(new GridLayout(1, 2));
        langPanel.setOpaque(false);
        langPanel.setMaximumSize(new Dimension(160, 30));
        langPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnEng = new JButton("English");
        btnEng.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnEng.setFocusPainted(false);

        JButton btnAmh = new JButton("አማርኛ");
        btnAmh.setFont(new Font(FontManager.getAmharicFontName(), Font.PLAIN, 13));
        btnAmh.setFocusPainted(false);

        // ✅ Hot Language Selection Redirection Sweepers
        btnEng.addActionListener(e -> {
            LanguageManager.setLanguage("en");
            parentFrame.showPage("register");
        });

        btnAmh.addActionListener(e -> {
            LanguageManager.setLanguage("am");
            parentFrame.showPage("register");
        });

        langPanel.add(btnEng);
        langPanel.add(btnAmh);

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
        formPanel.add(Box.createVerticalStrut(12));
        formPanel.add(langPanel);

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