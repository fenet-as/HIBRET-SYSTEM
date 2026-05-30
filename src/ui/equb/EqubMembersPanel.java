package ui.equb;

import service.EqubService;
import model.Group;
import model.Member;
import util.LanguageManager;
import util.FontManager; // ✅ Imported FontManager
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class EqubMembersPanel extends JPanel {
    private final JPanel containerPanel;
    private final EqubService service;
    private final Group groupCtx;

    // View Components
    private final DefaultTableModel tableModel;
    private final JTextField txtNewFullName;
    private final JTextField txtNewPhone;

    public EqubMembersPanel(JPanel containerPanel, EqubService service, Group groupCtx) {
        this.containerPanel = containerPanel;
        this.service = service;
        this.groupCtx = groupCtx;

        setOpaque(false);
        setLayout(new BorderLayout(0, 20));
        setBorder(BorderFactory.createEmptyBorder(25, 35, 30, 35));

        // ==========================================
        // 1. TOP ROW HEADER
        // ==========================================
        JPanel headPanel = new JPanel(new BorderLayout());
        headPanel.setOpaque(false);

        // ✅ Localized Panel Header Title with dynamic font engine mapping
        JLabel lblTitle = new JLabel(LanguageManager.getFormattedString("equb.members.title", groupCtx.getName()));
        lblTitle.setFont(FontManager.getBoldFont(24));
        lblTitle.setForeground(new Color(101, 53, 15));
        headPanel.add(lblTitle, BorderLayout.WEST);

        // ✅ Localized Cancel / Back Button Text Label
        JButton btnCancel = new JButton(LanguageManager.getString("equb.members.btn_back"));
        btnCancel.setFont(FontManager.getBoldFont(13));
        btnCancel.addActionListener(e -> navigateBackToDetails());
        headPanel.add(btnCancel, BorderLayout.EAST);
        add(headPanel, BorderLayout.NORTH);

        // ==========================================
        // 2. TWO-COLUMN SPLIT WORKSPACE
        // ==========================================
        JPanel splitBodyPanel = new JPanel(new GridLayout(1, 2, 30, 0));
        splitBodyPanel.setOpaque(false);

        // ------------------------------------------
        // COLUMN A: CURRENT POOL MEMBERS DIRECTORY LISTING
        // ------------------------------------------
        JPanel leftCard = createStyledFormCard();
        leftCard.setLayout(new BorderLayout(0, 15));

        // ✅ Localized Listing Subtitle Label
        JLabel lblLeftTitle = new JLabel(LanguageManager.getString("equb.members.left_title"));
        lblLeftTitle.setFont(FontManager.getBoldFont(16));
        lblLeftTitle.setForeground(new Color(101, 53, 15));
        leftCard.add(lblLeftTitle, BorderLayout.NORTH);

        // ✅ Localized Table Column Meta Descriptors
        String[] columns = {
                LanguageManager.getString("equb.members.col.rank"),
                LanguageManager.getString("equb.members.col.name"),
                LanguageManager.getString("equb.members.col.phone")
        };
        tableModel = new DefaultTableModel(null, columns) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(tableModel);
        table.setRowHeight(35);
        table.setFont(FontManager.getPlainFont(13)); // ✅ Safe font for multi-language table body text
        table.getTableHeader().setFont(FontManager.getBoldFont(12));
        table.getTableHeader().setBackground(new Color(242, 238, 228));

        JScrollPane scrollPane = new JScrollPane(table);
        leftCard.add(scrollPane, BorderLayout.CENTER);

        // Load active data rows directly into table views
        loadGroupMembersData();
        splitBodyPanel.add(leftCard);

        // ------------------------------------------
        // COLUMN B: REGISTRATION OF EXCLUSIVE NEW MEMBERS
        // ------------------------------------------
        JPanel rightCard = createStyledFormCard();
        rightCard.setLayout(new BoxLayout(rightCard, BoxLayout.Y_AXIS));

        // ✅ Localized Registration Section Subtitle
        JLabel lblRightTitle = new JLabel(LanguageManager.getString("equb.members.right_title"));
        lblRightTitle.setFont(FontManager.getBoldFont(16));
        lblRightTitle.setForeground(new Color(34, 100, 51));
        rightCard.add(lblRightTitle);
        rightCard.add(Box.createVerticalStrut(25));

        // ✅ Localized Form Field Input Titles and Placeholders
        rightCard.add(createFormLabel(LanguageManager.getString("equb.members.lbl_name")));
        String placeholderName = LanguageManager.getString("equb.members.placeholder_name");
        txtNewFullName = createStyledTextField(placeholderName);
        rightCard.add(txtNewFullName);
        rightCard.add(Box.createVerticalStrut(20));

        rightCard.add(createFormLabel(LanguageManager.getString("equb.members.lbl_phone")));
        String placeholderPhone = LanguageManager.getString("equb.members.placeholder_phone");
        txtNewPhone = createStyledTextField(placeholderPhone);
        rightCard.add(txtNewPhone);
        rightCard.add(Box.createVerticalStrut(40));

        // ✅ Localized Action Button Registration Label
        JButton btnRegisterNew = createStyledButton(LanguageManager.getString("equb.members.btn_register"), new Color(34, 100, 51));
        btnRegisterNew.addActionListener(e -> {
            // Apply proper fallback fonts onto the JOptionPane global parameters
            UIManager.put("OptionPane.messageFont", FontManager.getPlainFont(14));
            UIManager.put("OptionPane.buttonFont", FontManager.getPlainFont(13));

            String fullName = txtNewFullName.getText().trim();
            String phone = txtNewPhone.getText().trim();

            if (fullName.isEmpty() || fullName.equals(placeholderName) ||
                    phone.isEmpty() || phone.equals(placeholderPhone)) {
                JOptionPane.showMessageDialog(this, LanguageManager.getString("equb.members.err.validation"), LanguageManager.getString("msg.validation_error"), JOptionPane.WARNING_MESSAGE);
                return;
            }

            Member newMemberProfile = new Member();
            newMemberProfile.setFullName(fullName);
            newMemberProfile.setPhone(phone);
            newMemberProfile.setStatus("Active");

            // 1. Establish the unique profile sequence entry inside master ledger
            int generatedMemberId = service.createNewSystemMember(newMemberProfile);

            if (generatedMemberId > 0) {
                // 2. Link the verified freshly allocated ID cleanly to this specific group
                service.addMemberToGroup(groupCtx.getId(), generatedMemberId);

                // ✅ Localized Dynamic Integration Prompt Context String
                String successMessage = LanguageManager.getFormattedString("equb.members.success", fullName, groupCtx.getName());
                JOptionPane.showMessageDialog(this, successMessage, LanguageManager.getString("msg.success"), JOptionPane.INFORMATION_MESSAGE);

                // Reset text inputs and reload data components instantly using safe dynamic labels
                txtNewFullName.setText(placeholderName);
                txtNewFullName.setForeground(Color.LIGHT_GRAY);
                txtNewPhone.setText(placeholderPhone);
                txtNewPhone.setForeground(Color.LIGHT_GRAY);

                loadGroupMembersData();
            } else {
                JOptionPane.showMessageDialog(this, LanguageManager.getString("equb.members.err.database"), LanguageManager.getString("msg.error"), JOptionPane.ERROR_MESSAGE);
            }
        });
        rightCard.add(btnRegisterNew);
        splitBodyPanel.add(rightCard);

        add(splitBodyPanel, BorderLayout.CENTER);
    }

    // ==========================================
    // HELPER & DATA POPULATION METHODS
    // ==========================================
    private void loadGroupMembersData() {
        tableModel.setRowCount(0);
        try {
            List<Member> existingGroupMembers = service.getMembersInGroup(groupCtx.getId());
            int rank = 1;
            for (Member m : existingGroupMembers) {
                tableModel.addRow(new Object[]{
                        rank++,
                        m.getFullName(),
                        m.getPhone()
                });
            }
        } catch (Exception e) {
            System.err.println("Failed to synchronize table contents with group_members cluster arrays.");
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
        l.setFont(FontManager.getBoldFont(13));
        l.setForeground(new Color(80, 75, 65));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField tf = new JTextField(placeholder);
        tf.setFont(FontManager.getPlainFont(14)); // ✅ Fixed hardcoded placeholder font layout anomalies
        tf.setPreferredSize(new Dimension(Integer.MAX_VALUE, 40));
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
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
        btn.setFont(FontManager.getBoldFont(14));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(Integer.MAX_VALUE, 45));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void navigateBackToDetails() {
        containerPanel.remove(this);

        List<Group> realPools = service.getAllEqubGroups();
        Group targetCtx = groupCtx;
        for (Group lookup : realPools) {
            if (lookup.getId() == groupCtx.getId()) {
                targetCtx = lookup;
                break;
            }
        }

        EqubGroupDetailPanel detailHub = new EqubGroupDetailPanel(containerPanel, service, targetCtx);
        containerPanel.add(detailHub, "GroupDetail");

        containerPanel.revalidate();
        containerPanel.repaint();
        ((CardLayout) containerPanel.getLayout()).show(containerPanel, "GroupDetail");
    }
}