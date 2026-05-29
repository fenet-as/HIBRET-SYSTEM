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

        gbc.gridx = 0; gbc.gridy = 1;
        formContainer.add(createFieldLabel("Select Approved Active Claim Case"), gbc);
        comboApprovedCase = new JComboBox<>(new String[]{
                "CLAIM-ACTIVE: Urgent Support Case Selection Pool Reference"
        });
        gbc.gridy = 2;
        formContainer.add(comboApprovedCase, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        formContainer.add(createFieldLabel("Disbursed Payout Capital (birr)"), gbc);
        txtDisbursedAmount = new JTextField("15000");
        txtDisbursedAmount.setPreferredSize(new Dimension(0, 35));
        gbc.gridy = 2;
        formContainer.add(txtDisbursedAmount, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formContainer.add(createFieldLabel("Approving Chairman/Admin Board Authority"), gbc);
        txtApprovedBy = new JTextField("System Administrator");
        txtApprovedBy.setPreferredSize(new Dimension(0, 35));
        gbc.gridy = 4;
        formContainer.add(txtApprovedBy, gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        formContainer.add(createFieldLabel("Distribution Auditor Memo Notes"), gbc);
        txtNotes = new JTextArea("Disbursement processing transaction logs.", 2, 20);
        txtNotes.setBorder(BorderFactory.createLineBorder(new Color(210, 200, 185)));
        txtNotes.setLineWrap(true);
        JScrollPane notesScroll = new JScrollPane(txtNotes);
        gbc.gridy = 4;
        formContainer.add(notesScroll, gbc);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.insets = new Insets(30, 12, 12, 12);

        JButton btnSubmit = new JButton("Authorize Emergency Resource Payout") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(163, 51, 39));
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
            if (txtDisbursedAmount.getText().trim().isEmpty() || txtApprovedBy.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "All entry inputs are required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int conf = JOptionPane.showConfirmDialog(this, "Confirm fund allocation payout logs?", "Confirm Payout", JOptionPane.YES_NO_OPTION);
            if (conf == JOptionPane.YES_OPTION) {
                try {
                    double amt = Double.parseDouble(txtDisbursedAmount.getText().trim());
                    boolean ok = edirService.authorizePayout(groupName, "MOCK-ID", amt, txtApprovedBy.getText(), txtNotes.getText());
                    if (ok) {
                        JOptionPane.showMessageDialog(this, "Funds allocated and transaction logged successfully.");
                        for (Component comp : parentWrapper.getComponents()) {
                            if (comp instanceof EdirGroupDetailPanel) {
                                ((EdirGroupDetailPanel) comp).refreshDashboardMetricsAndLedger();
                            }
                        }
                        CardLayout cl = (CardLayout) parentWrapper.getLayout();
                        cl.show(parentWrapper, "EdirDetail");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid digit configurations format.", "Error", JOptionPane.ERROR_MESSAGE);
                }
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