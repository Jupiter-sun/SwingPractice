package zwei.ui.mediator;

import zwei.model.Course;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Created on 2018-12-11
 *
 * @author 九条涼果 chunxiang.huang@hypers.com
 */
public class StudentInterface extends JPanel implements UserInterface {

  private static final long serialVersionUID = -3427522772908306608L;

  private JLabel idLabel;
  private JLabel nmLabel;
  private JLabel clLabel;
  private JLabel sjLabel;

  private JComboBox<Course> courseDropdown;
  private JButton searchBtn;
  private JTextArea scoreArea;

  public StudentInterface() {
    createSelf();
    fillInData();
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

    scoreArea = new JTextArea("物理: 99");
    Border       lineBorder   = BorderFactory.createLineBorder(Color.DARK_GRAY);
    TitledBorder titledBorder = BorderFactory.createTitledBorder(lineBorder, "分数");
    scoreArea.setBorder(titledBorder);
    scoreArea.setOpaque(false);

    JPanel rightSubSegment1 = new JPanel(new BorderLayout(4, 0));
    rightSubSegment1.add(dropDownLabel, BorderLayout.WEST);
    rightSubSegment1.add(courseDropdown, BorderLayout.CENTER);

    JPanel rightPanel = new JPanel();
    rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.PAGE_AXIS));
    rightPanel.add(rightSubSegment1);
    rightPanel.add(searchBtn);
    rightPanel.add(scoreArea);

    setLayout(new BorderLayout(12, 0));
    setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    add(leftPanel, BorderLayout.WEST);
    add(rightPanel, BorderLayout.CENTER);
  }

  private void fillInData() {

  }

  @Override
  public void showInFrame(JFrame parent) {
    parent.setContentPane(this);
    parent.setTitle("学生页面");
    parent.pack();
  }

  public static void main(String[] args) {
    JFrame        frame     = new JFrame();
    UserInterface Interface = new StudentInterface();
    Interface.showInFrame(frame);
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }
}
