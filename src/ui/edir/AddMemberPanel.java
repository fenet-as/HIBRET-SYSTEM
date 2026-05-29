package ui.edir;

import javax.swing.*;
import java.awt.*;
import service.EdirService;
import model.EdirGroup;

public class AddMemberPanel extends JPanel {
    private final JPanel parentWrapper;
    private final EdirGroup group;
    private final EdirGroupDetailPanel trackingDashboard;

    private JTextField txtFullName;
    private JTextField txtPhone;

    public AddMemberPanel(JPanel parentWrapper, EdirGroup group, EdirGroupDetailPanel trackingDashboard) {
        this.parentWrapper = parentWrapper;
        this.group = group;
        this.trackingDashboard = trackingDashboard;

        setLayout(new GridBagLayout());
        setBackground(new Color(253, 247, 237));
        setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        initFormComponents();
    }

    private void initFormComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = new JLabel("Enroll New Group Member");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitle.setForeground(new Color(101, 31, 16));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(lblTitle, gbc);

        // Name input UI setup
        gbc.gridwidth = 1; gbc.gridy = 1;
        add(new JLabel("Full Name:"), gbc);
        txtFullName = new JTextField(20);
        gbc.gridx = 1;
        add(txtFullName, gbc);

        // Phone input UI setup
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Phone Number:"), gbc);
        txtPhone = new JTextField(20);
        gbc.gridx = 1;
        add(txtPhone, gbc);

        // Action Buttons Setup Container Panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);

        JButton btnCancel = new JButton("Cancel");
        JButton btnSubmit = new JButton("Register Member");
        btnSubmit.setBackground(new Color(46, 117, 89));
        btnSubmit.setForeground(Color.WHITE);

        // ✅ CANCEL ACTION ROUTINE: Triggers immediate back navigation
        btnCancel.addActionListener(e -> returnToDashboardView());

        // ✅ SUBMIT ACTION ROUTINE: Persists registration entry to Postgres database then routes back
        btnSubmit.addActionListener(e -> {
            String name = txtFullName.getText().trim();
            String phone = txtPhone.getText().trim();

            if (name.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All input fields are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Interface with Postgres via structural Service Layer
            boolean success = trackingDashboard.getEdirService().addMemberToGroup(group.getName(), name, phone);
            if (success) {
                JOptionPane.showMessageDialog(this, "Member successfully added to " + group.getName());
                returnToDashboardView();
            } else {
                JOptionPane.showMessageDialog(this, "Could not process database enrollment record.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(btnCancel);
        btnPanel.add(btnSubmit);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        add(btnPanel, gbc);
    }

    private void returnToDashboardView() {
        // Trigger the top-level refresh to update stat counts from the database
        trackingDashboard.refreshDashboardMetricsAndLedger();

        // Pop the view deck container card backward securely
        CardLayout cl = (CardLayout) parentWrapper.getLayout();
        cl.show(parentWrapper, "EdirDetail");
    }
}