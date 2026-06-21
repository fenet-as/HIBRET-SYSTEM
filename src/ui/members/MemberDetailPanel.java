package ui.members;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MemberDetailPanel extends JPanel {
  
  private int memberId;
  private JLabel memberIdLabel;
  private JLabel firstNameLabel;
  private JLabel lastNameLabel;
  private JLabel emailLabel;
  private JLabel phoneLabel;
  private JLabel addressLabel;
  private JLabel statusLabel;
  private JLabel createdDateLabel;
  private JLabel updatedDateLabel;
  private JButton editButton;
  private JButton deleteButton;
  private JButton backButton;
  
  public MemberDetailPanel() {
    setLayout(new BorderLayout());
    setBackground(Color.WHITE);
    
    initializeComponents();
    setupLayout();
    attachListeners();
  }
  
  public void loadMemberDetails(int id, String firstName, String lastName, String email,
                               String phone, String address, String status,
                               String createdDate, String updatedDate) {
    this.memberId = id;
    memberIdLabel.setText("ID: " + id);
    firstNameLabel.setText(firstName);
    lastNameLabel.setText(lastName);
    emailLabel.setText(email);
    phoneLabel.setText(phone);
    addressLabel.setText(address);
    statusLabel.setText(status);
    createdDateLabel.setText(createdDate);
    updatedDateLabel.setText(updatedDate);
  }
  
  private void initializeComponents() {
    memberIdLabel = new JLabel("ID: ");
    firstNameLabel = new JLabel("");
    lastNameLabel = new JLabel("");
    emailLabel = new JLabel("");
    phoneLabel = new JLabel("");
    addressLabel = new JLabel("");
    statusLabel = new JLabel("");
    createdDateLabel = new JLabel("");
    updatedDateLabel = new JLabel("");
    
    editButton = new JButton("Edit Member");
    deleteButton = new JButton("Delete Member");
    backButton = new JButton("Back to List");
  }
  
  private void setupLayout() {
    JPanel detailPanel = new JPanel();
    detailPanel.setLayout(new GridBagLayout());
    detailPanel.setBackground(Color.WHITE);
    detailPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(10, 10, 10, 10);
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.anchor = GridBagConstraints.WEST;
    
    // Title
    JLabel titleLabel = new JLabel("Member Details");
    titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    detailPanel.add(titleLabel, gbc);
    gbc.gridwidth = 1;
    
    // Member ID
    gbc.gridy++;
    JLabel idHeaderLabel = new JLabel("Member ID:");
    idHeaderLabel.setFont(new Font("Arial", Font.BOLD, 12));
    detailPanel.add(idHeaderLabel, gbc);
    gbc.gridx = 1;
    memberIdLabel.setFont(new Font("Arial", Font.PLAIN, 12));
    detailPanel.add(memberIdLabel, gbc);
    gbc.gridx = 0;
    
    // First Name
    gbc.gridy++;
    JLabel fNameHeaderLabel = new JLabel("First Name:");
    fNameHeaderLabel.setFont(new Font("Arial", Font.BOLD, 12));
    detailPanel.add(fNameHeaderLabel, gbc);
    gbc.gridx = 1;
    firstNameLabel.setFont(new Font("Arial", Font.PLAIN, 12));
    detailPanel.add(firstNameLabel, gbc);
    gbc.gridx = 0;
    
    // Last Name
    gbc.gridy++;
    JLabel lNameHeaderLabel = new JLabel("Last Name:");
    lNameHeaderLabel.setFont(new Font("Arial", Font.BOLD, 12));
    detailPanel.add(lNameHeaderLabel, gbc);
    gbc.gridx = 1;
    lastNameLabel.setFont(new Font("Arial", Font.PLAIN, 12));
    detailPanel.add(lastNameLabel, gbc);
    gbc.gridx = 0;
    
    // Email
    gbc.gridy++;
    JLabel emailHeaderLabel = new JLabel("Email:");
    emailHeaderLabel.setFont(new Font("Arial", Font.BOLD, 12));
    detailPanel.add(emailHeaderLabel, gbc);
    gbc.gridx = 1;
    emailLabel.setFont(new Font("Arial", Font.PLAIN, 12));
    detailPanel.add(emailLabel, gbc);
    gbc.gridx = 0;
    
    // Phone
    gbc.gridy++;
    JLabel phoneHeaderLabel = new JLabel("Phone Number:");
    phoneHeaderLabel.setFont(new Font("Arial", Font.BOLD, 12));
    detailPanel.add(phoneHeaderLabel, gbc);
    gbc.gridx = 1;
    phoneLabel.setFont(new Font("Arial", Font.PLAIN, 12));
    detailPanel.add(phoneLabel, gbc);
    gbc.gridx = 0;
    
    // Address
    gbc.gridy++;
    JLabel addressHeaderLabel = new JLabel("Address:");
    addressHeaderLabel.setFont(new Font("Arial", Font.BOLD, 12));
    detailPanel.add(addressHeaderLabel, gbc);
    gbc.gridx = 1;
    addressLabel.setFont(new Font("Arial", Font.PLAIN, 12));
    detailPanel.add(addressLabel, gbc);
    gbc.gridx = 0;
    
    // Status
    gbc.gridy++;
    JLabel statusHeaderLabel = new JLabel("Status:");
    statusHeaderLabel.setFont(new Font("Arial", Font.BOLD, 12));
    detailPanel.add(statusHeaderLabel, gbc);
    gbc.gridx = 1;
    statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
    statusLabel.setForeground(new Color(34, 139, 34));
    detailPanel.add(statusLabel, gbc);
    gbc.gridx = 0;
    
    // Created Date
    gbc.gridy++;
    JLabel createdHeaderLabel = new JLabel("Created Date:");
    createdHeaderLabel.setFont(new Font("Arial", Font.BOLD, 12));
    detailPanel.add(createdHeaderLabel, gbc);
    gbc.gridx = 1;
    createdDateLabel.setFont(new Font("Arial", Font.PLAIN, 11));
    createdDateLabel.setForeground(new Color(128, 128, 128));
    detailPanel.add(createdDateLabel, gbc);
    gbc.gridx = 0;
    
    // Updated Date
    gbc.gridy++;
    JLabel updatedHeaderLabel = new JLabel("Last Updated:");
    updatedHeaderLabel.setFont(new Font("Arial", Font.BOLD, 12));
    detailPanel.add(updatedHeaderLabel, gbc);
    gbc.gridx = 1;
    updatedDateLabel.setFont(new Font("Arial", Font.PLAIN, 11));
    updatedDateLabel.setForeground(new Color(128, 128, 128));
    detailPanel.add(updatedDateLabel, gbc);
    gbc.gridx = 0;
    
    // Buttons
    JPanel buttonPanel = new JPanel();
    buttonPanel.setBackground(Color.WHITE);
    buttonPanel.add(editButton);
    buttonPanel.add(deleteButton);
    buttonPanel.add(backButton);
    
    gbc.gridy++;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.CENTER;
    gbc.insets = new Insets(30, 10, 10, 10);
    detailPanel.add(buttonPanel, gbc);
    
    JScrollPane scrollPane = new JScrollPane(detailPanel);
    scrollPane.getVerticalScrollBar().setUnitIncrement(16);
    add(scrollPane, BorderLayout.CENTER);
  }
  
  private void attachListeners() {
    editButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        editMember();
      }
    });
    
    deleteButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        deleteMember();
      }
    });
    
    backButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        goBackToList();
      }
    });
  }
  
  private void editMember() {
    // TODO: Navigate to EditMemberPanel with this member's data
  }
  
  private void deleteMember() {
    int result = JOptionPane.showConfirmDialog(this,
      "Are you sure you want to delete this member? This action cannot be undone.",
      "Confirm Delete",
      JOptionPane.YES_NO_OPTION,
      JOptionPane.WARNING_MESSAGE);
    
    if (result == JOptionPane.YES_OPTION) {
      try {
        // TODO: Delete member via MemberService
        JOptionPane.showMessageDialog(this,
          "Member deleted successfully.",
          "Success",
          JOptionPane.INFORMATION_MESSAGE);
        goBackToList();
      } catch (Exception e) {
        JOptionPane.showMessageDialog(this,
          "Error deleting member: " + e.getMessage(),
          "Error",
          JOptionPane.ERROR_MESSAGE);
      }
    }
  }
  
  private void goBackToList() {
    // TODO: Navigate back to MemberListPanel
  }
}
