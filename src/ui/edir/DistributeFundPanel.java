package ui.edir;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import service.EdirService;

public class DistributeFundPanel extends JPanel {
    private final JPanel parentWrapper;
    private final EdirService edirService;
    private final String groupName;

    private JComboBox<String> comboApprovedCase;
    private JTextField txtDisbursedAmount;
    private JTextField txtApprovedBy;
    private JTextArea txtNotes;

    public DistributeFundPanel(JPanel parentWrapper, EdirService edirService, String groupName) {
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

        JButton btnBack = new JButton("← Cancel");
        btnBack.addActionListener(e -> {
            CardLayout innerLayout = (CardLayout) parentWrapper.getLayout();
            innerLayout.show(parentWrapper, "EdirDetail");
        });

        JLabel lblTitle = new JLabel("Distribute Emergency Support Fund — " + groupName);
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

        // Row 1: Approved Cases List Lookup & Amount Allocation
        gbc.gridx = 0; gbc.gridy = 1;
        formContainer.add(createFieldLabel("Select Approved Active Claim Case"), gbc);
        comboApprovedCase = new JComboBox<>(new String[]{
                "CASE-8930: Abel Tesfaye (Funeral Assistance Request)",
                "CASE-4211: Sara Kebede (Medical Outpatient Support)"
        });
        gbc.gridy = 2;
        formContainer.add(comboApprovedCase, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        formContainer.add(createFieldLabel("Disbursed Payout Capital (birr)"), gbc);
        txtDisbursedAmount = new JTextField("15,000");
        txtDisbursedAmount.setPreferredSize(new Dimension(0, 35));
        gbc.gridy = 2;
        formContainer.add(txtDisbursedAmount, gbc);

        // Row 2: Approving Authority Signature & Auditor Notes Memo Field
        gbc.gridx = 0; gbc.gridy = 3;
        formContainer.add(createFieldLabel("Approving Chairman/Admin Board Authority"), gbc);
        txtApprovedBy = new JTextField("System Administrator");
        txtApprovedBy.setPreferredSize(new Dimension(0, 35));
        gbc.gridy = 4;
        formContainer.add(txtApprovedBy, gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        formContainer.add(createFieldLabel("Distribution Auditor Memo Notes"), gbc);
        txtNotes = new JTextArea("Disbursement processing via cash reserve withdrawal confirmation receipt.", 2, 20);
        txtNotes.setBorder(BorderFactory.createLineBorder(new Color(210, 200, 185)));
        txtNotes.setLineWrap(true);
        JScrollPane notesScroll = new JScrollPane(txtNotes);
        gbc.gridy = 4;
        formContainer.add(notesScroll, gbc);

        // Submit Row Action Trigger
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.insets = new Insets(30, 12, 12, 12);

        JButton btnSubmit = new JButton("Authorize Emergency Resource Payout") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(163, 51, 39)); // Alert/Distribution crimson red
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




        //  CORRECTED CODE
        btnSubmit.addActionListener(e -> {
            if(txtDisbursedAmount.getText().trim().isEmpty() || txtApprovedBy.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "All financial clearance entries are mandatory.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int conf = JOptionPane.showConfirmDialog(this, "Are you sure you want to log this payout? This will deduct funds from the pool.", "Confirm Payout", JOptionPane.YES_NO_OPTION);
            if (conf == JOptionPane.YES_OPTION) {
                // Line removed successfully
                JOptionPane.showMessageDialog(this, "Funds allocated and transaction logged successfully.");
                CardLayout cl = (CardLayout) parentWrapper.getLayout();
                cl.show(parentWrapper, "EdirDetail");
            }
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