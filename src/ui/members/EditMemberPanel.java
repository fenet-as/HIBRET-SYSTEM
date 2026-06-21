package ui.members;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class EditMemberPanel extends JPanel {
  
  private int memberId;
  private JTextField firstNameField;
  private JTextField lastNameField;
  private JTextField emailField;
  private JTextField phoneField;
  private JTextField addressField;
  private JComboBox<String> memberStatusCombo;
  private JButton updateButton;
  private JButton clearButton;
  private JButton cancelButton;
  private JLabel messageLabel;
  
  public EditMemberPanel() {
    setLayout(new BorderLayout());
    setBackground(Color.WHITE);
    
    initializeComponents();
    setupLayout();
    attachListeners();
  }
  
  public void setMemberData(int id, String firstName, String lastName, String email, 
                            String phone, String address, String status) {
    this.memberId = id;
    firstNameField.setText(firstName);
    lastNameField.setText(lastName);
    emailField.setText(email);
    phoneField.setText(phone);
    addressField.setText(address);
    memberStatusCombo.setSelectedItem(status);
  }
  
  private void initializeComponents() {
    firstNameField = new JTextField(20);
    lastNameField = new JTextField(20);
    emailField = new JTextField(20);
    phoneField = new JTextField(20);
    addressField = new JTextField(20);
    
    memberStatusCombo = new JComboBox<>(new String[]{"Active", "Inactive", "Suspended"});
    memberStatusCombo.setSelectedIndex(0);
    
    updateButton = new JButton("Update Member");
    clearButton = new JButton("Reset");
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
    
    JLabel titleLabel = new JLabel("Edit Member (ID: " + memberId + ")");
    titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    formPanel.add(titleLabel, gbc);
    gbc.gridwidth = 1;
    
    gbc.gridy++;
    formPanel.add(new JLabel("First Name:"), gbc);
    gbc.gridx = 1;
    formPanel.add(firstNameField, gbc);
    gbc.gridx = 0;
    
    gbc.gridy++;
    formPanel.add(new JLabel("Last Name:"), gbc);
    gbc.gridx = 1;
    formPanel.add(lastNameField, gbc);
    gbc.gridx = 0;
    
    gbc.gridy++;
    formPanel.add(new JLabel("Email:"), gbc);
    gbc.gridx = 1;
    formPanel.add(emailField, gbc);
    gbc.gridx = 0;
    
    gbc.gridy++;
    formPanel.add(new JLabel("Phone Number:"), gbc);
    gbc.gridx = 1;
    formPanel.add(phoneField, gbc);
    gbc.gridx = 0;
    
    gbc.gridy++;
    formPanel.add(new JLabel("Address:"), gbc);
    gbc.gridx = 1;
    formPanel.add(addressField, gbc);
    gbc.gridx = 0;
    
    gbc.gridy++;
    formPanel.add(new JLabel("Member Status:"), gbc);
    gbc.gridx = 1;
    formPanel.add(memberStatusCombo, gbc);
    gbc.gridx = 0;
    
    gbc.gridy++;
    gbc.gridwidth = 2;
    formPanel.add(messageLabel, gbc);
    gbc.gridwidth = 1;
    
    JPanel buttonPanel = new JPanel();
    buttonPanel.setBackground(Color.WHITE);
    buttonPanel.add(updateButton);
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
    updateButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        updateMember();
      }
    });
    
    clearButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        resetFields();
      }
    });
    
    cancelButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        cancelEdit();
      }
    });
  }
  
  private void updateMember() {
    if (!validateInput()) {
      return;
    }
    
    try {
      String firstName = firstNameField.getText().trim();
      String lastName = lastNameField.getText().trim();
      String email = emailField.getText().trim();
      String phone = phoneField.getText().trim();
      String address = addressField.getText().trim();
      String status = (String) memberStatusCombo.getSelectedItem();
      
      // TODO: Update member in database via MemberService
      // Example: memberService.updateMember(memberId, firstName, lastName, email, phone, address, status);
      
      messageLabel.setForeground(new Color(34, 139, 34));
      messageLabel.setText("Member updated successfully!");
      
    } catch (Exception e) {
      messageLabel.setForeground(Color.RED);
      messageLabel.setText("Error updating member: " + e.getMessage());
    }
  }
  
  private boolean validateInput() {
    String firstName = firstNameField.getText().trim();
    String lastName = lastNameField.getText().trim();
    String email = emailField.getText().trim();
    String phone = phoneField.getText().trim();
    
    messageLabel.setForeground(Color.RED);
    
    if (firstName.isEmpty()) {
      messageLabel.setText("First name is required.");
      return false;
    }
    
    if (lastName.isEmpty()) {
      messageLabel.setText("Last name is required.");
      return false;
    }
    
    if (email.isEmpty()) {
      messageLabel.setText("Email is required.");
      return false;
    }
    
    if (!isValidEmail(email)) {
      messageLabel.setText("Invalid email format.");
      return false;
    }
    
    if (phone.isEmpty()) {
      messageLabel.setText("Phone number is required.");
      return false;
    }
    
    if (!isValidPhoneNumber(phone)) {
      messageLabel.setText("Invalid phone number format.");
      return false;
    }
    
    return true;
  }
  
  private boolean isValidEmail(String email) {
    return email.contains("@") && email.contains(".") && email.length() > 5;
  }
  
  private boolean isValidPhoneNumber(String phone) {
    String phoneDigitsOnly = phone.replaceAll("[^0-9]", "");
    return phoneDigitsOnly.length() >= 10;
  }
  
  private void resetFields() {
    // Reset to currently stored values - would reload from service in real implementation
    messageLabel.setText("");
  }
  
  private void cancelEdit() {
    messageLabel.setText("");
    // TODO: Navigate back to member list
  }
}
