package zwei.ui;

import zwei.dao.JDBCUtilities;
import zwei.ui.mediator.SplashInterface;
import zwei.ui.mediator.UserInterface;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

/**
 * Created on 2018-12-10
 *
 * @author 九条涼果 chunxiang.huang@hypers.com
 */
public final class Main {

  public static void main(String[] args) throws Exception {
    String propertyFile = args.length == 0 ? "app.properties" : args[0];

    /*连接到数据库*/
    try {
      JDBCUtilities.getInstance(propertyFile);
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Problem reading properties file " + propertyFile);
      return;
    }

    /*macOS的特别参数，把菜单栏放到全局菜单栏*/
    System.setProperty("apple.laf.useScreenMenuBar", "true");
    /*macOS的特别参数，设置菜单栏的显示文字*/
    System.setProperty("apple.awt.application.name", "成绩管理");
    /*macOS的特别参数，按⌘+Q时完全退出*/
    System.setProperty("apple.eawt.quitStrategy", "CLOSE_ALL_WINDOWS");
    /*设置主题为系统默认主题*/
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    setDockIcon();
    /*启动UI线程*/
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

  /** 在macOS上设置Dock图标 */
  private static void setDockIcon() {
    try {
      Class.forName("com.apple.eawt.Application");
    } catch (ClassNotFoundException e) {
      return;
    }
    URL resource = Main.class.getClassLoader().getResource("Icon_for_ryoka.png");

    if (resource != null) {
      try {
        BufferedImage image = ImageIO.read(resource);
        com.apple.eawt.Application.getApplication().setDockIconImage(image);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
