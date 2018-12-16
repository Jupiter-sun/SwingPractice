package zwei.ui.mediator;

import org.jetbrains.annotations.Nullable;
import zwei.JDBCUtilities;
import zwei.model.Course;
import zwei.model.Student;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;

/**
 * Created on 2018-12-14
 *
 * @author 九条涼果 chunxiang.huang@hypers.com
 */
public class CreateScoreDialog extends JDialog {

  private static final long serialVersionUID = -2985032488653700623L;

  private JComboBox<Student> studentBox;
  private JTextField scoreField;

  private JButton confirmBtn;
  private JButton cancelBtn;

  private transient Student userInputStudent;
  private transient BigDecimal userInputScore;

  public CreateScoreDialog(@Nullable Course course) {
    createSelf();
    Connection connection = JDBCUtilities.getInstance().getConnection();
    List<Student> students = (course == null) ?
        Student.retrieveALl(connection) :
        course.nonLinkedStudents(JDBCUtilities.getInstance().getConnection());
    studentBox.setModel(new DefaultComboBoxModel<>(students.toArray(new Student[0])));

    studentBox.addItemListener(e -> studentBox.transferFocus());
    scoreField.addActionListener(this::clickOk);
    confirmBtn.addActionListener(this::clickOk);
    cancelBtn.addActionListener(this::clickCancel);
  }

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

  public static void main(String[] args) {
    Window dialog = new CreateScoreDialog(null);
    dialog.pack();
    dialog.setVisible(true);
    dialog.setLocationRelativeTo(null);
  }
}
