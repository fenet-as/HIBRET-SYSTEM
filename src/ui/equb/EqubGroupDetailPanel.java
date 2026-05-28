package ui.equb;

import model.SelectedGroup;
import model.EqubGroup;
import ui.MainFrame;

import javax.swing.*;
import java.awt.*;

public class EqubGroupDetailPanel extends JPanel {

    public EqubGroupDetailPanel(MainFrame frame) {

        setLayout(new BorderLayout());

        EqubGroup g =
                SelectedGroup.currentGroup;

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

        JPanel center =
                new JPanel(new GridLayout(5,1));

        center.add(new JLabel(
                "Group Name: " + g.getName()
        ));

        center.add(new JLabel(
                "Contribution: " + g.getContribution()
        ));

        center.add(new JLabel(
                "Members: " + g.getMembersCount()
        ));

        center.add(new JLabel(
                "Current Cycle: " + g.getCurrentCycle()
        ));

        if (g.getCurrentWinner() != null) {

            center.add(new JLabel(
                    "Current Winner: "
                            + g.getCurrentWinner().getName()
            ));
        }

        add(center, BorderLayout.CENTER);

        JPanel bottom =
                new JPanel();

        JButton payment =
                new JButton("Payment");

        JButton payout =
                new JButton("Payout");

        JButton rotation =
                new JButton("Rotation");

        JButton report =
                new JButton("Report");

        JButton back =
                new JButton("Back");

        bottom.add(payment);
        bottom.add(payout);
        bottom.add(rotation);
        bottom.add(report);
        bottom.add(back);

        add(bottom, BorderLayout.SOUTH);

        payment.addActionListener(e ->
                frame.showScreen("payment"));

        payout.addActionListener(e ->
                frame.showScreen("payout"));

        rotation.addActionListener(e ->
                frame.showScreen("rotation"));

        report.addActionListener(e ->
                frame.showScreen("report"));

        back.addActionListener(e ->
                frame.showScreen("home"));
    }
}
