package zwei.ui.mediator;

import zwei.model.CourseStudentLink;
import zwei.model.Student;
import zwei.ui.table.CourseListModel;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;

/**
 * Created on 2018-12-11
 *
 * @author 九条涼果 chunxiang.huang@hypers.com
 */
public class StudentInterface extends JPanel implements UserInterface {

  private static final long serialVersionUID = -3427522772908306608L;

  private Student student;

  private JLabel idLabel;
  private JLabel nmLabel;
  private JLabel clLabel;
  private JLabel sjLabel;

  private JComboBox<String> courseDropdown;
  private JButton searchBtn;
  private JTextArea scoreArea;
  private CourseListModel courseListModel;

  public StudentInterface() {
    createSelf();
  }

  @SuppressWarnings( {"Duplicates", "MagicNumber"})
  private void createSelf() {

    JLabel idLabelLabel = new JLabel("学号:");
    JLabel nmLabelLabel = new JLabel("姓名:");
    JLabel clLabelLabel = new JLabel("班级:");
    JLabel sjLabelLabel = new JLabel("学科:");
    idLabel = new JLabel();
    nmLabel = new JLabel();
    clLabel = new JLabel();
    sjLabel = new JLabel();
    idLabelLabel.setLabelFor(idLabel);
    nmLabelLabel.setLabelFor(nmLabel);
    clLabelLabel.setLabelFor(clLabel);
    sjLabelLabel.setLabelFor(sjLabel);

    JPanel leftPanel = new JPanel();
    leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.PAGE_AXIS));
    leftPanel.setBorder(BorderFactory.createTitledBorder("简介"));

    JPanel leftSubSegment1 = new JPanel(new BorderLayout(4, 0));
    JPanel leftSubSegment2 = new JPanel(new BorderLayout(4, 0));
    JPanel leftSubSegment3 = new JPanel(new BorderLayout(4, 0));
    JPanel leftSubSegment4 = new JPanel(new BorderLayout(4, 0));
    leftSubSegment1.add(idLabelLabel, BorderLayout.LINE_START);
    leftSubSegment1.add(idLabel, BorderLayout.CENTER);
    leftSubSegment2.add(nmLabelLabel, BorderLayout.LINE_START);
    leftSubSegment2.add(nmLabel, BorderLayout.CENTER);
    leftSubSegment3.add(clLabelLabel, BorderLayout.LINE_START);
    leftSubSegment3.add(clLabel, BorderLayout.CENTER);
    leftSubSegment4.add(sjLabelLabel, BorderLayout.LINE_START);
    leftSubSegment4.add(sjLabel, BorderLayout.CENTER);
    leftPanel.add(leftSubSegment1);
    leftPanel.add(leftSubSegment2);
    leftPanel.add(leftSubSegment3);
    leftPanel.add(leftSubSegment4);


    courseDropdown = new JComboBox<>();
    JLabel dropDownLabel = new JLabel("课程:");
    dropDownLabel.setLabelFor(courseDropdown);

    searchBtn = new JButton("查询");
    searchBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

    scoreArea = new JTextArea();
    Border       lineBorder   = BorderFactory.createLineBorder(Color.DARK_GRAY);
    TitledBorder titledBorder = BorderFactory.createTitledBorder(lineBorder, "分数");
    scoreArea.setBorder(titledBorder);
    scoreArea.setOpaque(false);

    JPanel rightSubSegment1 = new JPanel(new BorderLayout(4, 0));
    rightSubSegment1.add(dropDownLabel, BorderLayout.WEST);
    rightSubSegment1.add(courseDropdown, BorderLayout.CENTER);

    JPanel rightPanel = new JPanel();
    rightPanel.setLayout(new GridBagLayout());

    GridBagConstraints constraints = new GridBagConstraints();
    constraints.fill = GridBagConstraints.HORIZONTAL;
    constraints.weighty = 0;
    constraints.gridy = 1;
    rightPanel.add(rightSubSegment1, constraints);
    constraints.fill = GridBagConstraints.NONE;
    constraints.gridy = 2;
    rightPanel.add(searchBtn, constraints);
    constraints.fill = GridBagConstraints.BOTH;
    constraints.gridy = 3;
    constraints.weighty = 1;
    constraints.weightx = 1;
    rightPanel.add(scoreArea, constraints);

    setLayout(new BorderLayout(12, 0));
    setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    add(leftPanel, BorderLayout.WEST);
    add(rightPanel, BorderLayout.CENTER);
  }

  private void fillInData() {
    idLabel.setText(student.getStudentId());
    nmLabel.setText(student.getStudentName());
    clLabel.setText(student.getStudentGrade());
    sjLabel.setText(student.getStudentSubject());

    courseListModel = new CourseListModel(student);
    courseDropdown.setModel(courseListModel);
    searchBtn.addActionListener(this::fireSearch);
  }

  private void fireSearch(ActionEvent actionEvent) {
    CourseStudentLink item   = courseListModel.getSelected();
    BigDecimal        score  = item.getScore();
    String            course = item.getCourse().getName();
    scoreArea.append(String.format("%s: %s%n", course, score));
  }

  @Override
  public void showInFrame(JFrame parent) {
    fillInData();
    parent.setContentPane(this);
    parent.setTitle("学生页面");
    parent.pack();
    parent.setMinimumSize(parent.getSize());
  }

  @SuppressWarnings("CastToConcreteClass")
  @Override
  public void putArgument(String key, Object value) {
    if ("user".equals(key)) {
      student = (Student) value;
    }
  }

  private static class MyDefaultListCellRenderer extends DefaultListCellRenderer {

    private static final long serialVersionUID = -4861762253451239217L;

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index,
        boolean isSelected, boolean cellHasFocus) {
      super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      if (value instanceof CourseStudentLink) {
        setText(((CourseStudentLink) value).getCourse().getName());
      }
      return this;
    }
  }

  public static void main(String[] args) {
    JFrame        frame     = new JFrame();
    UserInterface Interface = new StudentInterface();
    Interface.showInFrame(frame);
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }
}
