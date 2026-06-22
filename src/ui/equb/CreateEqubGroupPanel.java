package ui.equb;

import service.EqubService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CreateEqubGroupPanel extends JPanel {
    private final JPanel containerPanel;
    private final EqubService service;
    private final JTextField txtName;
    private final JTextField txtAmount;
    private final int loggedInUserId;

    // Premium Color Palette
    private static final Color BG_GRADIENT_START = new Color(250, 248, 245);
    private static final Color BG_GRADIENT_END = new Color(240, 235, 225);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(74, 38, 10);
    private static final Color TEXT_SECONDARY = new Color(115, 105, 95);
    private static final Color FIELD_BORDER = new Color(210, 205, 195);
    private static final Color FIELD_FOCUS = new Color(140, 110, 80);

    private static final Color BTN_PRIMARY = new Color(46, 117, 59);
    private static final Color BTN_HOVER = new Color(36, 97, 47);

    public CreateEqubGroupPanel(JPanel containerPanel, EqubService service, int loggedInUserId) {
        this.containerPanel = containerPanel;
        this.service = service;
        this.loggedInUserId = loggedInUserId;

        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 40, 40, 40));

        // --- TOP NAVIGATION BAR ---
        JPanel headPanel = new JPanel(new BorderLayout());
        headPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Create New Equb");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 26));
        lblTitle.setForeground(TEXT_PRIMARY);
        headPanel.add(lblTitle, BorderLayout.WEST);

        // Styled Borderless Cancel Button
        JButton btnCancel = new JButton("← Back to Home");
        btnCancel.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnCancel.setForeground(TEXT_SECONDARY);
        btnCancel.setContentAreaFilled(false);
        btnCancel.setBorderPainted(false);
        btnCancel.setFocusPainted(false);
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancel.addActionListener(e -> navigateBack());

        // Simple hover effect for back button
        btnCancel.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btnCancel.setForeground(TEXT_PRIMARY); }
            public void mouseExited(MouseEvent e) { btnCancel.setForeground(TEXT_SECONDARY); }
        });
        headPanel.add(btnCancel, BorderLayout.EAST);
        add(headPanel, BorderLayout.NORTH);

        // --- CENTER CONTAINER (Form Card) ---
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);

        // The Main Input Card
        JPanel formCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Subtle card shadow drop simulation
                g2.setColor(new Color(0, 0, 0, 15));
                g2.fillRoundRect(2, 2, getWidth() - 2, getHeight() - 2, 16, 16);
                // Crisp white background
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 16, 16);
                g2.setColor(new Color(230, 225, 215));
                g2.drawRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 16, 16);
                g2.dispose();
            }
        };
        formCard.setOpaque(false);
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBorder(BorderFactory.createEmptyBorder(35, 40, 40, 40));
        formCard.setPreferredSize(new Dimension(520, 360));

        // Subtitle inside card
        JLabel lblSubtitle = new JLabel("Set up your new rotational savings group below.");
        lblSubtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblSubtitle.setForeground(TEXT_SECONDARY);
        lblSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.add(lblSubtitle);
        formCard.add(Box.createVerticalStrut(30));

        // Field 1: Equb Name
        formCard.add(createFormLabel("Equb Name"));
        formCard.add(Box.createVerticalStrut(6));
        txtName = createStyledTextField("e.g. Weekly Family Equb");
        formCard.add(txtName);
        formCard.add(Box.createVerticalStrut(22));

        // Field 2: Payment Amount
        formCard.add(createFormLabel("Payment Amount per Round (ETB)"));
        formCard.add(Box.createVerticalStrut(6));
        txtAmount = createStyledTextField("e.g. 1000");
        formCard.add(txtAmount);
        formCard.add(Box.createVerticalStrut(35));

        // Primary Interactive Action Button
        JButton btnSave = new JButton("Create Equb") {
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
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnSave.setFont(new Font("SansSerif", Font.BOLD, 15));
        btnSave.setForeground(Color.WHITE);
        btnSave.setContentAreaFilled(false);
        btnSave.setBorderPainted(false);
        btnSave.setFocusPainted(false);
        btnSave.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnSave.addActionListener(e -> {
            UIManager.put("OptionPane.messageFont", new Font("SansSerif", Font.PLAIN, 14));
            UIManager.put("OptionPane.buttonFont", new Font("SansSerif", Font.PLAIN, 13));

            String poolName = txtName.getText().trim();
            String amountStr = txtAmount.getText().trim();

            if (poolName.isEmpty() || amountStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Missing Information", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                double parsedAmount = Double.parseDouble(amountStr);
                service.createEqubGroup(poolName, parsedAmount, this.loggedInUserId);
                JOptionPane.showMessageDialog(this, "Equb created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                navigateBack();
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number for the amount.", "Invalid Amount", JOptionPane.ERROR_MESSAGE);
            }
        });

        formCard.add(btnSave);

        // Add card to center container using constraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        centerWrapper.add(formCard, gbc);

        add(centerWrapper, BorderLayout.CENTER);
    }

    // Modern Gradient Canvas Background
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

    private JLabel createFormLabel(String fieldText) {
        JLabel l = new JLabel(fieldText);
        l.setFont(new Font("SansSerif", Font.BOLD, 13));
        l.setForeground(TEXT_PRIMARY);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    // Creates input text boxes with smooth interactive focuses and padding
    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setForeground(new Color(50, 50, 50));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Internal structural margin layout padding
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(FIELD_BORDER, 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));

        // Focus changes border colors dynamically
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

    private void navigateBack() {
        ((CardLayout) containerPanel.getLayout()).show(containerPanel, "EqubHome");
        for (Component c : containerPanel.getComponents()) {
            if (c instanceof EqubHomePanel) {
                ((EqubHomePanel) c).loadEqubGroupsData();
            }
        }
    }
}