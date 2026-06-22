package ui.edir;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import service.EdirService;

public class CreateEdirGroupPanel extends JPanel {
    private final JPanel parentWrapper;
    private final EdirService edirService;
    private final int loggedInUserId;

    private JTextField txtGroupName;
    private JTextField txtMonthlyFee;
    private JTextArea txtRules;

    // Professional Color Palette
    private static final Color BG_GRADIENT_START = new Color(250, 248, 245);
    private static final Color BG_GRADIENT_END = new Color(240, 235, 225);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(74, 38, 10);
    private static final Color TEXT_SECONDARY = new Color(115, 105, 95);
    private static final Color FIELD_BORDER = new Color(210, 205, 195);
    private static final Color FIELD_FOCUS = new Color(140, 110, 80);

    private static final Color BTN_PRIMARY = new Color(46, 117, 59);
    private static final Color BTN_HOVER = new Color(36, 97, 47);

    public CreateEdirGroupPanel(JPanel parentWrapper, EdirService edirService, int loggedInUserId) {
        this.parentWrapper = parentWrapper;
        this.edirService = edirService;
        this.loggedInUserId = loggedInUserId;

        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 40, 40, 40));

        initMainForm();
    }

    private void initMainForm() {
        // --- TOP NAVIGATION BAR ---
        JPanel headPanel = new JPanel(new BorderLayout());
        headPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Create New Edir");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 26));
        lblTitle.setForeground(TEXT_PRIMARY);
        headPanel.add(lblTitle, BorderLayout.WEST);

        JButton btnBack = new JButton("← Back to Home");
        btnBack.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnBack.setForeground(TEXT_SECONDARY);
        btnBack.setContentAreaFilled(false);
        btnBack.setBorderPainted(false);
        btnBack.setFocusPainted(false);
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.addActionListener(e -> {
            CardLayout innerLayout = (CardLayout) parentWrapper.getLayout();
            innerLayout.show(parentWrapper, "EdirHome");
        });

        btnBack.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btnBack.setForeground(TEXT_PRIMARY); }
            public void mouseExited(MouseEvent e) { btnBack.setForeground(TEXT_SECONDARY); }
        });
        headPanel.add(btnBack, BorderLayout.EAST);
        add(headPanel, BorderLayout.NORTH);

        // --- CENTER CONTAINER (Form Card) ---
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);

        JPanel formCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 15));
                g2.fillRoundRect(2, 2, getWidth() - 2, getHeight() - 2, 16, 16);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 16, 16);
                g2.setColor(new Color(230, 225, 215));
                g2.drawRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 16, 16);
                g2.dispose();
            }
        };
        formCard.setOpaque(false);
        formCard.setLayout(new GridBagLayout());
        formCard.setBorder(BorderFactory.createEmptyBorder(35, 40, 40, 40));
        formCard.setPreferredSize(new Dimension(540, 440));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Subtitle
        JLabel lblSubtitle = new JLabel("Set up your new community edir group below.");
        lblSubtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblSubtitle.setForeground(TEXT_SECONDARY);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formCard.add(lblSubtitle, gbc);

        // Field 1: Edir Name
        gbc.gridy = 1; gbc.insets = new Insets(15, 0, 2, 0);
        formCard.add(createFieldLabel("Edir Name"), gbc);

        txtGroupName = createStyledTextField();
        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 12, 0);
        formCard.add(txtGroupName, gbc);

        // Field 2: Monthly Fee
        gbc.gridy = 3; gbc.insets = new Insets(4, 0, 2, 0);
        formCard.add(createFieldLabel("Monthly Fee (ETB)"), gbc);

        txtMonthlyFee = createStyledTextField();
        txtMonthlyFee.setText("200");
        gbc.gridy = 4; gbc.insets = new Insets(0, 0, 12, 0);
        formCard.add(txtMonthlyFee, gbc);

        // Field 3: Rules and Bylaws
        gbc.gridy = 5; gbc.insets = new Insets(4, 0, 2, 0);
        formCard.add(createFieldLabel("Rules and Regulations"), gbc);

        txtRules = new JTextArea("1. Members must pay contributions on time.\n2. Meetings are held monthly.", 4, 20);
        txtRules.setLineWrap(true);
        txtRules.setWrapStyleWord(true);
        txtRules.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtRules.setForeground(new Color(50, 50, 50));
        txtRules.setBorder(new EmptyBorder(8, 12, 8, 12));

        JScrollPane rulesScroll = new JScrollPane(txtRules);
        rulesScroll.setBorder(BorderFactory.createLineBorder(FIELD_BORDER, 1, true));
        rulesScroll.setPreferredSize(new Dimension(0, 90));

        txtRules.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                rulesScroll.setBorder(BorderFactory.createLineBorder(FIELD_FOCUS, 1, true));
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                rulesScroll.setBorder(BorderFactory.createLineBorder(FIELD_BORDER, 1, true));
            }
        });

        gbc.gridy = 6; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1.0; gbc.insets = new Insets(0, 0, 25, 0);
        formCard.add(rulesScroll, gbc);

        // Submit Button
        JButton btnSubmit = new JButton("Create Edir") {
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
        btnSubmit.setFont(new Font("SansSerif", Font.BOLD, 15));
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setContentAreaFilled(false);
        btnSubmit.setBorderPainted(false);
        btnSubmit.setFocusPainted(false);
        btnSubmit.setPreferredSize(new Dimension(0, 46));
        btnSubmit.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnSubmit.addActionListener(e -> {
            UIManager.put("OptionPane.messageFont", new Font("SansSerif", Font.PLAIN, 14));
            UIManager.put("OptionPane.buttonFont", new Font("SansSerif", Font.PLAIN, 13));

            String groupName = txtGroupName.getText().trim();
            String feeStr = txtMonthlyFee.getText().trim().replace(",", "");
            String rules = txtRules.getText().trim();

            if (groupName.isEmpty() || feeStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Missing Information", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                double fee = Double.parseDouble(feeStr);
                double defaultInitialPool = 0.0; // Automatically handles removed initial capital option safely

                boolean success = edirService.createGroup(groupName, fee, defaultInitialPool, rules, this.loggedInUserId);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Edir created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

                    txtGroupName.setText("");
                    txtMonthlyFee.setText("200");
                    txtRules.setText("1. Members must pay contributions on time.\n2. Meetings are held monthly.");

                    for (Component viewComponent : parentWrapper.getComponents()) {
                        if (viewComponent instanceof EdirHomePanel) {
                            ((EdirHomePanel) viewComponent).loadGroups();
                        }
                    }

                    CardLayout innerLayout = (CardLayout) parentWrapper.getLayout();
                    innerLayout.show(parentWrapper, "EdirHome");
                } else {
                    JOptionPane.showMessageDialog(this, "Could not create the group.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number for the fee.", "Invalid Amount", JOptionPane.ERROR_MESSAGE);
            }
        });

        gbc.gridy = 7; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weighty = 0.0; gbc.insets = new Insets(0, 0, 0, 0);
        formCard.add(btnSubmit, gbc);

        GridBagConstraints wrapperGbc = new GridBagConstraints();
        wrapperGbc.gridx = 0; wrapperGbc.gridy = 0;
        wrapperGbc.weightx = 1.0; wrapperGbc.weighty = 1.0;
        wrapperGbc.anchor = GridBagConstraints.CENTER;
        centerWrapper.add(formCard, wrapperGbc);

        add(centerWrapper, BorderLayout.CENTER);
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