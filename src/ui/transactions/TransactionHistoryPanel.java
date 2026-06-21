package ui.transactions;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

public class TransactionHistoryPanel extends JPanel {
  
  private JTable transactionTable;
  private DefaultTableModel tableModel;
  private JButton exportButton;
  private JButton printButton;
  private JButton refreshButton;
  private JTextField startDateField;
  private JTextField endDateField;
  private JComboBox<String> typeFilterCombo;
  private JComboBox<String> groupFilterCombo;
  private JButton filterButton;
  private JLabel totalLabel;
  
  public TransactionHistoryPanel() {
    setLayout(new BorderLayout());
    setBackground(Color.WHITE);
    
    initializeComponents();
    setupLayout();
    attachListeners();
    loadTransactionData();
  }
  
  private void initializeComponents() {
    String[] columnNames = {"ID", "Date", "Type", "Group", "Member", "Amount", "Description", "Status"};
    tableModel = new DefaultTableModel(columnNames, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };
    
    transactionTable = new JTable(tableModel);
    transactionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    transactionTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    transactionTable.setRowHeight(25);
    
    exportButton = new JButton("Export to CSV");
    printButton = new JButton("Print");
    refreshButton = new JButton("Refresh");
    
    startDateField = new JTextField(10);
    startDateField.setToolTipText("YYYY-MM-DD");
    endDateField = new JTextField(10);
    endDateField.setToolTipText("YYYY-MM-DD");
    typeFilterCombo = new JComboBox<>(new String[]{"All Types", "Deposit", "Withdrawal", "Distribution", "Penalty"});
    groupFilterCombo = new JComboBox<>(new String[]{"All Groups", "Equb Group 1", "Edir Group 1"});
    filterButton = new JButton("Apply Filter");
    totalLabel = new JLabel("Total Transactions: 0");
  }
  
  private void setupLayout() {
    // Filter panel
    JPanel filterPanel = new JPanel();
    filterPanel.setBackground(Color.WHITE);
    filterPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    filterPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    
    filterPanel.add(new JLabel("From:"));
    filterPanel.add(startDateField);
    filterPanel.add(new JLabel("To:"));
    filterPanel.add(endDateField);
    filterPanel.add(new JLabel("Type:"));
    filterPanel.add(typeFilterCombo);
    filterPanel.add(new JLabel("Group:"));
    filterPanel.add(groupFilterCombo);
    filterPanel.add(filterButton);
    filterPanel.add(refreshButton);
    filterPanel.add(Box.createHorizontalGlue());
    filterPanel.add(totalLabel);
    
    // Table panel
    JScrollPane tableScrollPane = new JScrollPane(transactionTable);
    tableScrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
    // Action panel
    JPanel actionPanel = new JPanel();
    actionPanel.setBackground(Color.WHITE);
    actionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    actionPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
    actionPanel.add(exportButton);
    actionPanel.add(printButton);
    
    add(filterPanel, BorderLayout.NORTH);
    add(tableScrollPane, BorderLayout.CENTER);
    add(actionPanel, BorderLayout.SOUTH);
  }
  
  private void attachListeners() {
    filterButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        applyFilter();
      }
    });
    
    refreshButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        refreshData();
      }
    });
    
    exportButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        exportToCSV();
      }
    });
    
    printButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        printTransactions();
      }
    });
  }
  
  private void loadTransactionData() {
    // TODO: Load transactions from database via TransactionService
    tableModel.setRowCount(0);
    
    // Sample data
    Object[] row1 = {1, "2026-06-21", "Deposit", "Equb Group 1", "John Doe", "1000.00", "Monthly contribution", "Completed"};
    Object[] row2 = {2, "2026-06-20", "Distribution", "Equb Group 1", "Jane Smith", "5000.00", "Payout round", "Completed"};
    Object[] row3 = {3, "2026-06-19", "Penalty", "Equb Group 1", "Bob Johnson", "100.00", "Late payment penalty", "Completed"};
    
    tableModel.addRow(row1);
    tableModel.addRow(row2);
    tableModel.addRow(row3);
    
    updateTransactionCount();
  }
  
  private void applyFilter() {
    String startDate = startDateField.getText().trim();
    String endDate = endDateField.getText().trim();
    String type = (String) typeFilterCombo.getSelectedItem();
    String group = (String) groupFilterCombo.getSelectedItem();
    
    // TODO: Filter transactions via TransactionService
    JOptionPane.showMessageDialog(this,
      "Filter applied:\nFrom: " + startDate + "\nTo: " + endDate + "\nType: " + type + "\nGroup: " + group,
      "Filter Applied",
      JOptionPane.INFORMATION_MESSAGE);
  }
  
  private void refreshData() {
    loadTransactionData();
    startDateField.setText("");
    endDateField.setText("");
    typeFilterCombo.setSelectedIndex(0);
    groupFilterCombo.setSelectedIndex(0);
    JOptionPane.showMessageDialog(this, "Transaction history refreshed.", "Refresh", JOptionPane.INFORMATION_MESSAGE);
  }
  
  private void exportToCSV() {
    try {
      JFileChooser fileChooser = new JFileChooser();
      fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
      fileChooser.setSelectedFile(new java.io.File("transactions.csv"));
      
      int result = fileChooser.showSaveDialog(this);
      if (result == JFileChooser.APPROVE_OPTION) {
        // TODO: Export table to CSV
        JOptionPane.showMessageDialog(this,
          "Exported to: " + fileChooser.getSelectedFile().getAbsolutePath(),
          "Export Success",
          JOptionPane.INFORMATION_MESSAGE);
      }
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, "Error exporting: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
  }
  
  private void printTransactions() {
    try {
      transactionTable.print();
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, "Error printing: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
  }
  
  private void updateTransactionCount() {
    int count = tableModel.getRowCount();
    totalLabel.setText("Total Transactions: " + count);
  }
}
