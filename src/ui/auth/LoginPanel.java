package ui.auth;

import dao.impl.EdirDAOImpl;
import dao.impl.ReportDAOImpl;
import dao.impl.EqubDAOImpl;
import service.*;
import service.impl.AuthServiceImpl;
import service.impl.EdirServiceImpl;
import service.impl.ReportServiceImpl;
import service.impl.EqubServiceImpl;
import model.User;
import ui.core.MainFrame;
import util.LanguageManager;

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

    // --- State-Driven Business Services ---
    private final AuthService authService = new AuthServiceImpl();
    private final ReportService reportService = new ReportServiceImpl(new ReportDAOImpl());
    private final EqubService equbService = new EqubServiceImpl(new EqubDAOImpl());
    private final EdirService edirService = new EdirServiceImpl(new EdirDAOImpl());

    // Dynamic Font String Resolver based on Runtime Operating System
    private String getAmharicFontName() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) return "Nyala";
        if (os.contains("mac")) return "Kefa";
        return "Abyssinica SIL";
    }

    public LoginPanel(LoginFrame frame) {
        this.parentFrame = frame;

        // Resolve target font styling globally for text layout bindings
        String amhFont = getAmharicFontName();

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
        lblSubTitle.setFont(new Font(amhFont, Font.BOLD, 38)); // Supports localized brand headings if changed
        lblSubTitle.setForeground(new Color(34, 112, 43));
        lblSubTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ✅ Updated Font Assignment for i18n Elements
        JLabel lblTagline = new JLabel(LanguageManager.getString("login.tagline"));
        lblTagline.setFont(new Font(amhFont, Font.PLAIN, 14));
        lblTagline.setForeground(new Color(80, 80, 80));
        lblTagline.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblUser = new JLabel(LanguageManager.getString("login.username"));
        lblUser.setFont(new Font(amhFont, Font.BOLD, 13));
        lblUser.setForeground(new Color(70, 70, 70));
        lblUser.setAlignmentX(Component.LEFT_ALIGNMENT);

        String placeholderText = LanguageManager.getString("login.placeholder");
        JTextField txtUsername = new RoundedTextField(placeholderText, 20);
        txtUsername.setFont(new Font(amhFont, Font.PLAIN, 14));
        txtUsername.setForeground(Color.GRAY);
        txtUsername.setMaximumSize(new Dimension(320, 42));
        txtUsername.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtUsername.addFocusListener(new java.awt.event.FocusListener() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (txtUsername.getText().equals(LanguageManager.getString("login.placeholder"))) {
                    txtUsername.setText("");
                    txtUsername.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (txtUsername.getText().trim().isEmpty()) {
                    txtUsername.setText(LanguageManager.getString("login.placeholder"));
                    txtUsername.setForeground(Color.GRAY);
                }
            }
        });

        JLabel lblPass = new JLabel(LanguageManager.getString("login.password"));
        lblPass.setFont(new Font(amhFont, Font.BOLD, 13));
        lblPass.setForeground(new Color(70, 70, 70));
        lblPass.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPasswordField txtPassword = new RoundedPasswordField(20);
        txtPassword.setFont(new Font(amhFont, Font.PLAIN, 14));
        txtPassword.setMaximumSize(new Dimension(320, 42));
        txtPassword.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel forgotPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        forgotPanel.setOpaque(false);
        forgotPanel.setMaximumSize(new Dimension(320, 20));
        forgotPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblForgot = new JLabel(LanguageManager.getString("login.forgot"));
        lblForgot.setFont(new Font(amhFont, Font.PLAIN, 12));
        lblForgot.setForeground(new Color(101, 53, 15));
        lblForgot.setCursor(new Cursor(Cursor.HAND_CURSOR));

        lblForgot.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                parentFrame.showPage("forgot");
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                lblForgot.setText("<html><u>" + LanguageManager.getString("login.forgot") + "</u></html>");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                lblForgot.setText(LanguageManager.getString("login.forgot"));
            }
        });

        forgotPanel.add(lblForgot);

        JButton btnLogin = new RoundedButton(LanguageManager.getString("login.btn_login"), new Color(34, 112, 43));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font(amhFont, Font.BOLD, 14));
        btnLogin.setMaximumSize(new Dimension(320, 45));
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnLogin.addActionListener(e -> {
            String username = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword());

            if (username.isEmpty() || username.equals(LanguageManager.getString("login.placeholder")) || password.isEmpty()) {
                // Wrap JOptionPane with dynamic fonts to fix dialog boxes showing up empty
                UIManager.put("OptionPane.messageFont", new Font(amhFont, Font.PLAIN, 14));
                UIManager.put("OptionPane.buttonFont", new Font(amhFont, Font.PLAIN, 13));

                JOptionPane.showMessageDialog(this,
                        LanguageManager.getString("login.err.missing"),
                        LanguageManager.getString("login.err.title"),
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            User user = authService.login(username, password);

            if (user != null) {
                parentFrame.dispose();
                new MainFrame(user, reportService, equbService, edirService);
            } else {
                UIManager.put("OptionPane.messageFont", new Font(amhFont, Font.PLAIN, 14));
                UIManager.put("OptionPane.buttonFont", new Font(amhFont, Font.PLAIN, 13));

                JOptionPane.showMessageDialog(this,
                        LanguageManager.getString("login.err.invalid"),
                        LanguageManager.getString("login.err.auth_failed"),
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel registerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        registerPanel.setOpaque(false);
        registerPanel.setMaximumSize(new Dimension(320, 25));
        registerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblNoAccount = new JLabel(LanguageManager.getString("login.no_account"));
        lblNoAccount.setFont(new Font(amhFont, Font.PLAIN, 13));
        lblNoAccount.setForeground(new Color(100, 100, 100));

        JLabel lblRegister = new JLabel(LanguageManager.getString("login.sign_up"));
        lblRegister.setFont(new Font(amhFont, Font.BOLD, 13));
        lblRegister.setForeground(new Color(34, 112, 43));
        lblRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));

        lblRegister.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                parentFrame.showPage("register");
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                lblRegister.setText("<html><u>" + LanguageManager.getString("login.sign_up") + "</u></html>");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                lblRegister.setText(LanguageManager.getString("login.sign_up"));
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
        btnAmh.setFont(new Font(getAmharicFontName(), Font.PLAIN, 14));
        btnAmh.setFocusPainted(false);

        // ✅ Runtime Language Shift Handlers
        btnEng.addActionListener(e -> {
            LanguageManager.setLanguage("en");
            parentFrame.showPage("login"); // Forces parent context layout re-rendering
        });

        btnAmh.addActionListener(e -> {
            LanguageManager.setLanguage("am");
            parentFrame.showPage("login"); // Forces parent context layout re-rendering
        });

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