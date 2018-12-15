package zwei.ui.mediator;

import zwei.model.Course;
import zwei.model.Student;
import zwei.model.Teacher;
import zwei.ui.table.TeacherCourseListModel;
import zwei.ui.table.TeacherScoreTableModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;

import static zwei.ui.mediator.UiHelper.loadImage;

public class ScoreManagementPanel extends JPanel {

  private static final long serialVersionUID = -8894106068059978594L;
  private Teacher teacher;

  private JButton coursePlusBtn;
  private JButton courseMinusBtn;
  private JButton courseRefreshBtn;

  private JButton scorePlusBtn;
  private JButton scoreMinusBtn;
  private JButton scoreRefreshBtn;

  private JList<Course> courseList;
  private TeacherCourseListModel listModel;
  private JTable scoreTable;
  private TeacherScoreTableModel tableModel;

  public ScoreManagementPanel() {
    createSelf();

    tableModel = new TeacherScoreTableModel();
    scoreTable.setModel(tableModel);
    coursePlusBtn.addActionListener(this::createCourse);
    courseMinusBtn.addActionListener(this::deleteCourse);
    courseRefreshBtn.addActionListener(this::refreshCourse);
    courseList.addListSelectionListener(e -> {
      if (!e.getValueIsAdjusting()) return;
      Course selectedValue = courseList.getSelectedValue();
      if (selectedValue != null) {
        tableModel.setCourse(selectedValue);
      }
    });
  }

  public void setUser(Teacher teacher) {
    this.teacher = teacher;
    listModel = new TeacherCourseListModel(teacher);
    courseList.setModel(listModel);
  }

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

  private void createCourse(ActionEvent actionEvent) {
    String name =
        JOptionPane.showInputDialog(this, "课程的名字", "新建课程", JOptionPane.QUESTION_MESSAGE);
    if (name == null || name.isEmpty()) return;
    Course course = Course.createOne(name, teacher);
    if (course != null) {
      listModel.createOne(course);
    }
  }

  private void deleteCourse(ActionEvent actionEvent) {
    int selectedIndex = courseList.getSelectedIndex();
    if (selectedIndex != -1) {
      listModel.deleteOne(selectedIndex);
    }
  }

  private void refreshCourse(ActionEvent actionEvent) {
    listModel.refreshList();
  }

  private void createScore(ActionEvent actionEvent) {
    CreateScoreDialog dialog = new CreateScoreDialog();
    dialog.setModal(true);
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
    Student    student = dialog.getUserInputStudent();
    BigDecimal score   = dialog.getUserInputScore();
    if (student != null && score != null) {
      Course course = courseList.getSelectedValue();
      if (course == null) {
        courseList.transferFocus();
        return;
      }
      tableModel.createRow(student, score);
    }
  }

  private void deleteScore(ActionEvent actionEvent) {
    int[] selectedRows = scoreTable.getSelectedRows();
    if (selectedRows.length == 0) return;
    tableModel.removeRow(selectedRows);
  }

  private void refreshScore(ActionEvent actionEvent) {
    listModel.refreshList();
  }

  @SuppressWarnings("Duplicates")
  private void createSelf() {
    courseList = new JList<>();
    courseList.setLayoutOrientation(JList.VERTICAL);
    courseList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    UiHelper.setListRender(courseList, Course::getName);
    JLabel leftHint = new JLabel("课程列表");
    leftHint.setLabelFor(courseList);
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

    scoreTable = new JTable();
    UiHelper.setZebraStyle(scoreTable, String.class);
    UiHelper.setZebraStyle(scoreTable, BigDecimal.class);
    JPanel leftPanel  = new JPanel();
    JPanel rightPanel = new JPanel();

    leftPanel.setLayout(new BorderLayout());
    leftPanel.add(leftHint, BorderLayout.NORTH);
    leftPanel.add(courseList, BorderLayout.CENTER);
    leftPanel.add(leftBtnPanel, BorderLayout.SOUTH);

    JLabel rightHint = new JLabel("学生列表");
    rightHint.setLabelFor(courseList);

    scorePlusBtn = new JButton("添加");
    scoreMinusBtn = new JButton("删除");
    scoreRefreshBtn = new JButton("刷新");
    JPanel rightBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    rightBtnPanel.add(scorePlusBtn);
    rightBtnPanel.add(scoreMinusBtn);
    rightBtnPanel.add(scoreRefreshBtn);

    rightPanel.setLayout(new BorderLayout());
    rightPanel.add(rightHint, BorderLayout.NORTH);
    rightPanel.add(scoreTable, BorderLayout.CENTER);
    rightPanel.add(rightBtnPanel, BorderLayout.SOUTH);

    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
    setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
    setBackground(UserInterface.commonBackGround);
    setLayout(new BorderLayout());
    add(splitPane, BorderLayout.CENTER);
  }

  public static void main(String[] args) {
    try (BufferedReader reader = Files.newBufferedReader(Paths.get("/Volumes/RAM/coupon.sample"))) {
      reader.lines().forEach(System.out::println);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
