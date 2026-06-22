package ui.edir;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import service.EdirService;

public class EdirContributionPanel extends JPanel {
    private final JPanel parentWrapper;
    private final EdirService edirService;

    private final int groupId;
    private String groupDisplayName = "Loading...";

    private JLabel lblTitle;
    private JComboBox<String> comboMember;
    private JTextField txtAmount;

    // Matching Professional Color Palette
    private static final Color BG_GRADIENT_START = new Color(250, 248, 245);
    private static final Color BG_GRADIENT_END = new Color(240, 235, 225);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(74, 38, 10);
    private static final Color TEXT_SECONDARY = new Color(115, 105, 95);
    private static final Color FIELD_BORDER = new Color(210, 205, 195);
    private static final Color FIELD_FOCUS = new Color(140, 110, 80);

    private static final Color BTN_PRIMARY = new Color(46, 117, 59);
    private static final Color BTN_HOVER = new Color(36, 97, 47);

    public EdirContributionPanel(JPanel parentWrapper, EdirService edirService, int groupId) {
        this.parentWrapper = parentWrapper;
        this.edirService = edirService;
        this.groupId = groupId;

        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 40, 40, 40));

        initMainForm();
        loadMembersCombo();
    }

    private void initMainForm() {
        // --- TOP NAVIGATION BAR ---
        JPanel headPanel = new JPanel(new BorderLayout());
        headPanel.setOpaque(false);

        lblTitle = new JLabel("Collect Fees - " + groupDisplayName);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 26));
        lblTitle.setForeground(TEXT_PRIMARY);
        headPanel.add(lblTitle, BorderLayout.WEST);

        JButton btnBack = new JButton("← Cancel");
        btnBack.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnBack.setForeground(TEXT_SECONDARY);
        btnBack.setContentAreaFilled(false);
        btnBack.setBorderPainted(false);
        btnBack.setFocusPainted(false);
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.addActionListener(e -> {
            CardLayout innerLayout = (CardLayout) parentWrapper.getLayout();
            innerLayout.show(parentWrapper, "EdirDetail");
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
        formCard.setPreferredSize(new Dimension(500, 320));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Subtitle
        JLabel lblSubtitle = new JLabel("Log a contribution payment for a member below.");
        lblSubtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblSubtitle.setForeground(TEXT_SECONDARY);
        gbc.gridx = 0; gbc.gridy = 0;
        formCard.add(lblSubtitle, gbc);

        // Field 1: Select Member Label
        gbc.gridy = 1; gbc.insets = new Insets(15, 0, 2, 0);
        formCard.add(createFieldLabel("Select Member"), gbc);

        // Styled ComboBox
        comboMember = new JComboBox<>();
        comboMember.setFont(new Font("SansSerif", Font.PLAIN, 14));
        comboMember.setBackground(Color.WHITE);
        comboMember.setPreferredSize(new Dimension(0, 40));
        comboMember.setBorder(BorderFactory.createLineBorder(FIELD_BORDER, 1, true));
        comboMember.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setBorder(new EmptyBorder(5, 10, 5, 10));
                return label;
            }
        });
        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 12, 0);
        formCard.add(comboMember, gbc);

        // Field 2: Amount Label
        gbc.gridy = 3; gbc.insets = new Insets(4, 0, 2, 0);
        formCard.add(createFieldLabel("Amount (ETB)"), gbc);

        // Amount Input field
        txtAmount = new JTextField("200");
        txtAmount.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtAmount.setForeground(new Color(50, 50, 50));
        txtAmount.setPreferredSize(new Dimension(0, 40));
        txtAmount.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(FIELD_BORDER, 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));

        txtAmount.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtAmount.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(FIELD_FOCUS, 1, true),
                        new EmptyBorder(8, 12, 8, 12)
                ));
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtAmount.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(FIELD_BORDER, 1, true),
                        new EmptyBorder(8, 12, 8, 12)
                ));
            }
        });
        gbc.gridy = 4; gbc.insets = new Insets(0, 0, 30, 0);
        formCard.add(txtAmount, gbc);

        // Submit Button Setup
        JButton btnSubmit = new JButton("Save Payment") {
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

            if (comboMember.getSelectedItem() == null || txtAmount.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please choose a member and enter an amount.", "Missing Fields", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                String member = comboMember.getSelectedItem().toString();
                double amt = Double.parseDouble(txtAmount.getText().trim());

                // Creates background tracking timestamp automatically without a clunky input textbox
                String autoGeneratedReceipt = "REG-" + System.currentTimeMillis();
                String systemMonth = LocalDate.now().getMonth().toString();

                boolean ok = edirService.recordContribution(this.groupId, member, systemMonth, amt, autoGeneratedReceipt);
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Payment saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    for (Component comp : parentWrapper.getComponents()) {
                        if (comp instanceof EdirGroupDetailPanel) {
                            ((EdirGroupDetailPanel) comp).refreshDashboardMetricsAndLedger();
                        }
                    }
                    CardLayout cl = (CardLayout) parentWrapper.getLayout();
                    cl.show(parentWrapper, "EdirDetail");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to record payment.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number for the amount.", "Invalid Amount", JOptionPane.ERROR_MESSAGE);
            }
        });

        gbc.gridy = 5; gbc.insets = new Insets(0, 0, 0, 0);
        formCard.add(btnSubmit, gbc);

        // Center card configuration
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

    private void loadMembersCombo() {
        Map<String, String> details = edirService.getGroupDetails(this.groupId);
        if (details != null && !details.isEmpty()) {
            this.groupDisplayName = details.getOrDefault("name", "Edir Group");
            lblTitle.setText("Collect Fees - " + this.groupDisplayName);
        }

        comboMember.removeAllItems();

        List<Map<String, String>> list = edirService.getMembersByGroup(this.groupId);
        for (Map<String, String> m : list) {
            comboMember.addItem(m.get("full_name"));
        }
    }

    private JLabel createFieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 13));
        lbl.setForeground(TEXT_PRIMARY);
        return lbl;
    }
}