package ui.equb;

import service.EqubService;
import model.Group;
import model.Member;
import util.LanguageManager;
import util.FontManager; // ✅ Imported FontManager
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
        // ✅ Localized Header Title Label with dynamic font metrics lookup
        JLabel lblTitle = new JLabel(LanguageManager.getString("equb.payment.title"));
        lblTitle.setFont(FontManager.getBoldFont(22));
        lblTitle.setForeground(new Color(101, 53, 15));
        head.add(lblTitle, BorderLayout.WEST);

        // ✅ Localized Cancel Button
        JButton btnCancel = new JButton(LanguageManager.getString("equb.payment.btn_cancel"));
        btnCancel.setFont(FontManager.getBoldFont(13));
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

        // ✅ Localized Combobox Form Entry Title Label
        formFieldsPanel.add(createFormLabel(LanguageManager.getString("equb.payment.lbl_select_member")));
        comboMembers = new JComboBox<>();
        comboMembers.setFont(FontManager.getPlainFont(13)); // ✅ Safe font mapping for embedded item lists
        comboMembers.setPreferredSize(new Dimension(380, 38));
        comboMembers.setMaximumSize(new Dimension(380, 38));

        List<Member> groupPool = service.getMembersInGroup(groupCtx.getId());
        for (Member m : groupPool) {
            comboMembers.addItem(m);
        }
        formFieldsPanel.add(comboMembers);
        formFieldsPanel.add(Box.createVerticalStrut(15));

        // ✅ Localized Cash Rate Dynamic Label Box
        formFieldsPanel.add(createFormLabel(LanguageManager.getString("equb.payment.lbl_amount")));
        txtAmount = new JTextField(String.valueOf((int) groupCtx.getContributionAmount()));
        txtAmount.setFont(FontManager.getPlainFont(14));
        txtAmount.setPreferredSize(new Dimension(380, 38));
        txtAmount.setMaximumSize(new Dimension(380, 38));
        formFieldsPanel.add(txtAmount);
        formFieldsPanel.add(Box.createVerticalStrut(15));

        // ✅ Localized Reference Note Input Title and Text Area Placeholders
        formFieldsPanel.add(createFormLabel(LanguageManager.getString("equb.payment.lbl_note")));
        txtNote = new JTextField(LanguageManager.getString("equb.payment.placeholder_note"));
        txtNote.setFont(FontManager.getPlainFont(14));
        txtNote.setPreferredSize(new Dimension(380, 38));
        txtNote.setMaximumSize(new Dimension(380, 38));
        formFieldsPanel.add(txtNote);
        formFieldsPanel.add(Box.createVerticalStrut(30));

        // ✅ Localized Submission Trigger Button
        JButton btnSave = new JButton(LanguageManager.getString("equb.payment.btn_save")) {
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
        btnSave.setFont(FontManager.getBoldFont(14));
        btnSave.setForeground(Color.WHITE);
        btnSave.setContentAreaFilled(false);
        btnSave.setBorderPainted(false);
        btnSave.setPreferredSize(new Dimension(380, 42));
        btnSave.setMaximumSize(new Dimension(380, 42));
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnSave.addActionListener(e -> {
            // Reconfigure active alert schemas to safely map dynamic typography assets
            UIManager.put("OptionPane.messageFont", FontManager.getPlainFont(14));
            UIManager.put("OptionPane.buttonFont", FontManager.getPlainFont(13));

            Member targetedMember = (Member) comboMembers.getSelectedItem();
            String amountStr = txtAmount.getText().trim();

            if (targetedMember == null) {
                // ✅ Localized Error Alerts
                JOptionPane.showMessageDialog(this, LanguageManager.getString("equb.payment.err.no_members"), LanguageManager.getString("equb.payment.err.no_members_title"), JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (amountStr.isEmpty()) {
                // ✅ Localized Blank Validation Popup Prompt
                JOptionPane.showMessageDialog(this, LanguageManager.getString("equb.payment.err.validation"), LanguageManager.getString("msg.validation_error"), JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                double paymentAmount = Double.parseDouble(amountStr);
                service.recordPayment(groupCtx.getId(), targetedMember.getId(), paymentAmount, "", txtNote.getText().trim());

                // ✅ Localized Database Execution Success Alert
                JOptionPane.showMessageDialog(this, LanguageManager.getString("equb.payment.success"), LanguageManager.getString("equb.payment.success_title"), JOptionPane.INFORMATION_MESSAGE);
                navigateBackToDetails();
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, LanguageManager.getString("msg.parsing_error"), LanguageManager.getString("msg.error"), JOptionPane.ERROR_MESSAGE);
            }
        });

        formFieldsPanel.add(btnSave);
        centralBody.add(formFieldsPanel);
        add(centralBody, BorderLayout.CENTER);
    }

    private JLabel createFormLabel(String content) {
        JLabel l = new JLabel(content);
        l.setFont(FontManager.getBoldFont(13));
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