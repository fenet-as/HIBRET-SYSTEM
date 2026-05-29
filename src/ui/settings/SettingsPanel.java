package ui.settings;

import dao.impl.UserDAOImpl;
import model.User;
import dao.UserDAO;
//import UserDAOImpl;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class SettingsPanel extends JPanel {
    private final JPanel parentWrapper;
    private final User currentUser;
    private final UserDAO userDAO; // Points directly to interface template structure

    // UI Layout Components
    private JPanel contentCardsContainer;
    private CardLayout contentCardLayout;

    // Sidebar Navigation Buttons
    private JButton btnLanguageNav;
    private JButton btnPasswordNav;

    // Configuration Fields
    private JRadioButton rbEnglish;
    private JRadioButton rbAmharic;

    public SettingsPanel(JPanel parentWrapper, User currentUser) {
        this.parentWrapper = parentWrapper;
        this.currentUser = currentUser;
        this.userDAO = new UserDAOImpl();

        setLayout(new BorderLayout(30, 0));
        setBackground(new Color(253, 247, 237));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        initHeader();
        initSidebarNavigation();
        initSettingsContentCards();

        // Default View
        showCard("LanguageCard", btnLanguageNav);
    }

    private void initHeader() {
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        headerPanel.setOpaque(false);
        headerPanel.setPreferredSize(new Dimension(Short.MAX_VALUE, 50));

        JLabel lblIcon = new JLabel("⚙");
        lblIcon.setFont(new Font("SansSerif", Font.PLAIN, 28));
        lblIcon.setForeground(new Color(101, 31, 16));

        JLabel lblTitle = new JLabel("Settings");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 26));
        lblTitle.setForeground(new Color(101, 31, 16));

        headerPanel.add(lblIcon);
        headerPanel.add(lblTitle);
        add(headerPanel, BorderLayout.NORTH);
    }

    private void initSidebarNavigation() {
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setOpaque(false);
        sidebarPanel.setPreferredSize(new Dimension(220, Short.MAX_VALUE));

        btnLanguageNav = createSidebarButton("Language", "🌐");
        btnPasswordNav = createSidebarButton("Change Password", "🔒");

        btnLanguageNav.addActionListener(e -> showCard("LanguageCard", btnLanguageNav));
        btnPasswordNav.addActionListener(e -> showCard("PasswordCard", btnPasswordNav));

        sidebarPanel.add(btnLanguageNav);
        sidebarPanel.add(Box.createVerticalStrut(10));
        sidebarPanel.add(btnPasswordNav);

        add(sidebarPanel, BorderLayout.WEST);
    }

    private void initSettingsContentCards() {
        contentCardLayout = new CardLayout();
        contentCardsContainer = new JPanel(contentCardLayout);
        contentCardsContainer.setOpaque(false);

        // --- Card 1: Language Form Pane ---
        JPanel languageCard = createFormCard("Language");
        JPanel langBody = new JPanel();
        langBody.setLayout(new BoxLayout(langBody, BoxLayout.Y_AXIS));
        langBody.setOpaque(false);
        langBody.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblSelect = new JLabel("Select Language");
        lblSelect.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblSelect.setForeground(Color.GRAY);
        lblSelect.setAlignmentX(Component.LEFT_ALIGNMENT);

        rbEnglish = new JRadioButton("English", true);
        rbEnglish.setFont(new Font("SansSerif", Font.BOLD, 15));
        rbEnglish.setForeground(new Color(101, 31, 16));
        rbEnglish.setOpaque(false);
        rbEnglish.setAlignmentX(Component.LEFT_ALIGNMENT);

        rbAmharic = new JRadioButton("አማርኛ (Amharic)", false);
        rbAmharic.setFont(new Font("SansSerif", Font.BOLD, 15));
        rbAmharic.setForeground(new Color(101, 31, 16));
        rbAmharic.setOpaque(false);
        rbAmharic.setAlignmentX(Component.LEFT_ALIGNMENT);

        ButtonGroup languageGroup = new ButtonGroup();
        languageGroup.add(rbEnglish);
        languageGroup.add(rbAmharic);

        JButton btnApplyLang = createPrimaryButton("Apply");
        btnApplyLang.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnApplyLang.addActionListener(e -> {
            String selected = rbEnglish.isSelected() ? "English" : "Amharic";
            JOptionPane.showMessageDialog(this, "Language preference updated to: " + selected, "Success", JOptionPane.INFORMATION_MESSAGE);
        });

        langBody.add(lblSelect);
        langBody.add(Box.createVerticalStrut(20));
        langBody.add(rbEnglish);
        langBody.add(Box.createVerticalStrut(15));
        langBody.add(rbAmharic);
        langBody.add(Box.createVerticalStrut(30));
        langBody.add(btnApplyLang);
        languageCard.add(langBody, BorderLayout.CENTER);

        // --- Card 2: Password Form Pane ---
        JPanel passwordCard = createFormCard("Change Password");
        JPanel passBody = new JPanel(new GridBagLayout());
        passBody.setOpaque(false);
        passBody.setBorder(BorderFactory.createEmptyBorder(25, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblOld = new JLabel("Current Password:");
        lblOld.setFont(new Font("SansSerif", Font.BOLD, 13));
        JPasswordField txtOld = new JPasswordField(20);

        JLabel lblNew = new JLabel("New Password:");
        lblNew.setFont(new Font("SansSerif", Font.BOLD, 13));
        JPasswordField txtNew = new JPasswordField(20);

        JLabel lblConfirm = new JLabel("Confirm Password:");
        lblConfirm.setFont(new Font("SansSerif", Font.BOLD, 13));
        JPasswordField txtConfirm = new JPasswordField(20);

        JButton btnSavePassword = createPrimaryButton("Update Password");

        // ✅ USES EXISTING LOGIN METRICS METHOD FOR OLD PASSWORD CONFIRMATION
        btnSavePassword.addActionListener(e -> {
            String currentPass = new String(txtOld.getPassword()).trim();
            String newPass = new String(txtNew.getPassword()).trim();
            String confirmPass = new String(txtConfirm.getPassword()).trim();

            if (currentPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All security parameters must be filled out.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!newPass.equals(confirmPass)) {
                JOptionPane.showMessageDialog(this, "New Password fields do not match.", "Input Mismatch", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (currentUser == null || currentUser.getUsername() == null) {
                JOptionPane.showMessageDialog(this, "Session error: Active authenticated profile context missing.", "System Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 1. Authenticate using your existing login method signature
            User verifiedUser = userDAO.login(currentUser.getUsername(), currentPass);

            if (verifiedUser != null) {
                // 2. Clear to call the existing void signature method safely
                userDAO.updatePassword(currentUser.getUsername(), newPass);

                JOptionPane.showMessageDialog(this, "Security profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                txtOld.setText("");
                txtNew.setText("");
                txtConfirm.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update security profile.\nPlease check that your current password is correct.", "Authentication Denied", JOptionPane.ERROR_MESSAGE);
            }
        });

        gbc.gridx = 0; gbc.gridy = 0; passBody.add(lblOld, gbc);
        gbc.gridx = 1; gbc.gridy = 0; passBody.add(txtOld, gbc);

        gbc.gridx = 0; gbc.gridy = 1; passBody.add(lblNew, gbc);
        gbc.gridx = 1; gbc.gridy = 1; passBody.add(txtNew, gbc);

        gbc.gridx = 0; gbc.gridy = 2; passBody.add(lblConfirm, gbc);
        gbc.gridx = 1; gbc.gridy = 2; passBody.add(txtConfirm, gbc);

        gbc.gridx = 1; gbc.gridy = 3; gbc.insets = new Insets(20, 0, 0, 15); passBody.add(btnSavePassword, gbc);
        passwordCard.add(passBody, BorderLayout.NORTH);

        contentCardsContainer.add(languageCard, "LanguageCard");
        contentCardsContainer.add(passwordCard, "PasswordCard");

        add(contentCardsContainer, BorderLayout.CENTER);
    }

    private void showCard(String cardKey, JButton activeNavButton) {
        contentCardLayout.show(contentCardsContainer, cardKey);

        btnLanguageNav.setBackground(Color.WHITE);
        btnLanguageNav.setForeground(new Color(101, 31, 16));
        btnPasswordNav.setBackground(Color.WHITE);
        btnPasswordNav.setForeground(new Color(101, 31, 16));

        activeNavButton.setBackground(new Color(246, 235, 220));
        activeNavButton.setForeground(new Color(101, 31, 16));
    }

    private JPanel createFormCard(String title) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.setColor(new Color(235, 225, 210));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 12, 12));
                g2.dispose();
            }
        };
        card.setOpaque(false);

        JPanel cardHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 12)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(249, 245, 238));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.fillRect(0, getHeight() - 15, getWidth(), 15);
                g2.setColor(new Color(235, 225, 210));
                g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
                g2.dispose();
            }
        };
        cardHeader.setOpaque(false);
        cardHeader.setPreferredSize(new Dimension(Short.MAX_VALUE, 46));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblTitle.setForeground(new Color(34, 84, 54));
        cardHeader.add(lblTitle);

        card.add(cardHeader, BorderLayout.NORTH);
        return card;
    }

    private JButton createSidebarButton(String text, String icon) {
        JButton btn = new JButton("  " + icon + "   " + text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setColor(new Color(235, 225, 210));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setMaximumSize(new Dimension(210, 42));
        btn.setPreferredSize(new Dimension(210, 42));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(51, 98, 46));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(150, 38));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}