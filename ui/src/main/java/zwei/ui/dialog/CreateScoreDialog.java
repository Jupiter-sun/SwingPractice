package zwei.ui.dialog;

import org.jetbrains.annotations.Nullable;
import zwei.JDBCUtilities;
import zwei.model.Course;
import zwei.model.Student;
import zwei.ui.UiHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;

/**
 * 添加学生分数记录用的输入提示窗
 * Created on 2018-12-14
 *
 * @author 九条涼果 chunxiang.huang@hypers.com
 */
public class CreateScoreDialog extends JDialog {

  private static final long serialVersionUID = -2985032488653700623L;

  /** 学生选择下拉框 */
  private JComboBox<Student> studentBox;
  /** 分数输入文本框 */
  private JTextField scoreField;

  /** 确认按钮 */
  private JButton confirmBtn;
  /** 取消按钮 */
  private JButton cancelBtn;

  /** 临时保存创建的学生对象，供客户端在{@link #getUserInputStudent()}获取 */
  private transient Student userInputStudent;
  /** 临时保存创建的分数对象，供客户端在{@link #getUserInputScore()}}获取 */
  private transient BigDecimal userInputScore;

  public CreateScoreDialog(@Nullable Course course) {
    createSelf();

    /*设置下拉框内容为学生列表*/
    Connection connection = JDBCUtilities.getInstance().getConnection();
    List<Student> students = (course == null) ?
        Student.retrieveALl(connection) :
        course.nonLinkedStudents(JDBCUtilities.getInstance().getConnection());
    studentBox.setModel(new DefaultComboBoxModel<>(students.toArray(new Student[0])));

    /*添加操作回调函数*/
    studentBox.addItemListener(e -> studentBox.transferFocus());
    scoreField.addActionListener(this::clickOk);
    confirmBtn.addActionListener(this::clickOk);
    cancelBtn.addActionListener(this::clickCancel);
  }

  /** 处理用户点击OK按钮的事件 */
  private void clickOk(ActionEvent actionEvent) {
    Object item      = studentBox.getSelectedItem();
    String scoreText = scoreField.getText();
    if (item == null) {
      studentBox.requestFocusInWindow();
    } else if (scoreText.isEmpty()) {
      scoreField.requestFocusInWindow();
    } else {
      try {
        userInputStudent = (Student) item;
        userInputScore = new BigDecimal(scoreText);
        if (userInputScore.compareTo(BigDecimal.ZERO) < 0) {
          throw new NumberFormatException();
        }
        dispose();
      } catch (NumberFormatException e) {
        scoreField.requestFocusInWindow();
      }
    }
  }

  /** 处理用户点击Cancel按钮的事件 */
  private void clickCancel(ActionEvent actionEvent) {
    dispose();
  }

  @SuppressWarnings("Duplicates")
  private void createSelf() {
    JLabel info = new JLabel("请填写以下材料以创建学生成绩记录");

    JLabel selectLabel = new JLabel("学生:");
    studentBox = new JComboBox<>();
    UiHelper.setListRender(studentBox, Student::getStudentName);
    selectLabel.setLabelFor(studentBox);

    JLabel scoreLabel = new JLabel("成绩:");
    scoreField = new JTextField();
    scoreLabel.setLabelFor(scoreField);

    confirmBtn = new JButton("创建");
    cancelBtn = new JButton("取消");

    JPanel infoPanel  = new JPanel();
    JPanel inputPanel = new JPanel();
    JPanel btnPanel   = new JPanel();

    infoPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    infoPanel.add(info);
    infoPanel.setMinimumSize(infoPanel.getPreferredSize());

    inputPanel.setLayout(new GridBagLayout());
    GridBagConstraints constraints1 = new GridBagConstraints();
    GridBagConstraints constraints2 = new GridBagConstraints();
    constraints1.gridx = 0;
    constraints2.gridx = 1;
    constraints2.weightx = 1;
    constraints2.weighty = 1;
    constraints2.insets = new Insets(4, 4, 4, 4);
    constraints2.fill = GridBagConstraints.BOTH;

    constraints1.gridy = 0;
    inputPanel.add(selectLabel, constraints1);
    constraints1.gridy = 1;
    inputPanel.add(scoreLabel, constraints1);
    constraints2.gridy = 0;
    constraints2.weighty = 0;
    inputPanel.add(studentBox, constraints2);
    constraints2.gridy = 1;
    constraints2.weighty = 1;
    inputPanel.add(scoreField, constraints2);

    inputPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

    btnPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
    btnPanel.add(cancelBtn);
    btnPanel.add(confirmBtn);
    btnPanel.setMinimumSize(btnPanel.getPreferredSize());

    setLayout(new BorderLayout());
    add(infoPanel, BorderLayout.NORTH);
    add(inputPanel, BorderLayout.CENTER);
    add(btnPanel, BorderLayout.SOUTH);
    Dimension preferredSize = getPreferredSize();
    preferredSize.height += 20;
    setMinimumSize(preferredSize);
  }

  @Nullable
  public Student getUserInputStudent() {
    return userInputStudent;
  }

  @Nullable
  public BigDecimal getUserInputScore() {
    return userInputScore;
  }
}
