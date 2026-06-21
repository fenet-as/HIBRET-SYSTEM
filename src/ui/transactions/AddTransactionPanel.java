package ui.transactions;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;

public class AddTransactionPanel extends JPanel {
  
  private JComboBox<String> transactionTypeCombo;
  private JComboBox<String> groupCombo;
  private JComboBox<String> memberCombo;
  private JTextField amountField;
  private JTextArea descriptionArea;
  private JButton addButton;
  private JButton clearButton;
  private JButton cancelButton;
  private JLabel messageLabel;
  
  public AddTransactionPanel() {
    setLayout(new BorderLayout());
    setBackground(Color.WHITE);
    
    initializeComponents();
    setupLayout();
    attachListeners();
  }
  
  private void initializeComponents() {
    transactionTypeCombo = new JComboBox<>(new String[]{"Deposit", "Withdrawal", "Distribution", "Penalty"});
    groupCombo = new JComboBox<>(new String[]{"Select Group", "Equb Group 1", "Edir Group 1"});
    memberCombo = new JComboBox<>(new String[]{"Select Member"});
    
    amountField = new JTextField(20);
    descriptionArea = new JTextArea(5, 20);
    descriptionArea.setLineWrap(true);
    descriptionArea.setWrapStyleWord(true);
    
    addButton = new JButton("Record Transaction");
    clearButton = new JButton("Clear");
    cancelButton = new JButton("Cancel");
    
    messageLabel = new JLabel();
    messageLabel.setForeground(Color.RED);
  }
  
  private void setupLayout() {
    JPanel formPanel = new JPanel();
    formPanel.setLayout(new GridBagLayout());
    formPanel.setBackground(Color.WHITE);
    formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(10, 10, 10, 10);
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.anchor = GridBagConstraints.WEST;
    
    JLabel titleLabel = new JLabel("Record New Transaction");
    titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    formPanel.add(titleLabel, gbc);
    gbc.gridwidth = 1;
    
    gbc.gridy++;
    formPanel.add(new JLabel("Transaction Type:"), gbc);
    gbc.gridx = 1;
    formPanel.add(transactionTypeCombo, gbc);
    gbc.gridx = 0;
    
    gbc.gridy++;
    formPanel.add(new JLabel("Group:"), gbc);
    gbc.gridx = 1;
    formPanel.add(groupCombo, gbc);
    gbc.gridx = 0;
    
    gbc.gridy++;
    formPanel.add(new JLabel("Member:"), gbc);
    gbc.gridx = 1;
    formPanel.add(memberCombo, gbc);
    gbc.gridx = 0;
    
    gbc.gridy++;
    formPanel.add(new JLabel("Amount (ETB):"), gbc);
    gbc.gridx = 1;
    formPanel.add(amountField, gbc);
    gbc.gridx = 0;
    
    gbc.gridy++;
    formPanel.add(new JLabel("Description:"), gbc);
    gbc.gridx = 1;
    gbc.gridheight = 3;
    formPanel.add(new JScrollPane(descriptionArea), gbc);
    gbc.gridx = 0;
    gbc.gridheight = 1;
    
    gbc.gridy += 3;
    gbc.gridwidth = 2;
    formPanel.add(messageLabel, gbc);
    gbc.gridwidth = 1;
    
    JPanel buttonPanel = new JPanel();
    buttonPanel.setBackground(Color.WHITE);
    buttonPanel.add(addButton);
    buttonPanel.add(clearButton);
    buttonPanel.add(cancelButton);
    
    gbc.gridy++;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.CENTER;
    formPanel.add(buttonPanel, gbc);
    
    JScrollPane scrollPane = new JScrollPane(formPanel);
    scrollPane.getVerticalScrollBar().setUnitIncrement(16);
    add(scrollPane, BorderLayout.CENTER);
  }
  
  private void attachListeners() {
    addButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        recordTransaction();
      }
    });
    
    clearButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        clearFields();
      }
    });
    
    cancelButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        cancelTransaction();
      }
    });
    
    groupCombo.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        loadMembersForGroup();
      }
    });
  }
  
  private void recordTransaction() {
    if (!validateInput()) {
      return;
    }
    
    try {
      String transactionType = (String) transactionTypeCombo.getSelectedItem();
      String group = (String) groupCombo.getSelectedItem();
      String member = (String) memberCombo.getSelectedItem();
      BigDecimal amount = new BigDecimal(amountField.getText().trim());
      String description = descriptionArea.getText().trim();
      
      // TODO: Save transaction via TransactionService
      messageLabel.setForeground(new Color(34, 139, 34));
      messageLabel.setText("Transaction recorded successfully!");
      clearFields();
      
    } catch (NumberFormatException e) {
      messageLabel.setForeground(Color.RED);
      messageLabel.setText("Invalid amount format. Please enter a valid number.");
    } catch (Exception e) {
      messageLabel.setForeground(Color.RED);
      messageLabel.setText("Error recording transaction: " + e.getMessage());
    }
  }
  
  private boolean validateInput() {
    String amount = amountField.getText().trim();
    String group = (String) groupCombo.getSelectedItem();
    
    messageLabel.setForeground(Color.RED);
    
    if (group.equals("Select Group")) {
      messageLabel.setText("Please select a group.");
      return false;
    }
    
    if (amount.isEmpty()) {
      messageLabel.setText("Amount is required.");
      return false;
    }
    
    try {
      BigDecimal bd = new BigDecimal(amount);
      if (bd.compareTo(BigDecimal.ZERO) <= 0) {
        messageLabel.setText("Amount must be greater than zero.");
        return false;
      }
    } catch (NumberFormatException e) {
      messageLabel.setText("Invalid amount format.");
      return false;
    }
    
    return true;
  }
  
  private void loadMembersForGroup() {
    // TODO: Load members for selected group via MemberService
  }
  
  private void clearFields() {
    transactionTypeCombo.setSelectedIndex(0);
    groupCombo.setSelectedIndex(0);
    memberCombo.setSelectedIndex(0);
    amountField.setText("");
    descriptionArea.setText("");
    messageLabel.setText("");
  }
  
  private void cancelTransaction() {
    clearFields();
    messageLabel.setText("");
    // TODO: Navigate back
  }
}
