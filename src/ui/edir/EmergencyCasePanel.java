package ui.edir;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import service.EdirService;

public class EmergencyCasePanel extends JPanel {
    private JComboBox<String> comboMember;
    private JComboBox<String> comboEmergencyType;
    private JTextField txtAmount;
    private JTextArea txtDescription;
    private JButton btnSubmit;

    private final JPanel parentWrapper;
    private final EdirService edirService;
    private final String groupName;

    public EmergencyCasePanel(JPanel parentWrapper, EdirService edirService, String groupName) {
        this.parentWrapper = parentWrapper;
        this.edirService = edirService;
        this.groupName = groupName;

        setLayout(new BorderLayout(25, 25));
        setBackground(new Color(250, 246, 238));
        setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        initMainForm();
    }

    private void initMainForm() {
        JPanel formContainer = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),20,20));
                g2.setColor(new Color(225, 215, 195));
                g2.draw(new RoundRectangle2D.Float(0,0,getWidth()-1,getHeight()-1,20,20));
                g2.dispose();
            }
        };
        formContainer.setOpaque(false);
        formContainer.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JButton btnBack = new JButton("← Dismiss Form");
        btnBack.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnBack.addActionListener(e -> {
            ((CardLayout) parentWrapper.getLayout()).show(parentWrapper, "EdirDetail");
        });

        JLabel lblTitle = new JLabel("Register Emergency Incident Log Entry (" + groupName + ")");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(74, 20, 10));

        JPanel headerLayout = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        headerLayout.setOpaque(false);
        headerLayout.add(btnBack);
        headerLayout.add(lblTitle);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formContainer.add(headerLayout, gbc);

        gbc.gridwidth = 1;
        gbc.weightx = 0.5;

        gbc.gridx = 0; gbc.gridy = 1;
        formContainer.add(createFieldLabel("Affected Member Beneficiary"), gbc);
        comboMember = new JComboBox<>(new String[]{"Abel Tesfaye", "Sara Kebede", "Hana Bekele"});
        comboMember.setPreferredSize(new Dimension(0, 38));
        gbc.gridy = 2;
        formContainer.add(comboMember, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        formContainer.add(createFieldLabel("Incident Classification Category"), gbc);
        comboEmergencyType = new JComboBox<>(new String[]{"Funeral Support", "Critical Medical Outpatient", "Accidental Crisis Reserve"});
        comboEmergencyType.setPreferredSize(new Dimension(0, 38));
        gbc.gridy = 2;
        formContainer.add(comboEmergencyType, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formContainer.add(createFieldLabel("Authorized Capital Allocation Sum (ETB)"), gbc);
        txtAmount = new JTextField("15000");
        txtAmount.setPreferredSize(new Dimension(0, 38));
        txtAmount.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridy = 4;
        formContainer.add(txtAmount, gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        formContainer.add(createFieldLabel("Contextual Log Summary Remarks"), gbc);
        txtDescription = new JTextArea("Standard disbursement tracking protocol criteria met.", 3, 20);
        txtDescription.setBorder(BorderFactory.createLineBorder(new Color(215, 205, 185)));
        txtDescription.setLineWrap(true);
        txtDescription.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JScrollPane descScroll = new JScrollPane(txtDescription);
        gbc.gridy = 4; gbc.gridheight = 2; gbc.fill = GridBagConstraints.BOTH;
        formContainer.add(descScroll, gbc);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(35, 12, 12, 12);

        btnSubmit = new JButton("Commit Incident Clearance Registration Log") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0,0, new Color(34, 145, 94), 0, getHeight(), new Color(24, 115, 70)));
                g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),12,12));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnSubmit.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setContentAreaFilled(false);
        btnSubmit.setBorderPainted(false);
        btnSubmit.setFocusPainted(false);
        btnSubmit.setPreferredSize(new Dimension(0, 48));
        btnSubmit.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btnSubmit.addActionListener(e -> {
            if(txtAmount.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Numeric field entry exception trace.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            JOptionPane.showMessageDialog(this, "Emergency data logged into global file successfully.");
            ((CardLayout) parentWrapper.getLayout()).show(parentWrapper, "EdirDetail");
        });

        formContainer.add(btnSubmit, gbc);
        add(formContainer, BorderLayout.CENTER);
    }

    private JLabel createFieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(new Color(90, 90, 90));
        return lbl;
    }
}