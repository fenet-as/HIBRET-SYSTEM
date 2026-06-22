package ui.equb;

import service.EqubService;
import model.Group;
import model.Member;
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

        JLabel lblTitle = new JLabel("Manage Members: " + groupCtx.getName());
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblTitle.setForeground(new Color(101, 53, 15));
        headPanel.add(lblTitle, BorderLayout.WEST);

        JButton btnCancel = new JButton("Back to Details");
        btnCancel.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnCancel.addActionListener(e -> navigateBackToDetails());
        headPanel.add(btnCancel, BorderLayout.EAST);
        add(headPanel, BorderLayout.NORTH);

        // ==========================================
        // 2. TWO-COLUMN SPLIT WORKSPACE
        // ==========================================
        JPanel splitBodyPanel = new JPanel(new GridLayout(1, 2, 30, 0));
        splitBodyPanel.setOpaque(false);

        // ------------------------------------------
        // COLUMN A: CURRENT MEMBERS LIST
        // ------------------------------------------
        JPanel leftCard = createStyledFormCard();
        leftCard.setLayout(new BorderLayout(0, 15));

        JLabel lblLeftTitle = new JLabel("Current Members");
        lblLeftTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblLeftTitle.setForeground(new Color(101, 53, 15));
        leftCard.add(lblLeftTitle, BorderLayout.NORTH);

        String[] columns = { "#", "Full Name", "Phone Number" };
        tableModel = new DefaultTableModel(null, columns) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(tableModel);
        table.setRowHeight(35);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(242, 238, 228));

        JScrollPane scrollPane = new JScrollPane(table);
        leftCard.add(scrollPane, BorderLayout.CENTER);

        loadGroupMembersData();
        splitBodyPanel.add(leftCard);

        // ------------------------------------------
        // COLUMN B: ADD NEW MEMBERS
        // ------------------------------------------
        JPanel rightCard = createStyledFormCard();
        rightCard.setLayout(new BoxLayout(rightCard, BoxLayout.Y_AXIS));

        JLabel lblRightTitle = new JLabel("Add New Member");
        lblRightTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblRightTitle.setForeground(new Color(34, 100, 51));
        rightCard.add(lblRightTitle);
        rightCard.add(Box.createVerticalStrut(25));

        rightCard.add(createFormLabel("Full Name"));
        String placeholderName = "Enter full name";
        txtNewFullName = createStyledTextField(placeholderName);
        rightCard.add(txtNewFullName);
        rightCard.add(Box.createVerticalStrut(20));

        rightCard.add(createFormLabel("Phone Number"));
        String placeholderPhone = "Enter phone number";
        txtNewPhone = createStyledTextField(placeholderPhone);
        rightCard.add(txtNewPhone);
        rightCard.add(Box.createVerticalStrut(40));

        JButton btnRegisterNew = createStyledButton("Save & Add to Group", new Color(34, 100, 51));
        btnRegisterNew.addActionListener(e -> {
            UIManager.put("OptionPane.messageFont", new Font("SansSerif", Font.PLAIN, 14));
            UIManager.put("OptionPane.buttonFont", new Font("SansSerif", Font.PLAIN, 13));

            String fullName = txtNewFullName.getText().trim();
            String phone = txtNewPhone.getText().trim();

            if (fullName.isEmpty() || fullName.equals(placeholderName) ||
                    phone.isEmpty() || phone.equals(placeholderPhone)) {
                JOptionPane.showMessageDialog(this, "Please fill out all the boxes.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Member newMemberProfile = new Member();
            newMemberProfile.setFullName(fullName);
            newMemberProfile.setPhone(phone);
            newMemberProfile.setStatus("Active");

            int generatedMemberId = service.createNewSystemMember(newMemberProfile);

            if (generatedMemberId > 0) {
                service.addMemberToGroup(groupCtx.getId(), generatedMemberId);

                String successMessage = String.format("Successfully added %s to %s.", fullName, groupCtx.getName());
                JOptionPane.showMessageDialog(this, successMessage, "Success", JOptionPane.INFORMATION_MESSAGE);

                txtNewFullName.setText(placeholderName);
                txtNewFullName.setForeground(Color.LIGHT_GRAY);
                txtNewPhone.setText(placeholderPhone);
                txtNewPhone.setForeground(Color.LIGHT_GRAY);

                loadGroupMembersData();
            } else {
                JOptionPane.showMessageDialog(this, "Could not save to database.", "Error", JOptionPane.ERROR_MESSAGE);
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
            System.err.println("Could not load the group list.");
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
        l.setFont(new Font("SansSerif", Font.BOLD, 13));
        l.setForeground(new Color(80, 75, 65));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField tf = new JTextField(placeholder);
        tf.setFont(new Font("SansSerif", Font.PLAIN, 14));
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
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
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