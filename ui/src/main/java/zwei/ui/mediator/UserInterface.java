package zwei.ui.mediator;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * 代表一个可以在JFrame内显示的组件
 * Created on 2018-12-12
 *
 * @author 九条涼果 chunxiang.huang@hypers.com
 */
@FunctionalInterface
public interface UserInterface {

  /**
   * 将当前组件显示在JFrame中。
   * Note: call after {@link #putArgument(String, Object)}
   */
  void showInFrame(JFrame parent);

  /**
   * Transfer arguments to panel.
   * Inspired by Android Bundle mechanism
   * Note: call after constructor, obviously
   */
  default void putArgument(String key, Object value) { }

  /** 舒服的透明边框 */
  Border paddingBorder = BorderFactory.createEmptyBorder(10, 10, 10, 10);

  /** 白色的背景色 */
  Color commonBackground = new Color(0xE5E5E5);
}
