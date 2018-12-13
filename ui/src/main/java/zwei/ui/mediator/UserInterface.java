package zwei.ui.mediator;

import javax.swing.*;
import javax.swing.border.Border;

/**
 * Created on 2018-12-12
 *
 * @author 九条涼果 chunxiang.huang@hypers.com
 */
public interface UserInterface {

  void showInFrame(JFrame parent);

  Border paddingBorder = BorderFactory.createEmptyBorder(10, 10, 10, 10);
}
