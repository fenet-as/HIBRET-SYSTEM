package ui.edir;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import service.EdirService;

public class EdirMembersPanel extends JPanel {
    private final JPanel parentWrapper;
    private final EdirService edirService;
    private final String groupName;

    private JTextField txtFullName;
    private JTextField txtPhone;
    private JTable membersTable;
    private DefaultTableModel tableModel;

    public EdirMembersPanel(JPanel parentWrapper, EdirService edirService, String groupName) {
        this.parentWrapper = parentWrapper;
        this.edirService = edirService;
        this.groupName = groupName;

        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(253, 247, 237));
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        initHeader();
        initSplitBody();
    }

    private void initHeader() {
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        headerPanel.setOpaque(false);

        JButton btnBack = new JButton("← Back");
        btnBack.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnBack.setForeground(new Color(101, 31, 16));
        btnBack.addActionListener(e -> {
            CardLayout innerLayout = (CardLayout) parentWrapper.getLayout();
            innerLayout.show(parentWrapper, "EdirDetail");
        });

        JLabel lblTitle = new JLabel("Manage Members — " + groupName);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitle.setForeground(new Color(101, 31, 16));

        headerPanel.add(btnBack);
        headerPanel.add(lblTitle);
        add(headerPanel, BorderLayout.NORTH);
    }

    private void initSplitBody() {
        JPanel splitPanel = new JPanel(new GridLayout(1, 2, 25, 0));
        splitPanel.setOpaque(false);

        // --- LEFT SIDE: ADD MEMBER FORM ---
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

        JLabel lblFormTitle = new JLabel("Register New Member");
        lblFormTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblFormTitle.setForeground(new Color(101, 31, 16));
        gbc.gridx = 0; gbc.gridy = 0;
        formContainer.add(lblFormTitle, gbc);

        gbc.gridy = 1;
        formContainer.add(createFieldLabel("Full Name"), gbc);
        txtFullName = new JTextField();
        txtFullName.setPreferredSize(new Dimension(0, 35));
        gbc.gridy = 2;
        formContainer.add(txtFullName, gbc);

        gbc.gridy = 3;
        formContainer.add(createFieldLabel("Phone Number"), gbc);
        txtPhone = new JTextField();
        txtPhone.setPreferredSize(new Dimension(0, 35));
        gbc.gridy = 4;
        formContainer.add(txtPhone, gbc);

        JButton btnSubmit = new JButton("Add Member To Group") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(46, 117, 89)); // Emerald Green
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
            String name = txtFullName.getText().trim();
            String phone = txtPhone.getText().trim();
            if (name.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all details.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Add row to table model
            int index = tableModel.getRowCount() + 1;
            tableModel.addRow(new Object[]{String.valueOf(index), name, phone, "Active"});
            txtFullName.setText("");
            txtPhone.setText("");
            JOptionPane.showMessageDialog(this, name + " added to group registration roster successfully.");
        });

        gbc.gridy = 5;
        gbc.insets = new Insets(20, 8, 8, 8);
        formContainer.add(btnSubmit, gbc);

        // --- RIGHT SIDE: ACTIVE MEMBERS TABLE VIEW ---
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setOpaque(false);

        JLabel lblTableTitle = new JLabel("Registered Members List");
        lblTableTitle.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblTableTitle.setForeground(new Color(101, 31, 16));
        lblTableTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        tableContainer.add(lblTableTitle, BorderLayout.NORTH);

        String[] cols = {"ID", "Full Name", "Phone", "Status"};
        tableModel = new DefaultTableModel(null, cols);
        // Prepopulating demo data rows
        tableModel.addRow(new Object[]{"1", "Sara Kebede", "0911223344", "Active"});
        tableModel.addRow(new Object[]{"2", "Abel Tesfaye", "0912345678", "Active"});
        tableModel.addRow(new Object[]{"3", "Hana Bekele", "0918765432", "Active"});

        membersTable = new JTable(tableModel);
        membersTable.setRowHeight(38);
        membersTable.setBackground(Color.WHITE);
        membersTable.setShowGrid(false);
        membersTable.getTableHeader().setBackground(new Color(249, 237, 222));
        membersTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));

        JScrollPane sp = new JScrollPane(membersTable);
        sp.setBorder(BorderFactory.createLineBorder(new Color(230, 215, 195)));
        tableContainer.add(sp, BorderLayout.CENTER);

        splitPanel.add(formContainer);
        splitPanel.add(tableContainer);
        add(splitPanel, BorderLayout.CENTER);
    }

    private JLabel createFieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        lbl.setForeground(Color.DARK_GRAY);
        return lbl;
    }
}