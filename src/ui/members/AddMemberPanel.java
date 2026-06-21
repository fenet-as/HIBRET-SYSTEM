package ui.members;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AddMemberPanel extends JPanel {
  
  private JTextField firstNameField;
  private JTextField lastNameField;
  private JTextField emailField;
  private JTextField phoneField;
  private JTextField addressField;
  private JComboBox<String> memberStatusCombo;
  private JButton addButton;
  private JButton clearButton;
  private JButton cancelButton;
  private JLabel messageLabel;
  
  public AddMemberPanel() {
    setLayout(new BorderLayout());
    setBackground(Color.WHITE);
    
    initializeComponents();
    setupLayout();
    attachListeners();
  }
  
  private void initializeComponents() {
    // Input fields
    firstNameField = new JTextField(20);
    lastNameField = new JTextField(20);
    emailField = new JTextField(20);
    phoneField = new JTextField(20);
    addressField = new JTextField(20);
    
    // Combo box for member status
    memberStatusCombo = new JComboBox<>(new String[]{"Active", "Inactive", "Suspended"});
    memberStatusCombo.setSelectedIndex(0);
    
    // Buttons
    addButton = new JButton("Add Member");
    clearButton = new JButton("Clear");
    cancelButton = new JButton("Cancel");
    
    // Message label for feedback
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
    
    // Title
    JLabel titleLabel = new JLabel("Add New Member");
    titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    formPanel.add(titleLabel, gbc);
    gbc.gridwidth = 1;
    
    // First Name
    gbc.gridy++;
    formPanel.add(new JLabel("First Name:"), gbc);
    gbc.gridx = 1;
    formPanel.add(firstNameField, gbc);
    gbc.gridx = 0;
    
    // Last Name
    gbc.gridy++;
    formPanel.add(new JLabel("Last Name:"), gbc);
    gbc.gridx = 1;
    formPanel.add(lastNameField, gbc);
    gbc.gridx = 0;
    
    // Email
    gbc.gridy++;
    formPanel.add(new JLabel("Email:"), gbc);
    gbc.gridx = 1;
    formPanel.add(emailField, gbc);
    gbc.gridx = 0;
    
    // Phone
    gbc.gridy++;
    formPanel.add(new JLabel("Phone Number:"), gbc);
    gbc.gridx = 1;
    formPanel.add(phoneField, gbc);
    gbc.gridx = 0;
    
    // Address
    gbc.gridy++;
    formPanel.add(new JLabel("Address:"), gbc);
    gbc.gridx = 1;
    formPanel.add(addressField, gbc);
    gbc.gridx = 0;
    
    // Member Status
    gbc.gridy++;
    formPanel.add(new JLabel("Member Status:"), gbc);
    gbc.gridx = 1;
    formPanel.add(memberStatusCombo, gbc);
    gbc.gridx = 0;
    
    // Message Label
    gbc.gridy++;
    gbc.gridwidth = 2;
    formPanel.add(messageLabel, gbc);
    gbc.gridwidth = 1;
    
    // Buttons Panel
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
        addNewMember();
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
        cancelAddMember();
      }
    });
  }
  
  private void addNewMember() {
    // Validate input
    if (!validateInput()) {
      return;
    }
    
    try {
      // Collect form data
      String firstName = firstNameField.getText().trim();
      String lastName = lastNameField.getText().trim();
      String email = emailField.getText().trim();
      String phone = phoneField.getText().trim();
      String address = addressField.getText().trim();
      String status = (String) memberStatusCombo.getSelectedItem();
      
      // TODO: Save member data to database via MemberService
      // Example: memberService.addMember(firstName, lastName, email, phone, address, status);
      
      messageLabel.setForeground(new Color(34, 139, 34)); // Forest green
      messageLabel.setText("Member added successfully!");
      
      // Clear fields after successful addition
      clearFields();
      
    } catch (Exception e) {
      messageLabel.setForeground(Color.RED);
      messageLabel.setText("Error adding member: " + e.getMessage());
      e.printStackTrace();
    }
  }
  
  private boolean validateInput() {
    String firstName = firstNameField.getText().trim();
    String lastName = lastNameField.getText().trim();
    String email = emailField.getText().trim();
    String phone = phoneField.getText().trim();
    
    messageLabel.setForeground(Color.RED);
    
    // Check required fields
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
    
    // Basic email format validation
    if (!isValidEmail(email)) {
      messageLabel.setText("Invalid email format. Please enter a valid email address.");
      return false;
    }
    
    if (phone.isEmpty()) {
      messageLabel.setText("Phone number is required.");
      return false;
    }
    
    // Basic phone format validation (should be numeric and 10+ digits)
    if (!isValidPhoneNumber(phone)) {
      messageLabel.setText("Invalid phone number format. Please enter at least 10 digits.");
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
  
  private void clearFields() {
    firstNameField.setText("");
    lastNameField.setText("");
    emailField.setText("");
    phoneField.setText("");
    addressField.setText("");
    memberStatusCombo.setSelectedIndex(0);
    messageLabel.setText("");
  }
  
  private void cancelAddMember() {
    clearFields();
    messageLabel.setText("");
    // TODO: Navigate back to member list or previous panel
  }
}
