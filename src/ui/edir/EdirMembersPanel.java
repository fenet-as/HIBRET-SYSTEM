package ui.edir;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import java.util.Map;
import service.EdirService;

public class EdirMembersPanel extends JPanel {
    private final JPanel parentWrapper;
    private final EdirService edirService;

    private final int groupId;
    private String groupDisplayName = "Loading Group...";

    private JLabel lblTitle;
    private JTextField txtFullName;
    private JTextField txtPhone;
    private JTable membersTable;
    private DefaultTableModel tableModel;

    public EdirMembersPanel(JPanel parentWrapper, EdirService edirService, int groupId) {
        this.parentWrapper = parentWrapper;
        this.edirService = edirService;
        this.groupId = groupId;

        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(253, 247, 237));
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        initHeader();
        initSplitBody();
        loadMembersData();
    }

    private void initHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 45));

        JButton btnBack = new JButton("Return to Details") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(235, 225, 210));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnBack.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnBack.setForeground(new Color(101, 31, 16));
        btnBack.setContentAreaFilled(false);
        btnBack.setBorderPainted(false);
        btnBack.setPreferredSize(new Dimension(160, 38));
        btnBack.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btnBack.addActionListener(e -> {
            for (Component comp : parentWrapper.getComponents()) {
                if (comp instanceof EdirGroupDetailPanel) {
                    ((EdirGroupDetailPanel) comp).refreshDashboardMetricsAndLedger();
                }
            }
            CardLayout innerLayout = (CardLayout) parentWrapper.getLayout();
            innerLayout.show(parentWrapper, "EdirDetail");
        });

        lblTitle = new JLabel("Membership Directory for " + groupDisplayName);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitle.setForeground(new Color(101, 31, 16));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setOpaque(false);
        leftPanel.add(btnBack);
        leftPanel.add(lblTitle);

        headerPanel.add(leftPanel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);
    }

    private void initSplitBody() {
        JPanel splitPanel = new JPanel(new GridLayout(1, 2, 25, 0));
        splitPanel.setOpaque(false);

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
        formContainer.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel lblFormTitle = new JLabel("Register New Associate Member");
        lblFormTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblFormTitle.setForeground(new Color(101, 31, 16));
        gbc.gridx = 0; gbc.gridy = 0;
        formContainer.add(lblFormTitle, gbc);

        gbc.gridy = 1;
        formContainer.add(createFieldLabel("Full Legal Name"), gbc);
        txtFullName = new JTextField();
        txtFullName.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtFullName.setPreferredSize(new Dimension(0, 35));
        gbc.gridy = 2;
        formContainer.add(txtFullName, gbc);

        gbc.gridy = 3;
        formContainer.add(createFieldLabel("Phone Contact Number"), gbc);
        txtPhone = new JTextField();
        txtPhone.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtPhone.setPreferredSize(new Dimension(0, 35));
        gbc.gridy = 4;
        formContainer.add(txtPhone, gbc);

        JButton btnSubmit = new JButton("Add Member Reference") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(46, 117, 89));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnSubmit.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setContentAreaFilled(false);
        btnSubmit.setBorderPainted(false);
        btnSubmit.setFocusPainted(false);
        btnSubmit.setPreferredSize(new Dimension(0, 42));

        btnSubmit.addActionListener(e -> {
            UIManager.put("OptionPane.messageFont", new Font("SansSerif", Font.PLAIN, 14));
            UIManager.put("OptionPane.buttonFont", new Font("SansSerif", Font.PLAIN, 13));

            String name = txtFullName.getText().trim();
            String phone = txtPhone.getText().trim();

            if (name.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please completely fill out all the structural data registry input attributes.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean ok = edirService.addMemberToGroup(this.groupId, name, phone);
            if (ok) {
                loadMembersData();
                txtFullName.setText("");
                txtPhone.setText("");
                JOptionPane.showMessageDialog(this, "Successfully enrolled " + name + " into " + groupDisplayName + " databases.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to write structural parameters into system data files.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        gbc.gridy = 5;
        gbc.insets = new Insets(20, 8, 8, 8);
        formContainer.add(btnSubmit, gbc);

        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setOpaque(false);

        JLabel lblTableTitle = new JLabel("Currently Active Registered Members");
        lblTableTitle.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblTableTitle.setForeground(new Color(101, 31, 16));
        lblTableTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        tableContainer.add(lblTableTitle, BorderLayout.NORTH);

        String[] cols = { "Index", "Full Legal Name", "Phone Contact", "Account Status" };
        tableModel = new DefaultTableModel(null, cols) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        membersTable = new JTable(tableModel);
        membersTable.setRowHeight(38);
        membersTable.setBackground(Color.WHITE);
        membersTable.setShowGrid(false);
        membersTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        membersTable.getTableHeader().setBackground(new Color(249, 237, 222));
        membersTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));

        JScrollPane sp = new JScrollPane(membersTable);
        sp.setBorder(BorderFactory.createLineBorder(new Color(230, 215, 195)));
        tableContainer.add(sp, BorderLayout.CENTER);

        splitPanel.add(formContainer);
        splitPanel.add(tableContainer);
        add(splitPanel, BorderLayout.CENTER);
    }

    /**
     * Public utility method to be called when the custom "Actions" column triggers a deletion error.
     * Displays a clean explicit notification explaining why the database action cannot proceed.
     */
    public void handleDeletionError(String memberName) {
        UIManager.put("OptionPane.messageFont", new Font("SansSerif", Font.PLAIN, 14));
        UIManager.put("OptionPane.buttonFont", new Font("SansSerif", Font.PLAIN, 13));

        JOptionPane.showMessageDialog(this,
                "Cannot delete member '" + memberName + "' because they have linked transaction history.\n" +
                        "Please remove or settle their transaction records before removing them from the system directory.",
                "Deletion Blocked",
                JOptionPane.ERROR_MESSAGE);
    }

    private void loadMembersData() {
        Map<String, String> details = edirService.getGroupDetails(this.groupId);
        if (details != null && !details.isEmpty()) {
            this.groupDisplayName = details.getOrDefault("name", "Edir Group");
            lblTitle.setText("Membership Directory for " + this.groupDisplayName);
        }

        tableModel.setRowCount(0);

        List<Map<String, String>> members = edirService.getMembersByGroup(this.groupId);

        int sequenceNo = 1;
        for (Map<String, String> m : members) {
            tableModel.addRow(new Object[]{
                    String.valueOf(sequenceNo++),
                    m.get("full_name"),
                    m.get("phone"),
                    m.get("status")
            });
        }
    }

    private JLabel createFieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        lbl.setForeground(Color.DARK_GRAY);
        return lbl;
    }
}