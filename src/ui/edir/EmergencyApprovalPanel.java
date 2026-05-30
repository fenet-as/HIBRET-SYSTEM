package ui.edir;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import util.DBConnection;
import util.FontManager;

public class EmergencyApprovalPanel extends JPanel {
    private final JPanel containerPanel;

    // UI Components using standard JList structure
    private JList<ClaimItem> claimList;
    private DefaultListModel<ClaimItem> listModel;

    // Detail View Components
    private JLabel lblDetailId;
    private JLabel lblDetailName;
    private JLabel lblDetailType;
    private JLabel lblDetailFund;
    private JTextArea txtDetailDesc;

    // Helper data tracking container class
    private static class ClaimItem {
        final int id;
        final String fullName;
        final String emergencyType;
        final double amount;
        final String description;

        ClaimItem(int id, String fullName, String emergencyType, double amount, String description) {
            this.id = id;
            this.fullName = fullName;
            this.emergencyType = emergencyType == null ? "General" : emergencyType;
            this.amount = amount;
            this.description = description == null ? "" : description;
        }

        @Override
        public String toString() {
            // Displays case details as standard English plain strings
            return "ID: " + id + " | " + fullName + " (" + emergencyType + ")";
        }
    }

    public EmergencyApprovalPanel(JPanel containerPanel) {
        this.containerPanel = containerPanel;
        setOpaque(false);
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(25, 35, 30, 35));

        // Header Title Layout Setup (Hardcoded English)
        JLabel title = new JLabel("Emergency Cases Approval Management");
        title.setFont(FontManager.getBoldFont(22));
        title.setForeground(new Color(101, 53, 15));
        add(title, BorderLayout.NORTH);

        // Main Workspace Split Pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setOpaque(false);
        splitPane.setDividerLocation(320);
        splitPane.setBorder(null);

        // LEFT COLUMN: JList Menu Block
        listModel = new DefaultListModel<>();
        claimList = new JList<>(listModel);
        claimList.setFont(FontManager.getPlainFont(14));
        claimList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        claimList.setFixedCellHeight(40);

        claimList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setFont(FontManager.getPlainFont(14));
                label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return label;
            }
        });

        claimList.addListSelectionListener(this::handleListSelectionChanged);

        JScrollPane listScrollPane = new JScrollPane(claimList);
        listScrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(210, 200, 185)),
                " Pending Request List "
        ));
        splitPane.setLeftComponent(listScrollPane);

        // RIGHT COLUMN: Information Display Card Panel
        JPanel detailCard = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.setColor(new Color(230, 220, 205));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 12, 12));
                g2.dispose();
            }
        };
        detailCard.setOpaque(false);
        detailCard.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Static English Labels Form Block Placement
        gbc.gridx = 0; gbc.gridy = 0;
        detailCard.add(createStaticLabel("Case Reference ID:"), gbc);
        lblDetailId = createDynamicValueLabel("-");
        gbc.gridx = 1; detailCard.add(lblDetailId, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        detailCard.add(createStaticLabel("Beneficiary Name:"), gbc);
        lblDetailName = createDynamicValueLabel("-");
        gbc.gridx = 1; detailCard.add(lblDetailName, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        detailCard.add(createStaticLabel("Emergency Type:"), gbc);
        lblDetailType = createDynamicValueLabel("-");
        gbc.gridx = 1; detailCard.add(lblDetailType, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        detailCard.add(createStaticLabel("Requested Amount:"), gbc);
        lblDetailFund = createDynamicValueLabel("-");
        gbc.gridx = 1; detailCard.add(lblDetailFund, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        detailCard.add(createStaticLabel("Case Descriptions / Narrative Log:"), gbc);

        txtDetailDesc = new JTextArea(4, 25);
        txtDetailDesc.setFont(FontManager.getPlainFont(13));
        txtDetailDesc.setLineWrap(true);
        txtDetailDesc.setWrapStyleWord(true);
        txtDetailDesc.setEditable(false);
        txtDetailDesc.setBackground(new Color(250, 248, 243));
        JScrollPane descScroll = new JScrollPane(txtDetailDesc);
        gbc.gridy = 5; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1.0;
        detailCard.add(descScroll, gbc);

        splitPane.setRightComponent(detailCard);
        add(splitPane, BorderLayout.CENTER);

        loadPendingClaims();
    }

    private void handleListSelectionChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            ClaimItem selected = claimList.getSelectedValue();
            if (selected == null) {
                lblDetailId.setText("-");
                lblDetailName.setText("-");
                lblDetailType.setText("-");
                lblDetailFund.setText("-");
                txtDetailDesc.setText("");
            } else {
                lblDetailId.setText(String.valueOf(selected.id));
                lblDetailName.setText(selected.fullName);
                lblDetailType.setText(selected.emergencyType); // Directly sets the English database value
                lblDetailFund.setText(String.format("%,.2f ETB", selected.amount));
                txtDetailDesc.setText(selected.description);
            }
        }
    }

    private void loadPendingClaims() {
        listModel.clear();
        String sql = "SELECT e.id, m.full_name, e.emergency_type, e.amount_needed, e.status, e.description " +
                "FROM edir_emergencies e JOIN members m ON e.member_id = m.id WHERE e.status = 'PENDING' ORDER BY e.id DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                listModel.addElement(new ClaimItem(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("emergency_type"),
                        rs.getDouble("amount_needed"),
                        rs.getString("description")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private JLabel createStaticLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FontManager.getBoldFont(13));
        label.setForeground(Color.DARK_GRAY);
        return label;
    }

    private JLabel createDynamicValueLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FontManager.getPlainFont(14));
        label.setForeground(new Color(40, 40, 40));
        return label;
    }
}