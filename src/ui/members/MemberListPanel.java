package ui.members;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

public class MemberListPanel extends JPanel {
  
  private JTable memberTable;
  private DefaultTableModel tableModel;
  private JButton addButton;
  private JButton editButton;
  private JButton viewButton;
  private JButton deleteButton;
  private JButton refreshButton;
  private JTextField searchField;
  private JButton searchButton;
  private JComboBox<String> statusFilterCombo;
  private JLabel statusLabel;
  
  public MemberListPanel() {
    setLayout(new BorderLayout());
    setBackground(Color.WHITE);
    
    initializeComponents();
    setupLayout();
    attachListeners();
    loadMemberData();
  }
  
  private void initializeComponents() {
    // Create table model with columns
    String[] columnNames = {"ID", "First Name", "Last Name", "Email", "Phone", "Status"};
    tableModel = new DefaultTableModel(columnNames, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false; // Table is read-only
      }
    };
    
    memberTable = new JTable(tableModel);
    memberTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    memberTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    memberTable.getTableHeader().setReorderingAllowed(false);
    memberTable.setRowHeight(25);
    
    // Buttons
    addButton = new JButton("Add Member");
    editButton = new JButton("Edit");
    viewButton = new JButton("View Details");
    deleteButton = new JButton("Delete");
    refreshButton = new JButton("Refresh");
    
    // Search components
    searchField = new JTextField(15);
    searchButton = new JButton("Search");
    statusFilterCombo = new JComboBox<>(new String[]{"All", "Active", "Inactive", "Suspended"});
    statusLabel = new JLabel("Total Members: 0");
  }
  
  private void setupLayout() {
    // Top panel with search and filter
    JPanel topPanel = new JPanel();
    topPanel.setBackground(Color.WHITE);
    topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    topPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    
    topPanel.add(new JLabel("Search:"));
    topPanel.add(searchField);
    topPanel.add(searchButton);
    topPanel.add(new JLabel("  Status:"));
    topPanel.add(statusFilterCombo);
    topPanel.add(refreshButton);
    topPanel.add(Box.createHorizontalGlue());
    topPanel.add(statusLabel);
    
    // Center panel with table
    JScrollPane tableScrollPane = new JScrollPane(memberTable);
    tableScrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
    // Bottom panel with action buttons
    JPanel bottomPanel = new JPanel();
    bottomPanel.setBackground(Color.WHITE);
    bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
    bottomPanel.add(addButton);
    bottomPanel.add(viewButton);
    bottomPanel.add(editButton);
    bottomPanel.add(deleteButton);
    
    add(topPanel, BorderLayout.NORTH);
    add(tableScrollPane, BorderLayout.CENTER);
    add(bottomPanel, BorderLayout.SOUTH);
  }
  
  private void attachListeners() {
    addButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        addNewMember();
      }
    });
    
    editButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        editSelectedMember();
      }
    });
    
    viewButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        viewMemberDetails();
      }
    });
    
    deleteButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        deleteSelectedMember();
      }
    });
    
    refreshButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        refreshList();
      }
    });
    
    searchButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        searchMembers();
      }
    });
    
    statusFilterCombo.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        filterByStatus();
      }
    });
    
    memberTable.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
          viewMemberDetails();
        }
      }
    });
  }
  
  private void loadMemberData() {
    // TODO: Load members from database via MemberService
    // Example implementation:
    tableModel.setRowCount(0);
    
    // Sample data
    Object[] row1 = {1, "John", "Doe", "john@example.com", "0911234567", "Active"};
    Object[] row2 = {2, "Jane", "Smith", "jane@example.com", "0922345678", "Active"};
    Object[] row3 = {3, "Bob", "Johnson", "bob@example.com", "0933456789", "Inactive"};
    
    tableModel.addRow(row1);
    tableModel.addRow(row2);
    tableModel.addRow(row3);
    
    updateMemberCount();
  }
  
  private void addNewMember() {
    // TODO: Navigate to AddMemberPanel
    JOptionPane.showMessageDialog(this, "Navigate to Add Member Panel");
  }
  
  private void editSelectedMember() {
    int selectedRow = memberTable.getSelectedRow();
    if (selectedRow < 0) {
      JOptionPane.showMessageDialog(this, "Please select a member to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
      return;
    }
    
    // TODO: Navigate to EditMemberPanel with selected member data
    int memberId = (Integer) tableModel.getValueAt(selectedRow, 0);
    String firstName = (String) tableModel.getValueAt(selectedRow, 1);
    JOptionPane.showMessageDialog(this, "Edit member: " + firstName, "Edit Member", JOptionPane.INFORMATION_MESSAGE);
  }
  
  private void viewMemberDetails() {
    int selectedRow = memberTable.getSelectedRow();
    if (selectedRow < 0) {
      JOptionPane.showMessageDialog(this, "Please select a member to view.", "No Selection", JOptionPane.WARNING_MESSAGE);
      return;
    }
    
    // TODO: Navigate to MemberDetailPanel with selected member data
    int memberId = (Integer) tableModel.getValueAt(selectedRow, 0);
    JOptionPane.showMessageDialog(this, "View details for member ID: " + memberId, "View Member", JOptionPane.INFORMATION_MESSAGE);
  }
  
  private void deleteSelectedMember() {
    int selectedRow = memberTable.getSelectedRow();
    if (selectedRow < 0) {
      JOptionPane.showMessageDialog(this, "Please select a member to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
      return;
    }
    
    int result = JOptionPane.showConfirmDialog(this,
      "Are you sure you want to delete this member? This action cannot be undone.",
      "Confirm Delete",
      JOptionPane.YES_NO_OPTION,
      JOptionPane.WARNING_MESSAGE);
    
    if (result == JOptionPane.YES_OPTION) {
      try {
        int memberId = (Integer) tableModel.getValueAt(selectedRow, 0);
        // TODO: Delete member via MemberService
        tableModel.removeRow(selectedRow);
        updateMemberCount();
        JOptionPane.showMessageDialog(this, "Member deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
      } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error deleting member: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      }
    }
  }
  
  private void refreshList() {
    loadMemberData();
    searchField.setText("");
    statusFilterCombo.setSelectedIndex(0);
    JOptionPane.showMessageDialog(this, "Member list refreshed.", "Refresh", JOptionPane.INFORMATION_MESSAGE);
  }
  
  private void searchMembers() {
    String searchTerm = searchField.getText().trim();
    if (searchTerm.isEmpty()) {
      JOptionPane.showMessageDialog(this, "Please enter a search term.", "Empty Search", JOptionPane.WARNING_MESSAGE);
      return;
    }
    
    // TODO: Search members via MemberService
    JOptionPane.showMessageDialog(this, "Searching for: " + searchTerm, "Search", JOptionPane.INFORMATION_MESSAGE);
  }
  
  private void filterByStatus() {
    String selectedStatus = (String) statusFilterCombo.getSelectedItem();
    // TODO: Filter members by status via MemberService
    JOptionPane.showMessageDialog(this, "Filtering by status: " + selectedStatus, "Filter", JOptionPane.INFORMATION_MESSAGE);
  }
  
  private void updateMemberCount() {
    int count = tableModel.getRowCount();
    statusLabel.setText("Total Members: " + count);
  }
}
