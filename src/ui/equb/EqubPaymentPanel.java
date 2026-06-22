package ui.equb;

import service.EqubService;
import model.Group;
import model.Member;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class EqubPaymentPanel extends JPanel {
    private final JPanel containerPanel;
    private final EqubService service;
    private final Group groupCtx;
    private final JComboBox<Member> comboMembers;
    private final JTextField txtAmount;
    private final JTextField txtNote;

    // Design Tokens matching parent components
    private static final Color TEXT_DARK_BROWN = new Color(101, 53, 15);
    private static final Color BUTTON_GREEN = new Color(46, 117, 59);
    private static final Color TABLE_HEADER_BG = new Color(245, 242, 235);
    private static final Color TABLE_BORDER_COLOR = new Color(230, 225, 210);
    private static final Color CARD_LABEL_GRAY = new Color(130, 125, 115);

    public EqubPaymentPanel(JPanel containerPanel, EqubService service, Group groupCtx) {
        this.containerPanel = containerPanel;
        this.service = service;
        this.groupCtx = groupCtx;

        setOpaque(false);
        setLayout(new BorderLayout(0, 20));
        setBorder(BorderFactory.createEmptyBorder(25, 35, 40, 35));

        // ==========================================================
        // 1. TOP HEADER NAVIGATION LAYOUT
        // ==========================================================
        JPanel headPanel = new JPanel(new BorderLayout());
        headPanel.setOpaque(false);
        headPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        JLabel lblTitle = new JLabel("Record Payment");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 32));
        lblTitle.setForeground(TEXT_DARK_BROWN);
        headPanel.add(lblTitle, BorderLayout.WEST);

        JButton btnCancel = new JButton("Cancel") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(TABLE_HEADER_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(TABLE_BORDER_COLOR);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnCancel.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnCancel.setForeground(TEXT_DARK_BROWN);
        btnCancel.setContentAreaFilled(false);
        btnCancel.setBorderPainted(false);
        btnCancel.setFocusPainted(false);
        btnCancel.setPreferredSize(new Dimension(95, 42));
        btnCancel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCancel.addActionListener(e -> navigateBackToDetails());
        headPanel.add(btnCancel, BorderLayout.EAST);

        add(headPanel, BorderLayout.NORTH);

        // ==========================================================
        // 2. CENTRALIZED MODERN CARD FORM LAYOUT
        // ==========================================================
        JPanel centralBody = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        centralBody.setOpaque(false);

        // Form Container Styled as a Clean Modern Card Component
        JPanel cardFormPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 16, 16));
                g2.setColor(TABLE_BORDER_COLOR);
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 16, 16));
                g2.dispose();
            }
        };
        cardFormPanel.setLayout(new BoxLayout(cardFormPanel, BoxLayout.Y_AXIS));
        cardFormPanel.setBorder(BorderFactory.createEmptyBorder(30, 35, 35, 35));
        cardFormPanel.setPreferredSize(new Dimension(480, 390));

        // Field Group 1: Select Member Component
        cardFormPanel.add(createFormLabel("Select Member"));
        comboMembers = new JComboBox<>() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        comboMembers.setFont(new Font("SansSerif", Font.PLAIN, 14));
        comboMembers.setBackground(Color.WHITE);
        comboMembers.setPreferredSize(new Dimension(410, 40));
        comboMembers.setMaximumSize(new Dimension(410, 40));
        comboMembers.setAlignmentX(Component.LEFT_ALIGNMENT);

        List<Member> groupPool = service.getMembersInGroup(groupCtx.getId());
        for (Member m : groupPool) {
            comboMembers.addItem(m);
        }
        cardFormPanel.add(comboMembers);
        cardFormPanel.add(Box.createVerticalStrut(18));

        // Field Group 2: Payment Amount Component
        cardFormPanel.add(createFormLabel("Amount (ETB)"));
        txtAmount = new RoundedTextField(String.valueOf((int) groupCtx.getContributionAmount()));
        txtAmount.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtAmount.setPreferredSize(new Dimension(410, 40));
        txtAmount.setMaximumSize(new Dimension(410, 40));
        txtAmount.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardFormPanel.add(txtAmount);
        cardFormPanel.add(Box.createVerticalStrut(18));

        // Field Group 3: Note / Memo Description Component
        cardFormPanel.add(createFormLabel("Note / Memo"));
        txtNote = new RoundedTextField("e.g., Round 1 Payment");
        txtNote.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtNote.setPreferredSize(new Dimension(410, 40));
        txtNote.setMaximumSize(new Dimension(410, 40));
        txtNote.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardFormPanel.add(txtNote);
        cardFormPanel.add(Box.createVerticalStrut(32));

        // Save Form Bottom Submission Button
        JButton btnSave = new JButton("Save Payment") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BUTTON_GREEN);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnSave.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnSave.setForeground(Color.WHITE);
        btnSave.setContentAreaFilled(false);
        btnSave.setBorderPainted(false);
        btnSave.setFocusPainted(false);
        btnSave.setPreferredSize(new Dimension(410, 44));
        btnSave.setMaximumSize(new Dimension(410, 44));
        btnSave.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnSave.addActionListener(e -> {
            UIManager.put("OptionPane.messageFont", new Font("SansSerif", Font.PLAIN, 14));
            UIManager.put("OptionPane.buttonFont", new Font("SansSerif", Font.PLAIN, 13));

            Member targetedMember = (Member) comboMembers.getSelectedItem();
            String amountStr = txtAmount.getText().trim();

            if (targetedMember == null) {
                JOptionPane.showMessageDialog(this, "Please select a member first. Check if the group has members.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (amountStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter an amount.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                double paymentAmount = Double.parseDouble(amountStr);
                service.recordPayment(groupCtx.getId(), targetedMember.getId(), paymentAmount, "", txtNote.getText().trim());

                JOptionPane.showMessageDialog(this, "Payment successfully saved.", "Success", JOptionPane.INFORMATION_MESSAGE);
                navigateBackToDetails();
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number for the amount.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cardFormPanel.add(btnSave);
        centralBody.add(cardFormPanel);
        add(centralBody, BorderLayout.CENTER);
    }

    private JLabel createFormLabel(String content) {
        JLabel l = new JLabel(content);
        l.setFont(new Font("SansSerif", Font.BOLD, 13));
        l.setForeground(CARD_LABEL_GRAY);
        l.setBorder(BorderFactory.createEmptyBorder(0, 2, 6, 0));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private void navigateBackToDetails() {
        containerPanel.remove(this);

        List<Group> livePools = service.getAllEqubGroups();
        Group refreshedContext = groupCtx;
        for (Group lookup : livePools) {
            if (lookup.getId() == groupCtx.getId()) {
                refreshedContext = lookup;
                break;
            }
        }

        containerPanel.add(new EqubGroupDetailPanel(containerPanel, service, refreshedContext), "GroupDetail");
        ((CardLayout) containerPanel.getLayout()).show(containerPanel, "GroupDetail");
    }

    // Modern Custom Smooth Input Design Layer
    private static class RoundedTextField extends JTextField {
        public RoundedTextField(String text) {
            super(text);
            setOpaque(false);
            setBorder(new EmptyBorder(0, 12, 0, 12));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            g2.setColor(TABLE_BORDER_COLOR);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}