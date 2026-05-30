import ui.auth.LoginFrame;
import javax.swing.*;
import java.awt.*;

public class Main {

  public static void main(String[] args) {
    // 1. Detect Operating System to identify the ideal native Amharic font asset
    String os = System.getProperty("os.name").toLowerCase();
    String fontName;

    if (os.contains("win")) {
      fontName = "Nyala";         // Standard built-in Windows Amharic Font
    } else if (os.contains("mac")) {
      fontName = "Kefa";          // Standard built-in macOS Amharic Font
    } else {
      fontName = "Abyssinica SIL"; // Common Linux fallback
    }

    // 2. Set up cross-platform look and feel to properly draw round corners and custom buttons
    try {
      UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
    } catch (Exception e) {
      System.err.println("Could not initialize custom Look and Feel template wrapper.");
    }

    // 3. Adjust specific global components without breaking native symbol rendering blocks
    UIManager.put("TableHeader.font", new Font(fontName, Font.BOLD, 13));
    UIManager.put("Table.font", new Font(fontName, Font.PLAIN, 13));
    UIManager.put("OptionPane.messageFont", new Font(fontName, Font.PLAIN, 14));
    UIManager.put("OptionPane.buttonFont", new Font(fontName, Font.BOLD, 13));

    // 4. Safely run your GUI frame inside the Event Dispatch Thread (EDT)
    SwingUtilities.invokeLater(() -> {
      LoginFrame frame = new LoginFrame();
      frame.setVisible(true);
    });
  }
}