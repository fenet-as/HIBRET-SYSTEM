package ui.edir;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class AddMemberPanel extends JPanel {
    private final JPanel parentWrapper;
    private final int groupId;
    private final EdirGroupDetailPanel trackingDashboard;

    private JTextField txtFullName;
    private JTextField txtPhone;

    // Premium Consistent Color Palette
    private static final Color BG_GRADIENT_START = new Color(250, 248, 245);
    private static final Color BG_GRADIENT_END = new Color(240, 235, 225);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(74, 38, 10);
    private static final Color TEXT_SECONDARY = new Color(115, 105, 95);
    private static final Color FIELD_BORDER = new Color(210, 205, 195);
    private static final Color FIELD_FOCUS = new Color(140, 110, 80);

    private static final Color BTN_PRIMARY = new Color(46, 117, 59);
    private static final Color BTN_HOVER = new Color(36, 97, 47);
    private static final Color BTN_CANCEL = new Color(242, 238, 233);
    private static final Color BTN_CANCEL_HOVER = new Color(230, 224, 216);

    public AddMemberPanel(JPanel parentWrapper, int groupId, EdirGroupDetailPanel trackingDashboard) {
        this.parentWrapper = parentWrapper;
        this.groupId = groupId;
        this.trackingDashboard = trackingDashboard;

        setOpaque(false);
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        initFormComponents();
    }

    private void initFormComponents() {
        // --- CENTER CONTAINER (Form Card Panel) ---
        JPanel formCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Background shadow effect
                g2.setColor(new Color(0, 0, 0, 12));
                g2.fillRoundRect(2, 2, getWidth() - 2, getHeight() - 2, 16, 16);
                // Card background
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 16, 16);
                // Subtle card border outline
                g2.setColor(new Color(230, 225, 215));
                g2.drawRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 16, 16);
                g2.dispose();
            }
        };
        formCard.setLayout(new GridBagLayout());
        formCard.setBorder(BorderFactory.createEmptyBorder(35, 40, 40, 40));
        formCard.setOpaque(false);
        formCard.setPreferredSize(new Dimension(460, 410));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Title
        JLabel lblTitle = new JLabel("Add New Member");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblTitle.setForeground(TEXT_PRIMARY);
        gbc.gridx = 0; gbc.gridy = 0;
        formCard.add(lblTitle, gbc);

        // Subtitle text description
        JLabel lblSubtitle = new JLabel("Register a new member to this edir community.");
        lblSubtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblSubtitle.setForeground(TEXT_SECONDARY);
        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 15, 0);
        formCard.add(lblSubtitle, gbc);

        // Field 1: Full Name Label
        gbc.gridy = 2; gbc.insets = new Insets(8, 0, 2, 0);
        formCard.add(createFieldLabel("Full Name"), gbc);

        // Full Name Textbox
        txtFullName = createStyledTextField();
        gbc.gridy = 3; gbc.insets = new Insets(0, 0, 12, 0);
        formCard.add(txtFullName, gbc);

        // Field 2: Phone Number Label
        gbc.gridy = 4; gbc.insets = new Insets(4, 0, 2, 0);
        formCard.add(createFieldLabel("Phone Number"), gbc);

        // Phone Number Textbox
        txtPhone = createStyledTextField();
        gbc.gridy = 5; gbc.insets = new Insets(0, 0, 25, 0);
        formCard.add(txtPhone, gbc);

        // --- BUTTON ACTIONS CONTAINER ---
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 12, 0));
        btnPanel.setOpaque(false);

        // Styled Cancel Button
        JButton btnCancel = new JButton("Cancel") {
            private boolean isHovered = false;
            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { isHovered = true; repaint(); }
                    public void mouseExited(MouseEvent e) { isHovered = false; repaint(); }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isHovered ? BTN_CANCEL_HOVER : BTN_CANCEL);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnCancel.setPreferredSize(new Dimension(0, 44));
        btnCancel.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnCancel.setForeground(TEXT_PRIMARY);
        btnCancel.setContentAreaFilled(false);
        btnCancel.setBorderPainted(false);
        btnCancel.setFocusPainted(false);
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Styled Submit Button
        JButton btnSubmit = new JButton("Add Member") {
            private boolean isHovered = false;
            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { isHovered = true; repaint(); }
                    public void mouseExited(MouseEvent e) { isHovered = false; repaint(); }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isHovered ? BTN_HOVER : BTN_PRIMARY);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnSubmit.setPreferredSize(new Dimension(0, 44));
        btnSubmit.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setContentAreaFilled(false);
        btnSubmit.setBorderPainted(false);
        btnSubmit.setFocusPainted(false);
        btnSubmit.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnCancel.addActionListener(e -> returnToDashboardView());

        btnSubmit.addActionListener(e -> {
            UIManager.put("OptionPane.messageFont", new Font("SansSerif", Font.PLAIN, 14));
            UIManager.put("OptionPane.buttonFont", new Font("SansSerif", Font.PLAIN, 13));

            String name = txtFullName.getText().trim();
            String phone = txtPhone.getText().trim();

            if (name.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all required fields.", "Missing Fields", JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean success = trackingDashboard.getEdirService().addMemberToGroup(this.groupId, name, phone);
            if (success) {
                JOptionPane.showMessageDialog(this, "Member added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                returnToDashboardView();
            } else {
                JOptionPane.showMessageDialog(this, "Could not save member details. Please check connection.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(btnCancel);
        btnPanel.add(btnSubmit);

        gbc.gridy = 6; gbc.insets = new Insets(5, 0, 0, 0);
        formCard.add(btnPanel, gbc);

        // Core Layout Alignment Configuration
        GridBagConstraints centerConstraints = new GridBagConstraints();
        centerConstraints.gridx = 0;
        centerConstraints.gridy = 0;
        centerConstraints.weightx = 1.0;
        centerConstraints.weighty = 1.0;
        centerConstraints.anchor = GridBagConstraints.CENTER;

        add(formCard, centerConstraints);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint gradient = new GradientPaint(0, 0, BG_GRADIENT_START, 0, getHeight(), BG_GRADIENT_END);
        g2.setPaint(gradient);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
        super.paintComponent(g);
    }

    private void returnToDashboardView() {
        trackingDashboard.refreshDashboardMetricsAndLedger();
        CardLayout cl = (CardLayout) parentWrapper.getLayout();
        cl.show(parentWrapper, "EdirDetail");
    }

    private JLabel createFieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 13));
        lbl.setForeground(TEXT_PRIMARY);
        return lbl;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setForeground(new Color(50, 50, 50));
        field.setPreferredSize(new Dimension(0, 40));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(FIELD_BORDER, 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));

        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(FIELD_FOCUS, 1, true),
                        new EmptyBorder(8, 12, 8, 12)
                ));
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(FIELD_BORDER, 1, true),
                        new EmptyBorder(8, 12, 8, 12)
                ));
            }
        });

        return field;
    }
}