package Tests;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class LoginTest extends JFrame {

    private Image backgroundImage;

    public LoginTest() {
        // Try to load the generated background image
        try {
            // Adjust the path if you place it in a resource folder, e.g., getClass().getResource("/background.png")
            backgroundImage = ImageIO.read(new File("src/resources/images/LoginBg.png"));
        } catch (IOException e) {
            System.err.println("Could not load background image. Using fallback color.");
            e.printStackTrace();
        }

        setTitle("Hibret System - Login Screen");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 650); // Balanced resolution for the image aspect ratio
        setLocationRelativeTo(null);
        setResizable(true);

        // 1. Custom Background Panel
        JPanel mainBackgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    // Draw image scaled to fill the entire frame smoothly
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                } else {
                    // Fallback background color if image is missing
                    g.setColor(new Color(245, 238, 220));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };

        // Use GridBagLayout to position the login card cleanly on the right half
        mainBackgroundPanel.setLayout(new GridBagLayout());

        // 2. Right-side Form Container (Transparent to let the background show through)
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);

        // --- Brand Typography ---
        JLabel lblTitle = new JLabel("HIBRET");
        lblTitle.setFont(new Font("Serif", Font.BOLD, 46));
        lblTitle.setForeground(new Color(101, 53, 15)); // Cultured Dark Brown
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSubTitle = new JLabel("SYSTEM");
        lblSubTitle.setFont(new Font("SansSerif", Font.BOLD, 38));
        lblSubTitle.setForeground(new Color(34, 112, 43)); // Vibrant Green
        lblSubTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTagline = new JLabel("Equb & Edir Community Management");
        lblTagline.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblTagline.setForeground(new Color(80, 80, 80));
        lblTagline.setAlignmentX(Component.LEFT_ALIGNMENT);

        // --- Input Controls ---
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

        // --- Action Button ---
        JButton btnLogin = new RoundedButton("LOGIN", new Color(34, 112, 43));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnLogin.setMaximumSize(new Dimension(320, 45));
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // --- Language Toggle Panel ---
        JPanel langPanel = new JPanel(new GridLayout(1, 2));
        langPanel.setOpaque(false);
        langPanel.setMaximumSize(new Dimension(160, 35));
        langPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnEng = new JButton("English");
        btnEng.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnEng.setFocusPainted(false);

        JButton btnAmh = new JButton("አማርኛ");
        btnAmh.setFont(new Font("Nyala", Font.PLAIN, 14)); // Nyala handles Amharic well on Windows
        btnAmh.setFocusPainted(false);

        langPanel.add(btnEng);
        langPanel.add(btnAmh);

        // --- Component Assembly ---
        formPanel.add(lblTitle);
        formPanel.add(lblSubTitle);
        formPanel.add(lblTagline);
        formPanel.add(Box.createVerticalStrut(35)); // Spacer
        formPanel.add(lblUser);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(txtUsername);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(lblPass);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(txtPassword);
        formPanel.add(Box.createVerticalStrut(25));
        formPanel.add(btnLogin);
        formPanel.add(Box.createVerticalStrut(30));
        formPanel.add(langPanel);

        // Layout Constraints: Shift layout window dynamically to the right side
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        // Insets: top, left, bottom, right. Pushes components rightward away from the coffee pot image
        gbc.insets = new Insets(60, 420, 40, 20);
        gbc.anchor = GridBagConstraints.WEST;

        mainBackgroundPanel.add(formPanel, gbc);
        add(mainBackgroundPanel);
    }

    public static void main(String[] args) {
        // Set system look and feel for cleaner base text fields
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new LoginTest().setVisible(true));
    }
}

// ==========================================
//   CUSTOM COMPONENT UI CORNER ROUNDING
// ==========================================

class RoundedTextField extends JTextField {
    public RoundedTextField(String placeholder, int columns) {
        super(columns);
        setText(placeholder);
        setForeground(Color.GRAY);
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    }
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
        super.paintComponent(g2);
        g2.dispose();
    }
    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(200, 200, 200));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
        g2.dispose();
    }
}

class RoundedPasswordField extends JPasswordField {
    public RoundedPasswordField(int columns) {
        super(columns);
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    }
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
        super.paintComponent(g2);
        g2.dispose();
    }
    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(200, 200, 200));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
        g2.dispose();
    }
}

class RoundedButton extends JButton {
    private final Color backgroundColor;
    public RoundedButton(String label, Color bg) {
        super(label);
        this.backgroundColor = bg;
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
    }
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(backgroundColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
        super.paintComponent(g2);
        g2.dispose();
    }
}