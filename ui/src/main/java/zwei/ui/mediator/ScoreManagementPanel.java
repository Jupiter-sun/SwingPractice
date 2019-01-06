package zwei.ui.mediator;

import zwei.model.Course;
import zwei.model.Student;
import zwei.model.Teacher;
import zwei.ui.UiHelper;
import zwei.ui.dialog.CreateScoreDialog;
import zwei.ui.table.TeacherCourseListModel;
import zwei.ui.table.TeacherScoreTableModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;

import static zwei.ui.UiHelper.loadImage;

/**
 * 教师界面，分数管理面板
 * Created on 2018-12-12
 *
 * @author 九条涼果 chunxiang.huang@hypers.com
 */
public class ScoreManagementPanel extends JPanel {

  private static final long serialVersionUID = -8894106068059978594L;
  private Teacher teacher;

  /** 添加课程按钮 */
  private JButton coursePlusBtn;
  /** 移除课程按钮 */
  private JButton courseMinusBtn;
  /** 刷新课程列表按钮 */
  private JButton courseRefreshBtn;

  /** 添加分数记录按钮 */
  private JButton scorePlusBtn;
  /** 移除分数记录按钮 */
  private JButton scoreMinusBtn;
  /** 刷新分数列表按钮 */
  private JButton scoreRefreshBtn;

  /** 课程列表 */
  private JList<Course> courseList;
  private TeacherCourseListModel listModel;

  /** 在这个课程上的学生的分数列表 */
  private JTable scoreTable;
  private TeacherScoreTableModel tableModel;

  public ScoreManagementPanel() {
    createSelf();

    coursePlusBtn.addActionListener(this::createCourse);
    courseMinusBtn.addActionListener(this::deleteCourse);
    courseRefreshBtn.addActionListener(this::refreshCourse);
    scorePlusBtn.addActionListener(this::createScore);
    scoreMinusBtn.addActionListener(this::deleteScore);
    scoreRefreshBtn.addActionListener(this::refreshScore);
    courseList.addListSelectionListener(e -> {
      if (e.getValueIsAdjusting()) return;
      Course selectedValue = courseList.getSelectedValue();
      if (selectedValue != null) {
        scorePlusBtn.setEnabled(true);
        scoreMinusBtn.setEnabled(true);
        scoreRefreshBtn.setEnabled(true);
        tableModel.setCourse(selectedValue);
      } else {
        scorePlusBtn.setEnabled(false);
        scoreMinusBtn.setEnabled(false);
        scoreRefreshBtn.setEnabled(false);
      }
    });
  }

  /**
   * 传递参数，这是哪个老师的课程。
   * 不同老师会对应有不同的学生列表
   */
  public void setUser(Teacher teacher) {
    this.teacher = teacher;
    tableModel = new TeacherScoreTableModel();
    scoreTable.setModel(tableModel);
    listModel = new TeacherCourseListModel(teacher);
    courseList.setModel(listModel);
    if (listModel.getSize() > 0) {
      courseList.setSelectedIndex(0);
    }
  }

  /** 跳转到这个面板的时候，给窗体设置菜单栏 */
  @SuppressWarnings("Duplicates")
  public void setMenubar(JMenuBar menuBar) {
    JMenu menu = new JMenu("分数管理");

    JMenuItem coursePlusMenu    = new JMenuItem("添加执教课程");
    JMenuItem courseMinusMenu   = new JMenuItem("删除执教课程");
    JMenuItem courseRefreshMenu = new JMenuItem("刷新课程列表");

    JMenuItem scorePlusMenu    = new JMenuItem("添加成绩记录");
    JMenuItem scoreMinusMenu   = new JMenuItem("删除成绩记录");
    JMenuItem scoreRefreshMenu = new JMenuItem("刷新成绩表格");

    coursePlusMenu.addActionListener(this::createCourse);
    courseMinusMenu.addActionListener(this::deleteCourse);
    courseRefreshMenu.addActionListener(this::refreshCourse);
    scorePlusMenu.addActionListener(this::createScore);
    scoreMinusMenu.addActionListener(this::deleteScore);
    scoreRefreshMenu.addActionListener(this::refreshScore);

    menu.add(coursePlusMenu);
    menu.add(courseMinusMenu);
    menu.add(courseRefreshMenu);
    menu.addSeparator();
    menu.add(scorePlusMenu);
    menu.add(scoreMinusMenu);
    menu.add(scoreRefreshMenu);
    menuBar.add(menu);
  }

  /** 处理用户点击添加课程按钮的事件 */
  private void createCourse(@SuppressWarnings("unused") ActionEvent actionEvent) {
    String name =
        JOptionPane.showInputDialog(this, "课程的名字", "新建课程", JOptionPane.QUESTION_MESSAGE);
    if (name == null || name.isEmpty()) return;
    Course course = Course.createOne(name, teacher);
    if (course != null) {
      listModel.createOne(course);
    }
  }

