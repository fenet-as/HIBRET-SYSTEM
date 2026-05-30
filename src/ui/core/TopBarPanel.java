package ui.core;

import model.User;
import util.LanguageManager;
import util.FontManager; // ✅ Imported FontManager
import javax.swing.*;
import java.awt.*;

public class TopBarPanel extends JPanel {
    private boolean isProgrammaticSelection = false;
    private final JLabel lblBrand;
    private final JComboBox<String> comboLang;

    public TopBarPanel(User user, MainFrame frame) {
        setBackground(Color.WHITE);
        setOpaque(true);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(1150, 65));
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(225, 220, 205)));

        JPanel leftContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 12));
        leftContainer.setOpaque(false);

        JButton btnMenu = new JButton("☰");
        btnMenu.setFont(new Font("SansSerif", Font.BOLD, 20)); // System symbols retain normal typography engines
        btnMenu.setForeground(new Color(60, 60, 60));
        btnMenu.setContentAreaFilled(false);
        btnMenu.setBorderPainted(false);
        btnMenu.setFocusPainted(false);
        btnMenu.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Connect the button to trigger our smooth animation loop
        btnMenu.addActionListener(e -> frame.toggleSidebar());

        // ✅ Fixed Brand Heading Font with dynamic Amharic Font mapping configuration
        lblBrand = new JLabel(LanguageManager.getString("topbar.brand"));
        lblBrand.setFont(FontManager.getBoldFont(20));
        lblBrand.setForeground(new Color(34, 112, 43));

        leftContainer.add(btnMenu);
        leftContainer.add(lblBrand);

        JPanel rightContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 12));
        rightContainer.setOpaque(false);

        String[] languages = {"English", "አማርኛ"};
        comboLang = new JComboBox<>(languages);

        // ✅ CRUCIAL FIX: Force both the dropdown box AND its expanded menu list elements to use the Amharic Font
        comboLang.setFont(FontManager.getPlainFont(13));
        Object renderer = comboLang.getRenderer();
        if (renderer instanceof JComponent) {
            ((JComponent) renderer).setFont(FontManager.getPlainFont(13));
        }

        comboLang.setPreferredSize(new Dimension(110, 30));
        comboLang.setFocusable(false);

        // Synchronize selected list position with active system profile locales
        syncLanguageSelection();

        // Dynamic Global Language Change Handler Engine
        comboLang.addActionListener(e -> {
            if (isProgrammaticSelection) return;

            int selectedIndex = comboLang.getSelectedIndex();
            if (selectedIndex == 0) {
                LanguageManager.setLanguage("en");
            } else if (selectedIndex == 1) {
                LanguageManager.setLanguage("am");
            }

            // Fire the global main frame layout refresher to update strings on-the-fly
            frame.reloadLanguageContext();
        });

        JPanel avatarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(101, 53, 15));
                g2.fillOval(0, 0, 32, 32);
                g2.setColor(new Color(210, 195, 175));
                g2.fillOval(2, 2, 28, 28);
                g2.dispose();
            }
        };
        avatarPanel.setPreferredSize(new Dimension(32, 32));
        avatarPanel.setOpaque(false);

        rightContainer.add(comboLang);
        rightContainer.add(avatarPanel);

        add(leftContainer, BorderLayout.WEST);
        add(rightContainer, BorderLayout.EAST);
    }

    /**
     * ✅ Internal helper to gracefully switch combobox layout markers without triggering change handler loops
     */
    private void syncLanguageSelection() {
        isProgrammaticSelection = true;
        if (LanguageManager.getCurrentLocale() != null && "am".equalsIgnoreCase(LanguageManager.getCurrentLocale().getLanguage())) {
            comboLang.setSelectedIndex(1);
        } else {
            comboLang.setSelectedIndex(0);
        }
        isProgrammaticSelection = false;
    }

    /**
     * ✅ Public access sweeper: Updates textual labels when languages switch mid-execution
     */
    public void rebuildTopBarText() {
        lblBrand.setText(LanguageManager.getString("topbar.brand"));
        lblBrand.setFont(FontManager.getBoldFont(20)); // Ensure sizing maps cleanly

        // Update font configurations on the dropdown list elements dynamically
        comboLang.setFont(FontManager.getPlainFont(13));

        syncLanguageSelection();

        this.revalidate();
        this.repaint();
    }
}