package zwei.ui.mediator;

import zwei.model.Teacher;
import zwei.ui.UiHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Created on 2018-12-11
 *
 * @author 九条涼果 chunxiang.huang@hypers.com
 */
public class TeacherInterface extends JPanel implements UserInterface {

  private static final long serialVersionUID = -2882649301771472643L;

  /** 学生管理面板 */
  private StudentManagementPanel studentPanel;
  /** 分数管理面板 */
  private ScoreManagementPanel scorePanel;

  /** 菜单栏中显示教师名字的文本 */
  private JLabel nameMenuLabel = new JLabel();
  /** 窗口菜单栏 */
  private JMenuBar menuBar;

  public TeacherInterface() {
    createSelf();
  }

  @SuppressWarnings("Duplicates")
  private void createSelf() {

    studentPanel = new StudentManagementPanel();
    scorePanel = new ScoreManagementPanel();

    menuBar = new JMenuBar();
    setMenubar(menuBar);
    studentPanel.setMenubar(menuBar);
    scorePanel.setMenubar(menuBar);

    JTabbedPane switcher = new JTabbedPane(SwingConstants.TOP);
    switcher.addTab("学生信息", studentPanel);
    switcher.addTab("成绩信息", scorePanel);

    setLayout(new BorderLayout());
    add(switcher, BorderLayout.CENTER);
  }

  @SuppressWarnings("Duplicates")
  private void setMenubar(JMenuBar menuBar) {
    JMenu menu = new JMenu("账户");

    JMenuItem item = new JMenuItem("登出");
    item.addActionListener(this::clickLogout);
    menu.add(nameMenuLabel);
    menu.addSeparator();
    menu.add(item);
    menuBar.add(menu);
  }

  /** 处理用户点击菜单栏上登出按钮的事件 */
  private void clickLogout(ActionEvent actionEvent) {
    JFrame frame = (JFrame) SwingUtilities.windowForComponent(this);
    UiHelper.changeFrameContent(frame, new SplashInterface());
  }

  @Override
  @SuppressWarnings("CastToConcreteClass")
  public void putArgument(String key, Object value) {
    if ("user".equals(key)) {
      Teacher teacher = (Teacher) value;
      /*设置菜单栏上教师名字*/
      nameMenuLabel.setText("教师: " + teacher.getTeacherName());
      studentPanel.setUser(teacher);
      scorePanel.setUser(teacher);
    }
  }

  @Override
  public void showInFrame(JFrame parent) {
    UiHelper.onFrameCenter(parent, frame -> {
      frame.setContentPane(this);
      frame.setJMenuBar(menuBar);
      frame.setTitle("老师页面");
    });
  }
}
