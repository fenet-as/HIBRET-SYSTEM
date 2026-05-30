package ui.edir;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class AddMemberPanel extends JPanel {
    private final JPanel parentWrapper;

    // Rely strictly on structural Integer tracking primary keys
    private final int groupId;

    private final EdirGroupDetailPanel trackingDashboard;

    private JTextField txtFullName;
    private JTextField txtPhone;

    public AddMemberPanel(JPanel parentWrapper, int groupId, EdirGroupDetailPanel trackingDashboard) {
        this.parentWrapper = parentWrapper;
        this.groupId = groupId;
        this.trackingDashboard = trackingDashboard;

        // Use GridBagLayout on the master panel to cleanly center the inner Form card
        setLayout(new GridBagLayout());
        setBackground(new Color(253, 247, 237));
        setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        initFormComponents();
    }

    private void initFormComponents() {
        // Create an inner styled panel acting as a Card container for the form layout
        JPanel cardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Draw a subtle dropdown card background shadow
                g2.setColor(new Color(225, 215, 200));
                g2.fill(new RoundRectangle2D.Float(2, 2, getWidth() - 4, getHeight() - 4, 16, 16));
                // Base white background canvas
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 3, getHeight() - 3, 16, 16));
                g2.setColor(new Color(230, 220, 205));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 4, getHeight() - 4, 16, 16));
                g2.dispose();
            }
        };
        cardPanel.setLayout(new GridBagLayout());
        cardPanel.setBorder(BorderFactory.createEmptyBorder(35, 45, 35, 45));
        cardPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // 1. HEADER SECTION - Plain English with standard typography mapping
        JLabel lblTitle = new JLabel("Add New Member");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblTitle.setForeground(new Color(101, 31, 16));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(12, 12, 20, 12); // Clean structural spacing without subheader text
        cardPanel.add(lblTitle, gbc);

        // Reset baseline insets for form rows
        gbc.insets = new Insets(10, 12, 10, 12);

        // 2. FULL NAME FIELD LAYOUT - Plain English with standard typography mapping
        gbc.gridwidth = 1; gbc.gridy = 2; gbc.gridx = 0;
        JLabel lblName = new JLabel("Full Name");
        lblName.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblName.setForeground(new Color(70, 60, 50));
        cardPanel.add(lblName, gbc);

        txtFullName = new JTextField();
        txtFullName.setPreferredSize(new Dimension(360, 42));
        txtFullName.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtFullName.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 200, 185), 1, true),
                BorderFactory.createEmptyBorder(0, 12, 0, 12)
        ));
        gbc.gridx = 1;
        cardPanel.add(txtFullName, gbc);

        // 3. PHONE NUMBER FIELD LAYOUT - Plain English with standard typography mapping
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel lblPhone = new JLabel("Phone Number");
        lblPhone.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblPhone.setForeground(new Color(70, 60, 50));
        cardPanel.add(lblPhone, gbc);

        txtPhone = new JTextField();
        txtPhone.setPreferredSize(new Dimension(360, 42));
        txtPhone.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtPhone.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 200, 185), 1, true),
                BorderFactory.createEmptyBorder(0, 12, 0, 12)
        ));
        gbc.gridx = 1;
        cardPanel.add(txtPhone, gbc);

        // 4. ACTION BUTTON BUTTON CONTAINER LAYOUT
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        btnPanel.setOpaque(false);

        JButton btnCancel = new JButton("Cancel") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(240, 235, 225));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnCancel.setPreferredSize(new Dimension(110, 40));
        btnCancel.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnCancel.setForeground(new Color(101, 31, 16));
        btnCancel.setContentAreaFilled(false);
        btnCancel.setBorderPainted(false);
        btnCancel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JButton btnSubmit = new JButton("Add Member") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(46, 117, 89));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnSubmit.setPreferredSize(new Dimension(160, 40));
        btnSubmit.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setContentAreaFilled(false);
        btnSubmit.setBorderPainted(false);
        btnSubmit.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btnCancel.addActionListener(e -> returnToDashboardView());

        btnSubmit.addActionListener(e -> {
            UIManager.put("OptionPane.messageFont", new Font("SansSerif", Font.PLAIN, 14));
            UIManager.put("OptionPane.buttonFont", new Font("SansSerif", Font.PLAIN, 13));

            String name = txtFullName.getText().trim();
            String phone = txtPhone.getText().trim();

            if (name.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all required fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean success = trackingDashboard.getEdirService().addMemberToGroup(this.groupId, name, phone);
            if (success) {
                JOptionPane.showMessageDialog(this, "Member added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                returnToDashboardView();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save member details. Please check your database connection.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(btnCancel);
        btnPanel.add(btnSubmit);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.insets = new Insets(25, 12, 10, 12);
        cardPanel.add(btnPanel, gbc);

        // Add the styled form card cleanly right to the center anchor coordinates of the view panel
        GridBagConstraints centerConstraints = new GridBagConstraints();
        centerConstraints.gridx = 0;
        centerConstraints.gridy = 0;
        centerConstraints.weightx = 1.0;
        centerConstraints.weighty = 1.0;
        centerConstraints.anchor = GridBagConstraints.CENTER;

        add(cardPanel, centerConstraints);
    }

    private void returnToDashboardView() {
        trackingDashboard.refreshDashboardMetricsAndLedger();
        CardLayout cl = (CardLayout) parentWrapper.getLayout();
        cl.show(parentWrapper, "EdirDetail");
    }
}