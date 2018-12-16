package zwei.ui.mediator;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.net.URL;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created on 2018-12-16
 *
 * @author 九条涼果 chunxiang.huang@hypers.com
 */
class UiHelper {

  static void setZebraStyle(JTable table, Class<?> columnClass) {
    TableCellRenderer renderer = table.getDefaultRenderer(columnClass);
    table.setDefaultRenderer(columnClass,
        (table1, value, isSelected, hasFocus, row, column) -> {
          Component c =
              renderer.getTableCellRendererComponent(table1, value, isSelected, hasFocus, row,
                  column);
          c.setBackground(row % 2 == 0 ? new Color(0xEFF0F1) : new Color(0xF9F9F9));
          c.setBackground(isSelected ? new Color(0xB4D1F5) : c.getBackground());
          c.setForeground(isSelected ? Color.BLACK : new Color(0x222426));
          return c;
        });
  }

  static <E> void setListRender(JList<E> list, Function<? super E, String> toString) {
    ListCellRenderer<? super E> renderer = list.getCellRenderer();
    list.setCellRenderer(proxyListCellRenderer(renderer, toString));
  }

  static <E> void setListRender(JComboBox<E> comboBox, Function<? super E, String> toString) {
    ListCellRenderer<? super E> renderer = comboBox.getRenderer();
    comboBox.setRenderer(proxyListCellRenderer(renderer, toString));
  }

  private static <E> ListCellRenderer<E> proxyListCellRenderer(ListCellRenderer<? super E> renderer,
      Function<? super E, String> toString) {
    return (list, value, index, isSelected, cellHasFocus) -> {
      Component c =
          renderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      ((JLabel) c).setText(toString.apply(value));
      return c;
    };
  }

  static Icon loadImage(String path) {
    URL resource = ScoreManagementPanel.class.getClassLoader().getResource(path);
    if (resource != null) {
      Image image = Toolkit.getDefaultToolkit()
          .createImage(resource)
          .getScaledInstance(16, 16, Image.SCALE_SMOOTH);
      return new ImageIcon(image);
    }
    throw new RuntimeException("image not found");
  }

  /** 替换Frame的内容，不改变中心位置 */
  static void changeFrameContent(JFrame frame, UserInterface Interface) {
    onFrameCenter(frame, f -> Interface.showInFrame(frame));
    frame.setVisible(true);
    frame.requestFocus();
  }

  static void onFrameCenter(JFrame frame, Consumer<? super JFrame> worker) {
    if (!frame.isShowing()) {
      worker.accept(frame);
      frame.pack();
      frame.setMinimumSize(frame.getPreferredSize());
      return;
    }
    Point screenLocation = frame.getLocationOnScreen();
    screenLocation.translate(frame.getWidth() / 2, frame.getHeight() / 2);
    worker.accept(frame);
    frame.pack();
    frame.setMinimumSize(frame.getPreferredSize());
    screenLocation.translate(-frame.getWidth() / 2, -frame.getHeight() / 2);
    frame.setLocation(screenLocation);
  }


}
