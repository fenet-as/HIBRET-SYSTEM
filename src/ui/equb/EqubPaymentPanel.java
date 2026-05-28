package ui.equb;

import model.SelectedGroup;
import model.EqubGroup;

import service.impl.EqubServiceImpl;

import ui.MainFrame;

import util.Validator;

import javax.swing.*;
import java.awt.*;

public class EqubPaymentPanel extends JPanel {

    public EqubPaymentPanel(MainFrame frame) {

        setLayout(new GridLayout(3, 2));

        JTextField memberName =
                new JTextField();

        JTextField amount =
                new JTextField();

        add(new JLabel("Member Name:"));
        add(memberName);

        add(new JLabel("Amount:"));
        add(amount);

        JButton btnSave =
                new JButton("Save Payment");

        add(btnSave);

        btnSave.addActionListener(e -> {

            EqubGroup g =
                    SelectedGroup.currentGroup;

            if (g == null) {

                JOptionPane.showMessageDialog(
                        this,
                        "No group selected"
                );

                return;
            }

            if (Validator.isEmpty(memberName.getText())
                    || !Validator.isPositiveNumber(amount.getText())) {

                JOptionPane.showMessageDialog(
                        this,
                        "Check inputs"
                );

                return;
            }

            EqubServiceImpl service =
                    new EqubServiceImpl();

            service.makePayment(
                    g,
                    memberName.getText(),
                    Double.parseDouble(amount.getText())
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Payment saved"
            );

            frame.showScreen("detail");
        });
    }
}
