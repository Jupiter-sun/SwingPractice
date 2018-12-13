package zwei.ui.mediator;

import zwei.JDBCUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;

/**
 * Created on 2018-12-11
 *
 * @author 九条涼果 chunxiang.huang@hypers.com
 */
public class TeacherInterface extends JPanel implements UserInterface {

  private static final long serialVersionUID = -2882649301771472643L;
  private JTabbedPane switcher;
  private JPanel studentPanel;
  private JPanel scorePanel;
  private Connection connection;

  public TeacherInterface() {
    createSelf();
  }

  @SuppressWarnings("Duplicates")
  private void createSelf() {
    connection = JDBCUtilities.getInstance().getConnection();

    JPanel no1Panel = new StudentManagementPanel(connection);

    JPanel no2Panel = new ScoreManagementPanel();

    switcher = new JTabbedPane(SwingConstants.TOP);
    switcher.addTab("学生信息", no1Panel);
    switcher.addTab("成绩信息", no2Panel);

    setLayout(new BorderLayout());
    add(switcher, BorderLayout.CENTER);
  }

  @Override
  public void showInFrame(JFrame parent) {
    parent.setContentPane(this);
    parent.setTitle("老师页面");
    parent.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        JDBCUtilities.close(connection);
      }
    });
  }

  public static void main(String[] args) {
    JFrame frame = new JFrame();

    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    UserInterface Interface = new TeacherInterface();
    Interface.showInFrame(frame);
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }
}
