package zwei.ui;

import zwei.ui.mediator.SplashInterface;

import javax.swing.*;

/**
 * Created on 2018-12-10
 *
 * @author 九条涼果 chunxiang.huang@hypers.com
 */
public final class Main {

  public static void main(String[] args) throws Exception {
    System.setProperty("apple.laf.useScreenMenuBar", "true");
    System.setProperty("apple.awt.application.name", "成绩管理");
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    SwingUtilities.invokeAndWait(Main::createAndShowGUI);
  }

  private static void createAndShowGUI() {
    JFrame frame = new JFrame("成绩管理");
    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    SplashInterface splashInterface = new SplashInterface();
    splashInterface.showInFrame(frame);
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }
}
