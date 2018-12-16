package zwei.ui.mediator;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * Created on 2018-12-12
 *
 * @author 九条涼果 chunxiang.huang@hypers.com
 */
@FunctionalInterface
public interface UserInterface {

  /**
   * Transfer arguments to panel.
   * Inspired by Android Bundle mechanism
   * Note: call after constructor, obviously
   */
  default void putArgument(String key, Object value) { }

  /** Note: call after {@link #putArgument(String, Object)}*/
  void showInFrame(JFrame parent);

  Border paddingBorder = BorderFactory.createEmptyBorder(10, 10, 10, 10);

  Color commonBackGround = new Color(0xE5E5E5);
}
