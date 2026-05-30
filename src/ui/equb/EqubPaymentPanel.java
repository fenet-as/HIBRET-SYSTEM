package ui.equb;

import service.EqubService;
import model.Group;
import model.Member;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class EqubPaymentPanel extends JPanel {
    private final JPanel containerPanel;
    private final EqubService service;
    private final Group groupCtx;
    private final JComboBox<Member> comboMembers;
    private final JTextField txtAmount;
    private final JTextField txtNote;

    public EqubPaymentPanel(JPanel containerPanel, EqubService service, Group groupCtx) {
        this.containerPanel = containerPanel;
        this.service = service;
        this.groupCtx = groupCtx;

        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(25, 35, 30, 35));

        // --- TOP ROW TITLE ---
        JPanel head = new JPanel(new BorderLayout());
        head.setOpaque(false);

        JLabel lblTitle = new JLabel("Record Contribution Payment");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitle.setForeground(new Color(101, 53, 15));
        head.add(lblTitle, BorderLayout.WEST);

        JButton btnCancel = new JButton("Cancel");
        btnCancel.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnCancel.addActionListener(e -> navigateBackToDetails());
        head.add(btnCancel, BorderLayout.EAST);
        add(head, BorderLayout.NORTH);

        // --- SCROLLABLE CENTRALIZED FORM CONTENT ---
        JPanel centralBody = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 10));
        centralBody.setOpaque(false);

        JPanel formFieldsPanel = new JPanel();
        formFieldsPanel.setOpaque(false);
        formFieldsPanel.setLayout(new BoxLayout(formFieldsPanel, BoxLayout.Y_AXIS));
        formFieldsPanel.add(Box.createVerticalStrut(15));

        formFieldsPanel.add(createFormLabel("Select Contributing Member"));
        comboMembers = new JComboBox<>();
        comboMembers.setFont(new Font("SansSerif", Font.PLAIN, 13));
        comboMembers.setPreferredSize(new Dimension(380, 38));
        comboMembers.setMaximumSize(new Dimension(380, 38));

        List<Member> groupPool = service.getMembersInGroup(groupCtx.getId());
        for (Member m : groupPool) {
            comboMembers.addItem(m);
        }
        formFieldsPanel.add(comboMembers);
        formFieldsPanel.add(Box.createVerticalStrut(15));

        formFieldsPanel.add(createFormLabel("Payment Amount (ETB)"));
        txtAmount = new JTextField(String.valueOf((int) groupCtx.getContributionAmount()));
        txtAmount.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtAmount.setPreferredSize(new Dimension(380, 38));
        txtAmount.setMaximumSize(new Dimension(380, 38));
        formFieldsPanel.add(txtAmount);
        formFieldsPanel.add(Box.createVerticalStrut(15));

        formFieldsPanel.add(createFormLabel("Reference Note / Memo"));
        txtNote = new JTextField("e.g., Round 1 Payment");
        txtNote.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtNote.setPreferredSize(new Dimension(380, 38));
        txtNote.setMaximumSize(new Dimension(380, 38));
        formFieldsPanel.add(txtNote);
        formFieldsPanel.add(Box.createVerticalStrut(30));

        JButton btnSave = new JButton("Confirm & Save Payment") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(34, 100, 51));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnSave.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnSave.setForeground(Color.WHITE);
        btnSave.setContentAreaFilled(false);
        btnSave.setBorderPainted(false);
        btnSave.setPreferredSize(new Dimension(380, 42));
        btnSave.setMaximumSize(new Dimension(380, 42));
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnSave.addActionListener(e -> {
            UIManager.put("OptionPane.messageFont", new Font("SansSerif", Font.PLAIN, 14));
            UIManager.put("OptionPane.buttonFont", new Font("SansSerif", Font.PLAIN, 13));

            Member targetedMember = (Member) comboMembers.getSelectedItem();
            String amountStr = txtAmount.getText().trim();

            if (targetedMember == null) {
                JOptionPane.showMessageDialog(this, "No active member found selected. Verify group registration files.", "Selection Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (amountStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please completely fill out all financial input properties.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                double paymentAmount = Double.parseDouble(amountStr);
                service.recordPayment(groupCtx.getId(), targetedMember.getId(), paymentAmount, "", txtNote.getText().trim());

                JOptionPane.showMessageDialog(this, "Payment transaction verified and posted successfully.", "Transaction Recorded", JOptionPane.INFORMATION_MESSAGE);
                navigateBackToDetails();
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "The numerical contribution layout value cannot be cleanly parsed.", "Format Mismatch", JOptionPane.ERROR_MESSAGE);
            }
        });

        formFieldsPanel.add(btnSave);
        centralBody.add(formFieldsPanel);
        add(centralBody, BorderLayout.CENTER);
    }

    private JLabel createFormLabel(String content) {
        JLabel l = new JLabel(content);
        l.setFont(new Font("SansSerif", Font.BOLD, 13));
        l.setForeground(new Color(70, 70, 70));
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
}