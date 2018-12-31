package zwei.ui.dialog;

import org.jetbrains.annotations.Nullable;
import zwei.model.Student;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * 添加学生条目记录用的输入提示窗
 * Created on 2018-12-14
 *
 * @author 九条涼果 chunxiang.huang@hypers.com
 */
public class CreateStudentDialog extends JDialog {

  private static final long serialVersionUID = -2985032488653700623L;

  /** 学号输入文本框 */
  private JTextField idField;
  /** 密码输入文本框 */
  private JTextField pwField;
  /** 姓名输入文本框 */
  private JTextField nmField;
  /** 班级输入文本框 */
  private JTextField clField;
  /** 学科输入文本框 */
  private JTextField mjField;

  /** 确认按钮 */
  private JButton confirmBtn;
  /** 取消按钮 */
  private JButton cancelBtn;

  /**临时保存创建的对象，供客户端在{@link #getUserInput()}获取*/
  private transient Student userInput;

  public CreateStudentDialog() {
    createSelf();

    /*添加操作回调函数*/
    idField.addActionListener((e) -> idField.transferFocus());
    pwField.addActionListener((e) -> pwField.transferFocus());
    nmField.addActionListener((e) -> nmField.transferFocus());
    clField.addActionListener((e) -> clField.transferFocus());
    mjField.addActionListener(this::clickOk);
    confirmBtn.addActionListener(this::clickOk);
    cancelBtn.addActionListener(this::clickCancel);
  }

  /**处理用户点击OK按钮的事件*/
  private void clickOk(ActionEvent actionEvent) {
    String id = idField.getText();
    String pw = pwField.getText();
    String nm = nmField.getText();
    String cl = clField.getText();
    String mj = mjField.getText();

    if (id.isEmpty()) {
      idField.requestFocusInWindow();
    } else if (pw.isEmpty()) {
      pwField.requestFocusInWindow();
    } else if (nm.isEmpty()) {
      nmField.requestFocusInWindow();
    } else if (cl.isEmpty()) {
      clField.requestFocusInWindow();
    } else if (mj.isEmpty()) {
      mjField.requestFocusInWindow();
    } else {
      userInput = Student.createAccount(id, nm, pw, cl, mj);
      dispose();
    }
  }

  /** 处理用户点击Cancel按钮的事件 */
  private void clickCancel(ActionEvent actionEvent) {
    dispose();
  }

  @SuppressWarnings("Duplicates")
  private void createSelf() {
    JLabel info = new JLabel("请填写以下材料以创建学生记录");

    idField = new JTextField("1");
    pwField = new JTextField("2");
    nmField = new JTextField("3");
    clField = new JTextField("4");
    mjField = new JTextField("5");
    JLabel idLabel = new JLabel("学号:");
    JLabel pwLabel = new JLabel("密码:");
    JLabel nmLabel = new JLabel("姓名:");
    JLabel clLabel = new JLabel("班级:");
    JLabel mjLabel = new JLabel("专业:");
    idLabel.setLabelFor(idField);
    pwLabel.setLabelFor(pwField);
    nmLabel.setLabelFor(nmField);
    clLabel.setLabelFor(clField);
    mjLabel.setLabelFor(mjField);

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
    inputPanel.add(idLabel, constraints1);
    constraints1.gridy = 1;
    inputPanel.add(pwLabel, constraints1);
    constraints1.gridy = 2;
    inputPanel.add(nmLabel, constraints1);
    constraints1.gridy = 3;
    inputPanel.add(clLabel, constraints1);
    constraints1.gridy = 4;
    inputPanel.add(mjLabel, constraints1);
    constraints2.gridy = 0;
    inputPanel.add(idField, constraints2);
    constraints2.gridy = 1;
    inputPanel.add(pwField, constraints2);
    constraints2.gridy = 2;
    inputPanel.add(nmField, constraints2);
    constraints2.gridy = 3;
    inputPanel.add(clField, constraints2);
    constraints2.gridy = 4;
    inputPanel.add(mjField, constraints2);

    inputPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
    inputPanel.setMinimumSize(inputPanel.getPreferredSize());

    btnPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
    btnPanel.add(cancelBtn);
    btnPanel.add(confirmBtn);
    btnPanel.setMinimumSize(btnPanel.getPreferredSize());

    setLayout(new BorderLayout());
    add(infoPanel, BorderLayout.NORTH);
    add(inputPanel, BorderLayout.CENTER);
    add(btnPanel, BorderLayout.SOUTH);
    setMinimumSize(getPreferredSize());
  }

  @Nullable
  public Student getUserInput() {
    return userInput;
  }
}
