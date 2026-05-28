package ui.equb;

import model.SelectedGroup;
import model.EqubGroup;

import service.impl.EqubServiceImpl;

import ui.MainFrame;

import javax.swing.*;
import java.awt.*;

public class EqubPayoutPanel extends JPanel {

    public EqubPayoutPanel(MainFrame frame) {

        setLayout(new BorderLayout());

        EqubGroup g = SelectedGroup.currentGroup;

        if (g == null) {

            add(
                    new JLabel(
                            "No group selected",
                            SwingConstants.CENTER
                    ),
                    BorderLayout.CENTER
            );

            return;
        }

        JLabel info = new JLabel(
                "Payout to: " + g.getCurrentWinner().getName(),
                SwingConstants.CENTER
        );

        add(info, BorderLayout.CENTER);

        JButton btnConfirm =
                new JButton("Confirm Payout");

        add(btnConfirm, BorderLayout.SOUTH);

        btnConfirm.addActionListener(e -> {

            EqubServiceImpl service =
                    new EqubServiceImpl();

            service.executePayout(g);

            JOptionPane.showMessageDialog(
                    this,
                    "Payout completed"
            );

            frame.showScreen("detail");
        });
    }
}
