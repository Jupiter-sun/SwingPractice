package zwei.ui.mediator;

import zwei.model.Course;
import zwei.model.Teacher;
import zwei.ui.table.TeacherCourseListModel;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ScoreManagementPanel extends JPanel {

  private static final long serialVersionUID = -8894106068059978594L;
  private Teacher teacher;

  private JTable tableDisplay;
  private JButton plusBtn;
  private JButton minusBtn;
  private JTextField searchBox;
  private JList<Course> courseList;
  private TeacherCourseListModel model;

  public ScoreManagementPanel() {
    createSelf();
  }

  public void setUser(Teacher teacher) {
    this.teacher = teacher;
    model = new TeacherCourseListModel(teacher);
    courseList.setModel(model);
  }

  private void createSelf() {
    courseList = new JList<>();
    courseList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    JLabel leftHint = new JLabel("课程列表:");
    leftHint.setLabelFor(courseList);

    JPanel leftPanel  = new JPanel();
    JPanel rightPanel = new JPanel();

    leftPanel.setLayout(new BorderLayout());
    leftPanel.add(leftHint, BorderLayout.NORTH);
    leftPanel.add(courseList, BorderLayout.CENTER);

    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
    setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
    setBackground(UserInterface.commonBackGround);
    setLayout(new BorderLayout());
    add(splitPane, BorderLayout.CENTER);
  }
  public void setMenubar(JMenuBar menuBar) {
  }

  public static void main(String[] args) {
    try (BufferedReader reader = Files.newBufferedReader(Paths.get("/Volumes/RAM/coupon.sample"))) {
      reader.lines().forEach(System.out::println);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
