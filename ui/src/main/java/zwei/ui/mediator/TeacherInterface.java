package zwei.ui.mediator;

import zwei.model.Teacher;

import javax.swing.*;
import java.awt.*;

/**
 * Created on 2018-12-11
 *
 * @author 九条涼果 chunxiang.huang@hypers.com
 */
public class TeacherInterface extends JPanel implements UserInterface {

  private static final long serialVersionUID = -2882649301771472643L;

  private Teacher teacher;

  private JTabbedPane switcher;
  private StudentManagementPanel studentPanel;
  private ScoreManagementPanel scorePanel;

  public TeacherInterface() {
    createSelf();
  }

  @SuppressWarnings("Duplicates")
  private void createSelf() {

    JPanel no1Panel = new StudentManagementPanel();
    JPanel no2Panel = new ScoreManagementPanel();

    switcher = new JTabbedPane(SwingConstants.TOP);
    switcher.addTab("学生信息", no1Panel);
    switcher.addTab("成绩信息", no2Panel);

    JMenu menu = new JMenu();

    setLayout(new BorderLayout());
    add(switcher, BorderLayout.CENTER);
  }

  @Override
  public void showInFrame(JFrame parent) {
    parent.setContentPane(this);
    parent.setTitle("老师页面");
    parent.pack();
    parent.setLocationRelativeTo(null);
    parent.setMinimumSize(parent.getSize());
  }

  @SuppressWarnings("CastToConcreteClass")
  @Override
  public void putArgument(String key, Object value) {
    if ("user".equals(key)) {
      teacher = (Teacher) value;
    }
  }

  public static void main(String[] args) {
    JFrame frame = new JFrame();
    UserInterface Interface = new TeacherInterface();
    Interface.showInFrame(frame);
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }
}
