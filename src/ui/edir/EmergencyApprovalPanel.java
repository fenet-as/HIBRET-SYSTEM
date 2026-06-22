package ui.edir;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.*;
import util.DBConnection;

public class EmergencyApprovalPanel extends JPanel {
    private final JPanel containerPanel;

    private JList<ClaimItem> claimList;
    private DefaultListModel<ClaimItem> listModel;

    private JLabel lblDetailId;
    private JLabel lblDetailName;
    private JLabel lblDetailType;
    private JLabel lblDetailFund;
    private JTextArea txtDetailDesc;

    // Helper class to store details
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
            return "ID: " + id + " | " + fullName + " (" + emergencyType + ")";
        }
    }

    public EmergencyApprovalPanel(JPanel containerPanel) {
        this.containerPanel = containerPanel;
        setOpaque(false);
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(25, 35, 30, 35));

        // Simplest header title
        JLabel title = new JLabel("Review Claims");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(new Color(101, 53, 15));
        add(title, BorderLayout.NORTH);

        // Workspace Layout Split
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setOpaque(false);
        splitPane.setDividerLocation(320);
        splitPane.setBorder(null);

        // LEFT COLUMN: List of claims waiting
        listModel = new DefaultListModel<>();
        claimList = new JList<>(listModel);
        claimList.setFont(new Font("SansSerif", Font.PLAIN, 14));
        claimList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        claimList.setFixedCellHeight(40);

        claimList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setFont(new Font("SansSerif", Font.PLAIN, 14));
                label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return label;
            }
        });

        claimList.addListSelectionListener(this::handleListSelectionChanged);

        JScrollPane listScrollPane = new JScrollPane(claimList);
        listScrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(210, 200, 185)),
                " Waiting List "
        ));
        splitPane.setLeftComponent(listScrollPane);

        // RIGHT COLUMN: Claim Details View Card
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
        detailCard.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Simplest descriptive labels possible
        gbc.gridx = 0; gbc.gridy = 0;
        detailCard.add(createStaticLabel("ID:"), gbc);
        lblDetailId = createDynamicValueLabel("-");
        gbc.gridx = 1; detailCard.add(lblDetailId, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        detailCard.add(createStaticLabel("Name:"), gbc);
        lblDetailName = createDynamicValueLabel("-");
        gbc.gridx = 1; detailCard.add(lblDetailName, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        detailCard.add(createStaticLabel("Type:"), gbc);
        lblDetailType = createDynamicValueLabel("-");
        gbc.gridx = 1; detailCard.add(lblDetailType, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        detailCard.add(createStaticLabel("Amount:"), gbc);
        lblDetailFund = createDynamicValueLabel("-");
        gbc.gridx = 1; detailCard.add(lblDetailFund, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 8, 2, 8);
        detailCard.add(createStaticLabel("Details:"), gbc);

        // Description box space settings
        txtDetailDesc = new JTextArea(5, 25);
        txtDetailDesc.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtDetailDesc.setForeground(new Color(50, 50, 50));
        txtDetailDesc.setLineWrap(true);
        txtDetailDesc.setWrapStyleWord(true);
        txtDetailDesc.setEditable(false);
        txtDetailDesc.setBackground(new Color(250, 248, 243));

        JScrollPane descScroll = new JScrollPane(txtDetailDesc);
        descScroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 215, 205), 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(4, 8, 8, 8);
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
                lblDetailType.setText(selected.emergencyType);
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
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        label.setForeground(Color.DARK_GRAY);
        return label;
    }

    private JLabel createDynamicValueLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 14));
        label.setForeground(new Color(40, 40, 40));
        return label;
    }
}