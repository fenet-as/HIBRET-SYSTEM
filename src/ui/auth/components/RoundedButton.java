

package ui.auth.components;

import javax.swing.*;
import java.awt.*;

public class RoundedButton extends JButton {
    private final Color backgroundColor;

    public RoundedButton(String label, Color bg) {
        super(label);
        this.backgroundColor = bg;
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(backgroundColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
        super.paintComponent(g2);
        g2.dispose();
    }
}

