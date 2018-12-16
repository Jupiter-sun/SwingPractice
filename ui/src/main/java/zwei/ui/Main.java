package zwei.ui;

import zwei.JDBCUtilities;
import zwei.ui.mediator.SplashInterface;
import zwei.ui.mediator.UserInterface;

import javax.swing.*;

/**
 * Created on 2018-12-10
 *
 * @author 九条涼果 chunxiang.huang@hypers.com
 */
public final class Main {

  public static void main(String[] args) throws Exception {
    String propertyFile = args.length == 0 ? "app.properties" : args[0];

    try {
      JDBCUtilities.getInstance(propertyFile);
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Problem reading properties file " + propertyFile);
      return;
    }

    System.setProperty("apple.laf.useScreenMenuBar", "true");
    System.setProperty("apple.awt.application.name", "成绩管理");
    System.setProperty("apple.eawt.quitStrategy", "CLOSE_ALL_WINDOWS");
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    SwingUtilities.invokeAndWait(Main::createAndShowGUI);
  }

  private static void createAndShowGUI() {
    JFrame frame = new JFrame();
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    UserInterface splashInterface = new SplashInterface();
    splashInterface.showInFrame(frame);
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
    frame.requestFocus();
  }
}
