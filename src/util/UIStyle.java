package util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class UIStyle {

    // ================= COLORS =================

    public static Color BACKGROUND =
            new Color(245,240,230);

    public static Color DARK_BROWN =
            new Color(60,35,20);

    public static Color COFFEE =
            new Color(92,64,51);

    public static Color GOLD =
            new Color(212,175,55);

    public static Color WHITE =
            Color.WHITE;

    // ================= BUTTON STYLE =================

    public static void styleButton(JButton btn) {

        btn.setFocusPainted(false);

        btn.setBackground(COFFEE);

        btn.setForeground(Color.WHITE);

        btn.setFont(new Font("Segoe UI",
                Font.BOLD,16));

        btn.setPreferredSize(
                new Dimension(180,45));

        btn.setBorder(
                BorderFactory.createEmptyBorder(
                        10,20,10,20
                )
        );
    }

    // ================= TITLE STYLE =================

    public static void styleTitle(JLabel label) {

        label.setFont(
                new Font("Segoe UI",
                        Font.BOLD,32));

        label.setForeground(DARK_BROWN);
    }

    // ================= SUBTITLE STYLE =================

    public static void styleSubtitle(JLabel label) {

        label.setFont(
                new Font("Segoe UI",
                        Font.PLAIN,18));

        label.setForeground(Color.GRAY);
    }

    // ================= PANEL STYLE =================

    public static void styleCard(JPanel panel) {

        panel.setBackground(Color.WHITE);

        panel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(220,210,200)
                        ),
                        new EmptyBorder(20,20,20,20)
                )
        );
    }

    // ================= TABLE STYLE =================

    public static void styleTable(JTable table) {

        table.setRowHeight(35);

        table.setFont(
                new Font("Segoe UI",
                        Font.PLAIN,16));

        JTableHeader header =
                table.getTableHeader();

        header.setFont(
                new Font("Segoe UI",
                        Font.BOLD,16));

        header.setBackground(COFFEE);

        header.setForeground(Color.WHITE);
    }
}
