package ui.equb;

import service.EqubService;
import model.Group;
import model.Member;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class EqubMembersPanel extends JPanel {
    private final JPanel containerPanel;
    private final EqubService service;
    private final Group groupCtx;

    // Left form components (Existing)
    private final JComboBox<Member> comboMembers;

    // Right form components (New Member Registration Form - Email Removed)
    private final JTextField txtNewFullName;
    private final JTextField txtNewPhone;

    public EqubMembersPanel(JPanel containerPanel, EqubService service, Group groupCtx) {
        this.containerPanel = containerPanel;
        this.service = service;
        this.groupCtx = groupCtx;

        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(25, 35, 30, 35));

        // ==========================================
        // 1. TOP ROW HEADER
        // ==========================================
        JPanel headPanel = new JPanel(new BorderLayout());
        headPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("👤 Manage Pool Members: " + groupCtx.getName());
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitle.setForeground(new Color(101, 53, 15));
        headPanel.add(lblTitle, BorderLayout.WEST);

        JButton btnCancel = new JButton("Cancel");
        btnCancel.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnCancel.addActionListener(e -> navigateBackToDetails());
        headPanel.add(btnCancel, BorderLayout.EAST);
        add(headPanel, BorderLayout.NORTH);

        // ==========================================
        // 2. TWO-COLUMN SPLIT WORKSPACE
        // ==========================================
        JPanel splitBodyPanel = new JPanel(new GridLayout(1, 2, 40, 0));
        splitBodyPanel.setOpaque(false);
        splitBodyPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // ------------------------------------------
        // COLUMN A: ASSIGN EXISTING MEMBER
        // ------------------------------------------
        JPanel leftCard = createStyledFormCard();
        leftCard.setLayout(new BoxLayout(leftCard, BoxLayout.Y_AXIS));

        JLabel lblLeftTitle = new JLabel("🔗 Add Existing System Member");
        lblLeftTitle.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblLeftTitle.setForeground(new Color(101, 53, 15));
        leftCard.add(lblLeftTitle);
        leftCard.add(Box.createVerticalStrut(20));

        leftCard.add(createFormLabel("Select Registered Member:"));
        comboMembers = new JComboBox<>();
        comboMembers.setPreferredSize(new Dimension(Integer.MAX_VALUE, 38));
        comboMembers.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        setupExistingMembersDropdown();
        leftCard.add(comboMembers);
        leftCard.add(Box.createVerticalStrut(30));

        JButton btnAssignExisting = createStyledButton("Confirm Member Assignment", new Color(40, 96, 144));
        btnAssignExisting.addActionListener(e -> {
            Member selectedMember = (Member) comboMembers.getSelectedItem();
            if (selectedMember == null) {
                JOptionPane.showMessageDialog(this, "No system members selected or available.", "Selection Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            service.addMemberToGroup(groupCtx.getId(), selectedMember.getId());
            JOptionPane.showMessageDialog(this, selectedMember.getFullName() + " linked to group successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            navigateBackToDetails();
        });
        leftCard.add(btnAssignExisting);
        splitBodyPanel.add(leftCard);

        // ------------------------------------------
        // COLUMN B: REGISTER & ENROLL NEW MEMBER FORM
        // ------------------------------------------
        JPanel rightCard = createStyledFormCard();
        rightCard.setLayout(new BoxLayout(rightCard, BoxLayout.Y_AXIS));

        JLabel lblRightTitle = new JLabel("✨ Create & Enroll Brand-New Member");
        lblRightTitle.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblRightTitle.setForeground(new Color(34, 112, 43));
        rightCard.add(lblRightTitle);
        rightCard.add(Box.createVerticalStrut(20));

        rightCard.add(createFormLabel("Full Name:"));
        txtNewFullName = createStyledTextField("e.g. Almaz Abebe");
        rightCard.add(txtNewFullName);
        rightCard.add(Box.createVerticalStrut(15));

        rightCard.add(createFormLabel("Phone Number:"));
        txtNewPhone = createStyledTextField("e.g. 0911223344");
        rightCard.add(txtNewPhone);
        rightCard.add(Box.createVerticalStrut(35));

        JButton btnRegisterNew = createStyledButton("Register & Enroll Member", new Color(34, 112, 43));
        btnRegisterNew.addActionListener(e -> {
            String fullName = txtNewFullName.getText().trim();
            String phone = txtNewPhone.getText().trim();

            if (fullName.isEmpty() || fullName.equals("e.g. Almaz Abebe") ||
                    phone.isEmpty() || phone.equals("e.g. 0911223344")) {
                JOptionPane.showMessageDialog(this, "Full Name and Phone Number are required fields.", "Validation Failed", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Member newMemberProfile = new Member();
            newMemberProfile.setFullName(fullName);
            newMemberProfile.setPhone(phone);
            newMemberProfile.setStatus("Active");

            // 1. Save master profile and extract the real database sequence ID key
            int generatedMemberId = service.createNewSystemMember(newMemberProfile);

            if (generatedMemberId > 0) {
                // 2. Safely link this valid ID reference to your group mapping ledger
                service.addMemberToGroup(groupCtx.getId(), generatedMemberId);

                JOptionPane.showMessageDialog(this, fullName + " successfully registered and enrolled!", "Member Created", JOptionPane.INFORMATION_MESSAGE);
                navigateBackToDetails();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save the new member profile to the database.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        rightCard.add(btnRegisterNew);
        splitBodyPanel.add(rightCard);

        add(splitBodyPanel, BorderLayout.CENTER);
    }

    // ==========================================
    // HELPER & UTILITY METHODS
    // ==========================================
    private void setupExistingMembersDropdown() {
        try {
            List<Member> activeSystemMembers = service.getAllSystemMembers();
            List<Member> existingGroupMembers = service.getMembersInGroup(groupCtx.getId());

            for (Member sm : activeSystemMembers) {
                boolean isAlreadyInGroup = false;
                for (Member em : existingGroupMembers) {
                    if (em.getId() == sm.getId()) {
                        isAlreadyInGroup = true;
                        break;
                    }
                }
                if (!isAlreadyInGroup) {
                    comboMembers.addItem(sm);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JPanel createStyledFormCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(new Color(230, 225, 215));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        return card;
    }

    private JLabel createFormLabel(String content) {
        JLabel l = new JLabel(content);
        l.setFont(new Font("SansSerif", Font.BOLD, 12));
        l.setForeground(new Color(80, 75, 65));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField tf = new JTextField(placeholder);
        tf.setPreferredSize(new Dimension(Integer.MAX_VALUE, 38));
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        tf.setForeground(Color.LIGHT_GRAY);
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);

        tf.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (tf.getText().equals(placeholder)) {
                    tf.setText("");
                    tf.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (tf.getText().trim().isEmpty()) {
                    tf.setText(placeholder);
                    tf.setForeground(Color.LIGHT_GRAY);
                }
            }
        });
        return tf;
    }

    private JButton createStyledButton(String text, Color bgThemeColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgThemeColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ==========================================
    // 🎯 RE-QUERY ON NAVIGATION METHOD
    // ==========================================
    private void navigateBackToDetails() {
        containerPanel.remove(this);

        // RE-QUERY: Forces service layers to grab the absolute latest state from the database
        List<Group> realPools = service.getAllEqubGroups();
        Group targetCtx = groupCtx;
        for (Group lookup : realPools) {
            if (lookup.getId() == groupCtx.getId()) {
                targetCtx = lookup; // Loaded with the new member counts and balance data
                break;
            }
        }

        // Rebuild the dashboard panel using the updated data context
        EqubGroupDetailPanel detailHub = new EqubGroupDetailPanel(containerPanel, service, targetCtx);
        containerPanel.add(detailHub, "GroupDetail");

        containerPanel.revalidate();
        containerPanel.repaint();
        ((CardLayout) containerPanel.getLayout()).show(containerPanel, "GroupDetail");
    }
}