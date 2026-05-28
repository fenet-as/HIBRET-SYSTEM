package ui.equb;

import model.SelectedGroup;
import model.EqubGroup;

import service.impl.EqubServiceImpl;

import ui.MainFrame;

import javax.swing.*;
import java.awt.*;

public class EqubRotationPanel extends JPanel {

    public EqubRotationPanel(MainFrame frame) {

        setLayout(new BorderLayout());

        JButton btnNext = new JButton("Next Rotation");

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

        add(btnNext, BorderLayout.SOUTH);

        btnNext.addActionListener(e -> {

            EqubServiceImpl service =
                    new EqubServiceImpl();

            service.nextRotation(g);

            JOptionPane.showMessageDialog(
                    this,
                    "Moved to next cycle"
            );

            frame.showScreen("detail");
        });
    }
}

