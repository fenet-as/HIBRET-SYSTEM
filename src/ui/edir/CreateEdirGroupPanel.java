package ui.edir;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import service.EdirService;

public class CreateEdirGroupPanel extends JPanel {
    private final JPanel parentWrapper;
    private final EdirService edirService;

    private JTextField txtGroupName;
    private JTextField txtMonthlyFee;
    private JTextField txtInitialDeposit;
    private JTextArea txtRules;

    public CreateEdirGroupPanel(JPanel parentWrapper, EdirService edirService) {
        this.parentWrapper = parentWrapper;
        this.edirService = edirService;

        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(253, 247, 237)); // Standard Hibret cream canvas background
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

        // Custom Top Header with Action Routing Back Button
        JButton btnBack = new JButton("← Cancel");
        btnBack.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnBack.setForeground(new Color(101, 31, 16));
        btnBack.addActionListener(e -> {
            CardLayout innerLayout = (CardLayout) parentWrapper.getLayout();
            innerLayout.show(parentWrapper, "EdirHome");
        });

        JLabel lblTitle = new JLabel("Establish New Edir Group");
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

        // Row 1: Group Name & Monthly Subscription Fee
        gbc.gridx = 0; gbc.gridy = 1;
        formContainer.add(createFieldLabel("Edir Group Name"), gbc);
        txtGroupName = new JTextField();
        txtGroupName.setPreferredSize(new Dimension(0, 35));
        gbc.gridy = 2;
        formContainer.add(txtGroupName, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        formContainer.add(createFieldLabel("Monthly Membership Fee (birr)"), gbc);
        txtMonthlyFee = new JTextField("200");
        txtMonthlyFee.setPreferredSize(new Dimension(0, 35));
        gbc.gridy = 2;
        formContainer.add(txtMonthlyFee, gbc);

        // Row 2: Initial Capital Pool Deposit & Terms/By-laws Memo Field
        gbc.gridx = 0; gbc.gridy = 3;
        formContainer.add(createFieldLabel("Initial Group Reserve Deposit (birr)"), gbc);
        txtInitialDeposit = new JTextField("5,000");
        txtInitialDeposit.setPreferredSize(new Dimension(0, 35));
        gbc.gridy = 4;
        formContainer.add(txtInitialDeposit, gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        formContainer.add(createFieldLabel("Group Policies / Claims Criteria Bylaws"), gbc);
        txtRules = new JTextArea("Standard community support rules apply. Payout allocations require a minimum committee confirmation audit.", 3, 20);
        txtRules.setBorder(BorderFactory.createLineBorder(new Color(210, 200, 185)));
        txtRules.setLineWrap(true);
        txtRules.setFont(new Font("SansSerif", Font.PLAIN, 12));
        JScrollPane rulesScroll = new JScrollPane(txtRules);
        gbc.gridy = 4; gbc.gridheight = 2; gbc.fill = GridBagConstraints.BOTH;
        formContainer.add(rulesScroll, gbc);

        // Submit Row Setup
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(30, 12, 12, 12);

        JButton btnSubmit = new JButton("Create and Register Group") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(28, 85, 163)); // Corporate Blue
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
            String groupName = txtGroupName.getText().trim();
            String feeStr = txtMonthlyFee.getText().trim();
            String initialPoolStr = txtInitialDeposit.getText().trim();
            String rules = txtRules.getText().trim();

            if(groupName.isEmpty() || feeStr.isEmpty() || initialPoolStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All input identification entries are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                double fee = Double.parseDouble(feeStr);
                double initialPool = Double.parseDouble(initialPoolStr);

                // Runs transaction sequence inside Postgres groups & edir_groups tables simultaneously
                boolean success = edirService.createGroup(groupName, fee, initialPool, rules);
                if (success) {
                    JOptionPane.showMessageDialog(this, "EDIR Group '" + groupName + "' has been successfully registered!");

                    // Clear inputs
                    txtGroupName.setText("");
                    txtMonthlyFee.setText("");
                    txtInitialDeposit.setText("");
                    txtRules.setText("");

                    // Instantaneously trigger landing view table records updates
                    for (Component viewComponent : parentWrapper.getComponents()) {
                        if (viewComponent instanceof EdirHomePanel) {
                            ((EdirHomePanel) viewComponent).loadGroups();
                        }
                    }

                    // Switch layout cards back to grid view homepage
                    CardLayout innerLayout = (CardLayout) parentWrapper.getLayout();
                    innerLayout.show(parentWrapper, "EdirHome");
                } else {
                    JOptionPane.showMessageDialog(this, "Could not create group. The group name might already be taken.", "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid number values for numeric input configuration entries.", "Input Error", JOptionPane.ERROR_MESSAGE);
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