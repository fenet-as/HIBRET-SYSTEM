package ui.edir;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import service.EdirService;

public class CreateEdirGroupPanel extends JPanel {
    private final JPanel parentWrapper;
    private final EdirService edirService;
    private final int loggedInUserId;

    private JTextField txtGroupName;
    private JTextField txtMonthlyFee;
    private JTextField txtInitialDeposit;
    private JTextArea txtRules;

    public CreateEdirGroupPanel(JPanel parentWrapper, EdirService edirService, int loggedInUserId) {
        this.parentWrapper = parentWrapper;
        this.edirService = edirService;
        this.loggedInUserId = loggedInUserId;

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

        JButton btnBack = new JButton("Back");
        btnBack.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnBack.setForeground(new Color(101, 31, 16));
        btnBack.addActionListener(e -> {
            CardLayout innerLayout = (CardLayout) parentWrapper.getLayout();
            innerLayout.show(parentWrapper, "EdirHome");
        });

        JLabel lblTitle = new JLabel("Create New Edir Group");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitle.setForeground(new Color(101, 31, 16));

        JPanel headerLayout = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        headerLayout.setOpaque(false);
        headerLayout.add(btnBack);
        headerLayout.add(lblTitle);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formContainer.add(headerLayout, gbc);

        gbwidth(); // Reset layout rules helper context
        gbc.gridwidth = 1;
        gbc.weightx = 0.5;

        // Row 1: Group Name & Monthly Subscription Fee Labels
        gbc.gridx = 0; gbc.gridy = 1;
        formContainer.add(createFieldLabel("Edir Group Name"), gbc);
        txtGroupName = new JTextField();
        txtGroupName.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtGroupName.setPreferredSize(new Dimension(0, 35));
        gbc.gridy = 2;
        formContainer.add(txtGroupName, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        formContainer.add(createFieldLabel("Monthly Contribution Fee (ETB)"), gbc);
        txtMonthlyFee = new JTextField("200");
        txtMonthlyFee.setFont(new Font("SansSerif", Font.PLAIN, 14)); // ✅ Fixed argument types mismatch error
        txtMonthlyFee.setPreferredSize(new Dimension(0, 35));
        gbc.gridy = 2;
        formContainer.add(txtMonthlyFee, gbc);

        // Row 2: Initial Capital Pool Deposit & Terms Bylaws Labels
        gbc.gridx = 0; gbc.gridy = 3;
        formContainer.add(createFieldLabel("Initial Vault Starting Deposit (ETB)"), gbc);
        txtInitialDeposit = new JTextField("5000");
        txtInitialDeposit.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtInitialDeposit.setPreferredSize(new Dimension(0, 35));
        gbc.gridy = 4;
        formContainer.add(txtInitialDeposit, gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        formContainer.add(createFieldLabel("Group Rules and Bylaws Statement"), gbc);
        txtRules = new JTextArea("1. Members must commit to paying contributions on time.\n2. General meetings are scheduled monthly.", 3, 20);
        txtRules.setBorder(BorderFactory.createLineBorder(new Color(210, 200, 185)));
        txtRules.setLineWrap(true);
        txtRules.setWrapStyleWord(true);
        txtRules.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JScrollPane rulesScroll = new JScrollPane(txtRules);
        gbc.gridy = 4; gbc.gridheight = 2; gbc.fill = GridBagConstraints.BOTH;
        formContainer.add(rulesScroll, gbc);

        // Submit Button Setup
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(30, 12, 12, 12);

        JButton btnSubmit = new JButton("Create Group") {
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
        btnSubmit.setFont(new Font("SansSerif", Font.BOLD, 15));
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setContentAreaFilled(false);
        btnSubmit.setBorderPainted(false);
        btnSubmit.setFocusPainted(false);
        btnSubmit.setPreferredSize(new Dimension(0, 45));

        btnSubmit.addActionListener(e -> {
            UIManager.put("OptionPane.messageFont", new Font("SansSerif", Font.PLAIN, 14));
            UIManager.put("OptionPane.buttonFont", new Font("SansSerif", Font.PLAIN, 13));

            String groupName = txtGroupName.getText().trim();
            String feeStr = txtMonthlyFee.getText().trim().replace(",", "");
            String initialPoolStr = txtInitialDeposit.getText().trim().replace(",", "");
            String rules = txtRules.getText().trim();

            if(groupName.isEmpty() || feeStr.isEmpty() || initialPoolStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all required form properties.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                double fee = Double.parseDouble(feeStr);
                double initialPool = Double.parseDouble(initialPoolStr);

                boolean success = edirService.createGroup(groupName, fee, initialPool, rules, this.loggedInUserId);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Successfully created group: " + groupName, "Success", JOptionPane.INFORMATION_MESSAGE);

                    // Clear inputs and reset defaults
                    txtGroupName.setText("");
                    txtMonthlyFee.setText("200");
                    txtInitialDeposit.setText("5000");
                    txtRules.setText("1. Members must commit to paying contributions on time.\n2. General meetings are scheduled monthly.");

                    // Refresh database records inside home grid panel
                    for (Component viewComponent : parentWrapper.getComponents()) {
                        if (viewComponent instanceof EdirHomePanel) {
                            ((EdirHomePanel) viewComponent).loadGroups();
                        }
                    }

                    // Revert UI context layout position
                    CardLayout innerLayout = (CardLayout) parentWrapper.getLayout();
                    innerLayout.show(parentWrapper, "EdirHome");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to write data records. Please check the database log configuration.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please verify financial values contain completely clean decimal numbers.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        formContainer.add(btnSubmit, gbc);
        add(formContainer, BorderLayout.CENTER);
    }

    private void gbwidth() {
        // structural dummy configuration logic placeholder
    }

    private JLabel createFieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 13));
        lbl.setForeground(Color.DARK_GRAY);
        return lbl;
    }
}