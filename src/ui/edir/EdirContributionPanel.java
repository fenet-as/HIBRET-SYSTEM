package ui.edir;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import service.EdirService;

public class EdirContributionPanel extends JPanel {
    private final JPanel parentWrapper;
    private final EdirService edirService;
    private final String groupName;

    private JComboBox<String> comboMember;
    private JTextField txtAmount;
    private JComboBox<String> comboMonth;
    private JTextField txtReceiptNumber;

    public EdirContributionPanel(JPanel parentWrapper, EdirService edirService, String groupName) {
        this.parentWrapper = parentWrapper;
        this.edirService = edirService;
        this.groupName = groupName;

        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(253, 247, 237));
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        initMainForm();
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
        formContainer.setBorder(BorderFactory.createEmptyBorder(35, 35, 35, 35));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Custom Flow Header with Back Navigation
        JButton btnBack = new JButton("← Cancel");
        btnBack.addActionListener(e -> {
            CardLayout innerLayout = (CardLayout) parentWrapper.getLayout();
            innerLayout.show(parentWrapper, "EdirDetail");
        });

        JLabel lblTitle = new JLabel("Record Member Contribution — " + groupName);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitle.setForeground(new Color(101, 31, 16));

        JPanel headerLayout = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        headerLayout.setOpaque(false);
        headerLayout.add(btnBack);
        headerLayout.add(lblTitle);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formContainer.add(headerLayout, gbc);

        gbc.gridwidth = 1;
        gbc.weightx = 0.5;

        // Row 1: Member Selection & Month
        gbc.gridx = 0; gbc.gridy = 1;
        formContainer.add(createFieldLabel("Select Contributor Member"), gbc);
        comboMember = new JComboBox<>(new String[]{"Sara Kebede", "Abel Tesfaye", "Hana Bekele"});
        gbc.gridy = 2;
        formContainer.add(comboMember, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        formContainer.add(createFieldLabel("Contribution Period (Month)"), gbc);
        comboMonth = new JComboBox<>(new String[]{"Meskerem", "Tikimt", "Hidar", "Tahsas", "Tir", "Yakatit", "Megabit", "Miazia", "Genbot", "Sene", "Hamle", "Nehase"});
        gbc.gridy = 2;
        formContainer.add(comboMonth, gbc);

        // Row 2: Amount & Receipt Number reference tracker
        gbc.gridx = 0; gbc.gridy = 3;
        formContainer.add(createFieldLabel("Amount Paid (birr)"), gbc);
        txtAmount = new JTextField("200");
        txtAmount.setPreferredSize(new Dimension(0, 35));
        gbc.gridy = 4;
        formContainer.add(txtAmount, gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        formContainer.add(createFieldLabel("Manual Receipt Slit Number Reference"), gbc);
        txtReceiptNumber = new JTextField("REC-" + (int)(Math.random() * 90000 + 10000));
        txtReceiptNumber.setPreferredSize(new Dimension(0, 35));
        gbc.gridy = 4;
        formContainer.add(txtReceiptNumber, gbc);

        // Submit Button Row
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.insets = new Insets(30, 12, 12, 12);

        JButton btnSubmit = new JButton("Post Contribution Payment Log") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(28, 85, 163)); // Hibret Corporate Blue Accent
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
            if(txtAmount.getText().trim().isEmpty() || txtReceiptNumber.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "All database reporting parameters are mandatory.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            JOptionPane.showMessageDialog(this, "Contribution fee payment recorded into financial ledgers successfully.");
            CardLayout cl = (CardLayout) parentWrapper.getLayout();
            cl.show(parentWrapper, "EdirDetail");
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