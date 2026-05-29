package ui.edir;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import model.EdirGroup;
import service.EdirService;
import service.impl.EdirServiceImpl;
import util.DBConnection;

public class EmergencyCasePanel extends JPanel {
    private final JPanel containerPanel;
    private final EdirGroup group;
    private final EdirService edirService = new EdirServiceImpl();

    private JComboBox<String> comboMembers;
    private JComboBox<String> comboType;
    private JTextField txtPayoutAmount;
    private JTextArea txtDesc;

    public EmergencyCasePanel(JPanel containerPanel, EdirGroup group) {
        this.containerPanel = containerPanel;
        this.group = group;

        setBackground(new Color(252, 249, 242));
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Top Main Form Section Header line layout
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titlePanel.setOpaque(false);
        JLabel titleIcon = new JLabel("⚠️");
        titleIcon.setFont(new Font("SansSerif", Font.PLAIN, 24));
        JLabel titleText = new JLabel("Register Emergency Case");
        titleText.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleText.setForeground(new Color(115, 35, 15));
        titlePanel.add(titleIcon);
        titlePanel.add(titleText);
        add(titlePanel, BorderLayout.NORTH);

        // Core Form Inputs Column (Left Side Grid)
        JPanel leftFormGrid = new JPanel(new GridBagLayout());
        leftFormGrid.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Row 1: Select Member & Type of Emergency
        gbc.gridy = 0; gbc.gridx = 0;
        leftFormGrid.add(createLabelField("Select Member"), gbc);
        gbc.gridx = 1;
        leftFormGrid.add(createLabelField("Type of Emergency"), gbc);

        gbc.gridy = 1; gbc.gridx = 0;
        comboMembers = new JComboBox<>();
        loadActiveSystemMembers();
        leftFormGrid.add(comboMembers, gbc);

        gbc.gridx = 1;
        comboType = new JComboBox<>(new String[]{"Funeral", "Medical Shock", "Disaster Loss"});
        leftFormGrid.add(comboType, gbc);

        // Row 2: Amount Needed & Description Text field box inputs
        gbc.gridy = 2; gbc.gridx = 0;
        leftFormGrid.add(createLabelField("Amount Needed"), gbc);
        gbc.gridx = 1;
        leftFormGrid.add(createLabelField("Description"), gbc);

        gbc.gridy = 3; gbc.gridx = 0;
        txtPayoutAmount = new JTextField("15000");
        txtPayoutAmount.setPreferredSize(new Dimension(150, 35));
        leftFormGrid.add(txtPayoutAmount, gbc);

        gbc.gridx = 1; gbc.gridheight = 2; gbc.fill = GridBagConstraints.BOTH;
        txtDesc = new JTextArea("Funeral support for family member");
        txtDesc.setBorder(BorderFactory.createLineBorder(new Color(200, 190, 175)));
        leftFormGrid.add(new JScrollPane(txtDesc), gbc);

        // Reset constraints layout settings for bottom button rows
        gbc.gridheight = 1; gbc.fill = GridBagConstraints.HORIZONTAL;

        // Bottom Submission execution button layout row line
        JButton btnSubmit = new JButton("Submit Case");
        btnSubmit.setBackground(new Color(34, 112, 43));
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setFont(new Font("SansSerif", Font.BOLD, 15));
        btnSubmit.setPreferredSize(new Dimension(getWidth(), 45));
        btnSubmit.setFocusPainted(false);
        btnSubmit.addActionListener(e -> {
            String selection = (String) comboMembers.getSelectedItem();
            if (selection == null) return;
            int mId = Integer.parseInt(selection.split(" - ")[0]);
            double amt = Double.parseDouble(txtPayoutAmount.getText().trim());

            edirService.registerEmergencyCase(mId, group.getId(), (String)comboType.getSelectedItem(), amt, txtDesc.getText());
            JOptionPane.showMessageDialog(this, "Emergency Case successfully processed & stored!");
            ((CardLayout) containerPanel.getLayout()).show(containerPanel, "EdirGroupDetail");
        });

        // Right side Vector Graphics placeholder layout area container view
        JPanel rightArtVectorPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Draws a fallback illustration matching the mockup's hands holding a heart
                g2.setColor(new Color(245, 180, 180, 120));
                g2.fillOval(getWidth()/2 - 50, getHeight()/2 - 60, 100, 100);
                g2.setColor(new Color(115, 35, 15, 80));
                g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
                g2.drawString("❤️ Community Support", getWidth()/2 - 60, getHeight()/2 + 70);
            }
        };
        rightArtVectorPanel.setOpaque(false);
        rightArtVectorPanel.setPreferredSize(new Dimension(300, getHeight()));

        JPanel mainSplitContentBody = new JPanel(new BorderLayout());
        mainSplitContentBody.setOpaque(false);
        mainSplitContentBody.add(leftFormGrid, BorderLayout.CENTER);
        mainSplitContentBody.add(rightArtVectorPanel, BorderLayout.EAST);
        mainSplitContentBody.add(btnSubmit, BorderLayout.SOUTH);

        add(mainSplitContentBody, BorderLayout.CENTER);
    }

    private JLabel createLabelField(String caption) {
        JLabel lbl = new JLabel(caption);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 13));
        lbl.setForeground(new Color(90, 80, 70));
        return lbl;
    }

    private void loadActiveSystemMembers() {
        String sql = "SELECT id, full_name FROM members ORDER BY full_name ASC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                comboMembers.addItem(rs.getInt("id") + " - " + rs.getString("full_name"));
            }
            if(comboMembers.getItemCount() == 0) {
                comboMembers.addItem("1 - Abel");
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }
}