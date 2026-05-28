
import ui.MainFrame;
import ui.auth.LoginFrame;


import javax.swing.*;

public class Main {

  public static void main(String[] args) {

    SwingUtilities.invokeLater(() -> {
      LoginFrame frame = new LoginFrame();
      frame.setVisible(true);
    });



  }
}