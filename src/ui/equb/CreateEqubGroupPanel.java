package ui.equb;

import service.EqubService;
import javax.swing.*;
import java.awt.*;

public class CreateEqubGroupPanel extends JPanel {
    private final JPanel containerPanel;
    private final EqubService service;
    private final JTextField txtName;
    private final JTextField txtAmount;
    private final int loggedInUserId;

    public CreateEqubGroupPanel(JPanel containerPanel, EqubService service, int loggedInUserId) {
        this.containerPanel = containerPanel;
        this.service = service;
        this.loggedInUserId = loggedInUserId;

        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(25, 35, 30, 35));

        // --- HEADER BAR ---
        JPanel headPanel = new JPanel(new BorderLayout());
        headPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Create New Equb Group");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblTitle.setForeground(new Color(101, 53, 15));
        headPanel.add(lblTitle, BorderLayout.WEST);

        JButton btnCancel = new JButton("Cancel");
        btnCancel.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnCancel.addActionListener(e -> navigateBack());
        headPanel.add(btnCancel, BorderLayout.EAST);
        add(headPanel, BorderLayout.NORTH);

        // --- FORM CONTENT WRAPPER ---
        JPanel centralBody = new JPanel(new GridLayout(1, 2, 40, 0));
        centralBody.setOpaque(false);

        JPanel formFieldsPanel = new JPanel();
        formFieldsPanel.setOpaque(false);
        formFieldsPanel.setLayout(new BoxLayout(formFieldsPanel, BoxLayout.Y_AXIS));
        formFieldsPanel.add(Box.createVerticalStrut(30));

        formFieldsPanel.add(createFormLabel("Equb Group Name"));
        txtName = new JTextField();
        txtName.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtName.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        formFieldsPanel.add(txtName);
        formFieldsPanel.add(Box.createVerticalStrut(20));

        formFieldsPanel.add(createFormLabel("Contribution Amount per Round (ETB)"));
        txtAmount = new JTextField();
        txtAmount.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtAmount.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        formFieldsPanel.add(txtAmount);
        formFieldsPanel.add(Box.createVerticalStrut(35));

        JButton btnSave = new JButton("Save & Initialize Group") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(34, 100, 51));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnSave.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnSave.setForeground(Color.WHITE);
        btnSave.setContentAreaFilled(false);
        btnSave.setBorderPainted(false);
        btnSave.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnSave.addActionListener(e -> {
            UIManager.put("OptionPane.messageFont", new Font("SansSerif", Font.PLAIN, 14));
            UIManager.put("OptionPane.buttonFont", new Font("SansSerif", Font.PLAIN, 13));

            String poolName = txtName.getText().trim();
            String amountStr = txtAmount.getText().trim();

            if (poolName.isEmpty() || amountStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please completely fill out all the structural text field input properties.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                double parsedAmount = Double.parseDouble(amountStr);

                service.createEqubGroup(poolName, parsedAmount, this.loggedInUserId);

                JOptionPane.showMessageDialog(this, "Equb savings pool created successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                navigateBack();
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "The numerical value assigned to the coverage sum property could not be parsed cleanly.", "Format Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        formFieldsPanel.add(btnSave);
        centralBody.add(formFieldsPanel);

        // Graphic Branding Subpanel Layout Block
        JPanel decorationPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(242, 238, 228));
                g2.fillRoundRect(20, 30, getWidth() - 40, getHeight() - 60, 12, 12);
                g2.setColor(new Color(130, 90, 40));

                g2.setFont(new Font("SansSerif", Font.PLAIN, 15));

                String systemBrandText = "Equb Rotational Savings Manager";
                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(systemBrandText);
                int textHeight = fm.getAscent();
                g2.drawString(systemBrandText, (getWidth() - textWidth) / 2, (getHeight() + textHeight) / 2);
                g2.dispose();
            }
        };
        decorationPanel.setOpaque(false);
        centralBody.add(decorationPanel);

        add(centralBody, BorderLayout.CENTER);
    }

    private JLabel createFormLabel(String fieldText) {
        JLabel l = new JLabel(fieldText);
        l.setFont(new Font("SansSerif", Font.BOLD, 13));
        l.setForeground(new Color(101, 53, 15));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private void navigateBack() {
        ((CardLayout) containerPanel.getLayout()).show(containerPanel, "EqubHome");
        for (Component c : containerPanel.getComponents()) {
            if (c instanceof EqubHomePanel) {
                ((EqubHomePanel) c).loadEqubGroupsData();
            }
        }
    }
}