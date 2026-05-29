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

        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(253, 247, 237));
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        initMainForm();
    }

    private void initMainForm() {
        JPanel formContainer = new JPanel(new GridBagLayout());
        formContainer.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JButton btnBack = new JButton("← Cancel");
        btnBack.addActionListener(e -> {
            CardLayout innerLayout = (CardLayout) parentWrapper.getLayout();
            innerLayout.show(parentWrapper, "EdirDetail");
        });

        JLabel lblTitle = new JLabel("Register Emergency Case (" + groupName + ")");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitle.setForeground(new Color(101, 31, 16));

        JPanel headerLayout = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        headerLayout.setOpaque(false);
        headerLayout.add(btnBack);
        headerLayout.add(lblTitle);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formContainer.add(headerLayout, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.5;
        formContainer.add(createFieldLabel("Select Member"), gbc);
        comboMember = new JComboBox<>(new String[]{"Abel", "Sara", "Hana"});
        gbc.gridy = 2;
        formContainer.add(comboMember, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        formContainer.add(createFieldLabel("Type of Emergency"), gbc);
        comboEmergencyType = new JComboBox<>(new String[]{"Funeral", "Medical", "Accident"});
        gbc.gridy = 2;
        formContainer.add(comboEmergencyType, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formContainer.add(createFieldLabel("Amount Needed (birr)"), gbc);
        txtAmount = new JTextField("15,000");
        txtAmount.setPreferredSize(new Dimension(0, 35));
        gbc.gridy = 4;
        formContainer.add(txtAmount, gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        formContainer.add(createFieldLabel("Description"), gbc);
        txtDescription = new JTextArea("Funeral support for family membership", 3, 20);
        txtDescription.setBorder(BorderFactory.createLineBorder(new Color(210, 200, 185)));
        txtDescription.setLineWrap(true);
        JScrollPane descScroll = new JScrollPane(txtDescription);
        gbc.gridy = 4; gbc.gridheight = 2; gbc.fill = GridBagConstraints.BOTH;
        formContainer.add(descScroll, gbc);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(25, 10, 10, 10);

        btnSubmit = new JButton("Submit Case") {
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
        btnSubmit.setFont(new Font("SansSerif", Font.BOLD, 15));
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setContentAreaFilled(false);
        btnSubmit.setBorderPainted(false);
        btnSubmit.setFocusPainted(false);
        btnSubmit.setPreferredSize(new Dimension(0, 45));

        btnSubmit.addActionListener(e -> {
            if(txtAmount.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Amount is required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            JOptionPane.showMessageDialog(this, "Emergency Registered successfully.");
            CardLayout innerLayout = (CardLayout) parentWrapper.getLayout();
            innerLayout.show(parentWrapper, "EdirDetail");
        });

        formContainer.add(btnSubmit, gbc);
        add(formContainer, BorderLayout.CENTER);
    }

    private JLabel createFieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 13));
        lbl.setForeground(Color.DARK_GRAY);
        return lbl;
    }
}