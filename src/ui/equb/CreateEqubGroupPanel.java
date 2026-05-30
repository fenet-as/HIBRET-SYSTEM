package ui.equb;

import service.EqubService;
import util.LanguageManager;
import util.FontManager; // ✅ Imported FontManager
import javax.swing.*;
import java.awt.*;

public class CreateEqubGroupPanel extends JPanel {
    private final JPanel containerPanel;
    private final EqubService service;
    private final JTextField txtName;
    private final JTextField txtAmount;
    private final int loggedInUserId;

    public CreateEqubGroupPanel(JPanel containerPanel, EqubService service, int loggedInUserId) {
        this.containerPanel = containerPanel;
        this.service = service;
        this.loggedInUserId = loggedInUserId;

        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(25, 35, 30, 35));

        // --- HEADER BAR ---
        JPanel headPanel = new JPanel(new BorderLayout());
        headPanel.setOpaque(false);

        // ✅ Localized Panel Header Title with dynamic typography metrics
        JLabel lblTitle = new JLabel(LanguageManager.getString("equb.create.title"));
        lblTitle.setFont(FontManager.getBoldFont(24));
        lblTitle.setForeground(new Color(101, 53, 15));
        headPanel.add(lblTitle, BorderLayout.WEST);

        // ✅ Localized Action Button Cancel Label with dynamic font configuration
        JButton btnCancel = new JButton(LanguageManager.getString("equb.create.btn_cancel"));
        btnCancel.setFont(FontManager.getBoldFont(13));
        btnCancel.addActionListener(e -> navigateBack());
        headPanel.add(btnCancel, BorderLayout.EAST);
        add(headPanel, BorderLayout.NORTH);

        // --- FORM CONTENT WRAPPER ---
        JPanel centralBody = new JPanel(new GridLayout(1, 2, 40, 0));
        centralBody.setOpaque(false);

        JPanel formFieldsPanel = new JPanel();
        formFieldsPanel.setOpaque(false);
        formFieldsPanel.setLayout(new BoxLayout(formFieldsPanel, BoxLayout.Y_AXIS));
        formFieldsPanel.add(Box.createVerticalStrut(30));

        // ✅ Localized Name Input Label Field
        formFieldsPanel.add(createFormLabel(LanguageManager.getString("equb.create.lbl_name")));
        txtName = new JTextField();
        txtName.setFont(FontManager.getPlainFont(14));
        txtName.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        formFieldsPanel.add(txtName);
        formFieldsPanel.add(Box.createVerticalStrut(20));

        // ✅ Localized Value Entry Form Label
        formFieldsPanel.add(createFormLabel(LanguageManager.getString("equb.create.lbl_amount")));
        txtAmount = new JTextField();
        txtAmount.setFont(FontManager.getPlainFont(14));
        txtAmount.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        formFieldsPanel.add(txtAmount);
        formFieldsPanel.add(Box.createVerticalStrut(35));

        // ✅ Localized Save Submission Button
        JButton btnSave = new JButton(LanguageManager.getString("equb.create.btn_save")) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(34, 100, 51));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnSave.setFont(FontManager.getBoldFont(14));
        btnSave.setForeground(Color.WHITE);
        btnSave.setContentAreaFilled(false);
        btnSave.setBorderPainted(false);
        btnSave.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnSave.addActionListener(e -> {
            // ✅ Map JOptionPane alert layout options to dynamic Ge'ez font properties
            UIManager.put("OptionPane.messageFont", FontManager.getPlainFont(14));
            UIManager.put("OptionPane.buttonFont", FontManager.getPlainFont(13));

            String poolName = txtName.getText().trim();
            String amountStr = txtAmount.getText().trim();

            if (poolName.isEmpty() || amountStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, LanguageManager.getString("equb.create.err.validation"), LanguageManager.getString("msg.validation_error"), JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                double parsedAmount = Double.parseDouble(amountStr);

                service.createEqubGroup(poolName, parsedAmount, this.loggedInUserId);

                JOptionPane.showMessageDialog(this, LanguageManager.getString("equb.create.success"), LanguageManager.getString("msg.success"), JOptionPane.INFORMATION_MESSAGE);
                navigateBack();
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, LanguageManager.getString("equb.create.err.parsing"), LanguageManager.getString("msg.parsing_error"), JOptionPane.ERROR_MESSAGE);
            }
        });

        formFieldsPanel.add(btnSave);
        centralBody.add(formFieldsPanel);

        // ✅ Localized Branding Dynamic Graphic Subpanel with safe dynamic graphics pipeline mapping
        JPanel decorationPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(242, 238, 228));
                g2.fillRoundRect(20, 30, getWidth() - 40, getHeight() - 60, 12, 12);
                g2.setColor(new Color(130, 90, 40));

                // Using standard Plain dynamic font fallback for custom text painting to avoid Ge'ez missing box artifacts
                g2.setFont(FontManager.getPlainFont(15));

                String systemBrandText = LanguageManager.getString("equb.create.system_brand");
                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(systemBrandText);
                int textHeight = fm.getAscent();
                g2.drawString(systemBrandText, (getWidth() - textWidth) / 2, (getHeight() + textHeight) / 2);
                g2.dispose();
            }
        };
        decorationPanel.setOpaque(false);
        centralBody.add(decorationPanel);

        add(centralBody, BorderLayout.CENTER);
    }

    private JLabel createFormLabel(String fieldText) {
        JLabel l = new JLabel(fieldText);
        l.setFont(FontManager.getBoldFont(13));
        l.setForeground(new Color(101, 53, 15));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private void navigateBack() {
        ((CardLayout) containerPanel.getLayout()).show(containerPanel, "EqubHome");
        for (Component c : containerPanel.getComponents()) {
            if (c instanceof EqubHomePanel) {
                ((EqubHomePanel) c).loadEqubGroupsData();
            }
        }
    }
}