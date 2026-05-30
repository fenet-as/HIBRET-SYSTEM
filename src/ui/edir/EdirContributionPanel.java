package ui.edir;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import service.EdirService;
import util.LanguageManager;
import util.FontManager;

public class EdirContributionPanel extends JPanel {
    private final JPanel parentWrapper;
    private final EdirService edirService;

    private final int groupId;
    private String groupDisplayName = "Loading Group...";

    private JLabel lblTitle;
    private JComboBox<String> comboMember;
    private JTextField txtAmount;
    private JTextField txtReceiptNumber;

    public EdirContributionPanel(JPanel parentWrapper, EdirService edirService, int groupId) {
        this.parentWrapper = parentWrapper;
        this.edirService = edirService;
        this.groupId = groupId;

        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(253, 247, 237));
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        initMainForm();
        loadMembersCombo();
    }

    private void initMainForm() {
        JPanel formContainer = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.setColor(new Color(235, 225, 210));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 16, 16));
                g2.dispose();
            }
        };
        formContainer.setOpaque(false);
        formContainer.setBorder(BorderFactory.createEmptyBorder(35, 45, 35, 45));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Top Cancel Back Button
        JButton btnBack = new JButton(LanguageManager.getString("edir.contribution.btn_cancel"));
        btnBack.setFont(FontManager.getBoldFont(12));
        btnBack.addActionListener(e -> {
            CardLayout innerLayout = (CardLayout) parentWrapper.getLayout();
            innerLayout.show(parentWrapper, "EdirDetail");
        });

        lblTitle = new JLabel(LanguageManager.getFormattedString("edir.contribution.title", groupDisplayName));
        lblTitle.setFont(FontManager.getBoldFont(22));
        lblTitle.setForeground(new Color(101, 31, 16));

        JPanel headerLayout = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        headerLayout.setOpaque(false);
        headerLayout.add(btnBack);
        headerLayout.add(lblTitle);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(12, 12, 20, 12);
        formContainer.add(headerLayout, gbc);

        // Reset base bounds
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 12, 10, 12);

        // Row 1: Contributor Member
        gbc.gridy = 1;
        formContainer.add(createFieldLabel(LanguageManager.getString("edir.contribution.lbl_contributor")), gbc);

        comboMember = new JComboBox<>();
        comboMember.setFont(FontManager.getPlainFont(14));
        comboMember.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setFont(FontManager.getPlainFont(14));
                return label;
            }
        });
        gbc.gridy = 2;
        formContainer.add(comboMember, gbc);

        // Row 2: Amount Textbox
        gbc.gridy = 3;
        formContainer.add(createFieldLabel(LanguageManager.getString("edir.contribution.lbl_amount")), gbc);

        txtAmount = new JTextField("200");
        txtAmount.setFont(FontManager.getPlainFont(14));
        txtAmount.setPreferredSize(new Dimension(360, 42));
        txtAmount.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 200, 185), 1, true),
                BorderFactory.createEmptyBorder(0, 12, 0, 12)
        ));
        gbc.gridy = 4;
        formContainer.add(txtAmount, gbc);

        // Row 3: Receipt Textbox
        gbc.gridy = 5;
        formContainer.add(createFieldLabel(LanguageManager.getString("edir.contribution.lbl_receipt")), gbc);

        txtReceiptNumber = new JTextField("REC-" + (int)(Math.random() * 90000 + 10000));
        txtReceiptNumber.setFont(FontManager.getPlainFont(14));
        txtReceiptNumber.setPreferredSize(new Dimension(360, 42));
        txtReceiptNumber.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 200, 185), 1, true),
                BorderFactory.createEmptyBorder(0, 12, 0, 12)
        ));
        gbc.gridy = 6;
        formContainer.add(txtReceiptNumber, gbc);

        // Submit Button Box
        gbc.gridy = 7;
        gbc.insets = new Insets(30, 12, 10, 12);

        JButton btnSubmit = new JButton(LanguageManager.getString("edir.contribution.btn_submit")) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(28, 85, 163));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnSubmit.setFont(FontManager.getBoldFont(15));
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setContentAreaFilled(false);
        btnSubmit.setBorderPainted(false);
        btnSubmit.setFocusPainted(false);
        btnSubmit.setPreferredSize(new Dimension(200, 45));

        btnSubmit.addActionListener(e -> {
            UIManager.put("OptionPane.messageFont", FontManager.getPlainFont(14));
            UIManager.put("OptionPane.buttonFont", FontManager.getPlainFont(13));

            if (comboMember.getSelectedItem() == null || txtAmount.getText().trim().isEmpty() || txtReceiptNumber.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, LanguageManager.getString("edir.contribution.err.required"), LanguageManager.getString("msg.error"), JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                String member = comboMember.getSelectedItem().toString();
                double amt = Double.parseDouble(txtAmount.getText().trim());
                String receipt = txtReceiptNumber.getText().trim();

                String systemMonth = LocalDate.now().getMonth().toString();

                boolean ok = edirService.recordContribution(this.groupId, member, systemMonth, amt, receipt);
                if (ok) {
                    JOptionPane.showMessageDialog(this, LanguageManager.getString("edir.contribution.success"), LanguageManager.getString("msg.success"), JOptionPane.INFORMATION_MESSAGE);
                    for (Component comp : parentWrapper.getComponents()) {
                        if (comp instanceof EdirGroupDetailPanel) {
                            ((EdirGroupDetailPanel) comp).refreshDashboardMetricsAndLedger();
                        }
                    }
                    CardLayout cl = (CardLayout) parentWrapper.getLayout();
                    cl.show(parentWrapper, "EdirDetail");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, LanguageManager.getString("edir.contribution.err.number"), LanguageManager.getString("msg.error"), JOptionPane.ERROR_MESSAGE);
            }
        });

        formContainer.add(btnSubmit, gbc);

        // To center the inner card layout smoothly within the BorderLayout constraints
        JPanel centeringWrapper = new JPanel(new GridBagLayout());
        centeringWrapper.setOpaque(false);

        GridBagConstraints centerConstraints = new GridBagConstraints();
        centerConstraints.gridx = 0;
        centerConstraints.gridy = 0;
        centerConstraints.anchor = GridBagConstraints.CENTER;
        centeringWrapper.add(formContainer, centerConstraints);

        // ✅ FIXED: Explicitly assigns standard string layout flags to the parent panel's BorderLayout
        add(centeringWrapper, BorderLayout.CENTER);
    }

    private void loadMembersCombo() {
        Map<String, String> details = edirService.getGroupDetails(this.groupId);
        if (details != null && !details.isEmpty()) {
            this.groupDisplayName = details.getOrDefault("name", "Edir Group");
            lblTitle.setText(LanguageManager.getFormattedString("edir.contribution.title", this.groupDisplayName));
        }

        comboMember.removeAllItems();

        List<Map<String, String>> list = edirService.getMembersByGroup(this.groupId);
        for(Map<String, String> m : list) {
            comboMember.addItem(m.get("full_name"));
        }
    }

    private JLabel createFieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FontManager.getBoldFont(13));
        lbl.setForeground(Color.DARK_GRAY);
        return lbl;
    }
}