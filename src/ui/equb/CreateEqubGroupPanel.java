package ui.equb;
import service.impl.EqubServiceImpl;
import service.impl.EqubServiceImpl;

import ui.MainFrame;

import util.Validator;

import javax.swing.*;
import java.awt.*;

public class CreateEqubGroupPanel extends JPanel {

    public CreateEqubGroupPanel(MainFrame frame) {

        setLayout(new GridLayout(4, 2));

        JTextField nameField =
                new JTextField();

        JTextField contField =
                new JTextField();

        add(new JLabel("Group Name:"));
        add(nameField);

        add(new JLabel("Contribution Amount:"));
        add(contField);

        JButton btnSubmit =
                new JButton("Create Group");

        add(btnSubmit);

        btnSubmit.addActionListener(e -> {

            String name = nameField.getText();
            String amountStr = contField.getText();

            if (!Validator.isEmpty(name)
                    && Validator.isPositive(amountStr)) {

                double amount =
                        Double.parseDouble(amountStr);

                EqubServiceImpl service =
                        new EqubServiceImpl();

                service.createGroup(name, amount);

                JOptionPane.showMessageDialog(this,
                        "Group Created Successfully");

                frame.showScreen("home");

            } else {

                JOptionPane.showMessageDialog(this,
                        "Invalid Input");
            }
        });
    }
}