  /** 处理用户点击移除课程按钮的事件，支持多选 */
  private void deleteCourse(@SuppressWarnings("unused") ActionEvent actionEvent) {
    int selectedIndex = courseList.getSelectedIndex();
    if (selectedIndex != -1) {
      listModel.deleteOne(selectedIndex);
      courseList.setSelectedIndex(selectedIndex - 1);
    }
  }

  /** 处理用户点击刷新课程按钮的事件 */
  private void refreshCourse(@SuppressWarnings("unused") ActionEvent actionEvent) {
    listModel.refreshList();
  }

  /** 处理用户点击添加分数按钮的事件，弹出对话框 */
  private void createScore(@SuppressWarnings("unused") ActionEvent actionEvent) {
    Course selectedCourse = courseList.getSelectedValue();
    if (selectedCourse == null) {
      courseList.requestFocusInWindow();
      return;
    }

    CreateScoreDialog dialog = new CreateScoreDialog(selectedCourse);
    dialog.setModal(true);
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);

    // parse result
    Student    student = dialog.getUserInputStudent();
    BigDecimal score   = dialog.getUserInputScore();
    if (student != null && score != null) {
      tableModel.createRow(student, score);
    }
  }

  /** 处理用户点击移除分数按钮的事件，支持多选 */
  private void deleteScore(@SuppressWarnings("unused") ActionEvent actionEvent) {
    int[] selectedRows = scoreTable.getSelectedRows();
    if (selectedRows.length == 0) return;
    tableModel.removeRow(selectedRows);
  }

  /** 处理用户点击刷新分数按钮的事件 */
  private void refreshScore(@SuppressWarnings("unused") ActionEvent actionEvent) {
    tableModel.refreshTable();
  }

  /** 创建UI界面 */
  @SuppressWarnings("Duplicates")
  private void createSelf() {
    // left list
    courseList = new JList<>();
    courseList.setLayoutOrientation(JList.VERTICAL);
    courseList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    UiHelper.setListRender(courseList, Course::getName);

    // left list title
    JLabel leftHint = new JLabel("课程列表");
    leftHint.setLabelFor(courseList);

    // left controller button groups
    coursePlusBtn = new JButton(loadImage("iconmonstr-plus-1-16.png"));
    courseMinusBtn = new JButton(loadImage("iconmonstr-minus-1-16.png"));
    courseRefreshBtn = new JButton(loadImage("iconmonstr-refresh-3-16.png"));
    coursePlusBtn.setToolTipText("添加");
    courseMinusBtn.setToolTipText("删除");
    courseRefreshBtn.setToolTipText("刷新");
    JPanel leftBtnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    leftBtnPanel.add(coursePlusBtn);
    leftBtnPanel.add(courseMinusBtn);
    leftBtnPanel.add(courseRefreshBtn);

    // right table
    scoreTable = new JTable();
    scoreTable.setFillsViewportHeight(true);
    UiHelper.setZebraStyle(scoreTable, String.class);
    UiHelper.setZebraStyle(scoreTable, BigDecimal.class);

    // right table title
    JLabel rightHint = new JLabel("学生列表");
    rightHint.setLabelFor(courseList);

    // right controller button groups
    scorePlusBtn = new JButton("添加");
    scoreMinusBtn = new JButton("删除");
    scoreRefreshBtn = new JButton("刷新");
    JPanel rightBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    rightBtnPanel.add(scorePlusBtn);
    rightBtnPanel.add(scoreMinusBtn);
    rightBtnPanel.add(scoreRefreshBtn);

    // split panel components
    JPanel leftPanel  = new JPanel();
    JPanel rightPanel = new JPanel();

    // build left split panel
    leftPanel.setLayout(new BorderLayout());
    leftPanel.add(leftHint, BorderLayout.NORTH);
    leftPanel.add(courseList, BorderLayout.CENTER);
    leftPanel.add(leftBtnPanel, BorderLayout.SOUTH);

    // build right split panel
    rightPanel.setLayout(new BorderLayout());
    rightPanel.add(rightHint, BorderLayout.NORTH);
    rightPanel.add(new JScrollPane(scoreTable), BorderLayout.CENTER);
    rightPanel.add(rightBtnPanel, BorderLayout.SOUTH);

    // hock up content panel components
    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    splitPane.setLeftComponent(leftPanel);
    splitPane.setRightComponent(rightPanel);
    setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
    setBackground(UserInterface.commonBackground);
    setLayout(new BorderLayout());
    add(splitPane, BorderLayout.CENTER);
  }
}
