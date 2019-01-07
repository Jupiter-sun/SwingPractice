package zwei.ui.mediator;

import zwei.dao.JDBCUtilities;
import zwei.model.Student;
import zwei.model.Teacher;
import zwei.model.User;
import zwei.ui.UiHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * 用户注册的界面
 * Created on 2018-12-11
 *
 * @author 九条涼果 chunxiang.huang@hypers.com
 */
public class RegisterInterface extends JPanel implements UserInterface {

  private static final long serialVersionUID = -5795654569586694048L;

  /** 注册的用户类型 */
  private Class<? extends User> type;

  /** 用户ID输入文本框 */
  private JTextField idField;
  /** 密码输入文本框 */
  private JTextField pwField;
  /** 姓名输入文本框 */
  private JTextField nmField;
  /** 班级输入文本框 */
  private JTextField clField;
  /** 专业输入文本框 */
  private JTextField mjField;

  /** 返回按钮 */
  private JButton backBtn;
  /** 注册按钮 */
  private JButton registerBtn;

  /** 保存来源状态，用于还原到上一个界面 */
  private Memento parentState;

  public RegisterInterface(Class<? extends User> type) {
    this.type = type;

    createSelf();

    /*添加操作回调函数*/
    idField.addActionListener((e) -> idField.transferFocus());
    nmField.addActionListener((e) -> nmField.transferFocus());
    pwField.addActionListener(this::clickRegister);
    backBtn.addActionListener(this::backward);
    registerBtn.addActionListener(this::clickRegister);
  }

  @Override
  public void showInFrame(JFrame parent) {
    parentState = backup(parent);
    UiHelper.onFrameCenter(parent, f -> {
      f.setContentPane(this);
      f.getRootPane().setDefaultButton(registerBtn);
      f.setTitle("用户注册");
    });
  }

  @SuppressWarnings("Duplicates")
  private void createSelf() {
    idField = new JTextField();
    nmField = new JTextField();
    pwField = new JPasswordField();
    clField = new JTextField();
    mjField = new JTextField();
    JLabel idLabel = new JLabel("学号:", JLabel.TRAILING);
    JLabel nmLabel = new JLabel("姓名:", JLabel.TRAILING);
    JLabel pwLabel = new JLabel("密码:", JLabel.TRAILING);
    JLabel clLabel = new JLabel("班级:", JLabel.TRAILING);
    JLabel mjLabel = new JLabel("学科:", JLabel.TRAILING);
    idLabel.setLabelFor(idField);
    nmLabel.setLabelFor(nmField);
    pwLabel.setLabelFor(pwField);
    clLabel.setLabelFor(clField);
    mjLabel.setLabelFor(mjField);

    backBtn = new JButton("返回");
    registerBtn = new JButton("注册");

    JPanel midPanel = new JPanel(new SpringLayout());
    midPanel.add(idLabel);
    midPanel.add(idField);
    midPanel.add(nmLabel);
    midPanel.add(nmField);
    midPanel.add(pwLabel);
    midPanel.add(pwField);

    if (type == Teacher.class) {
      idLabel.setText("工号:");
      SpringUtilities.makeCompactGrid(midPanel, 3, 2, 6, 6, 6, 6);
    } else if (type == Student.class) {
      midPanel.add(clLabel);
      midPanel.add(clField);
      midPanel.add(mjLabel);
      midPanel.add(mjField);
      SpringUtilities.makeCompactGrid(midPanel, 5, 2, 6, 6, 6, 6);
    }

    JPanel lstPanel = new JPanel();
    lstPanel.add(backBtn);
    lstPanel.add(registerBtn);

    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    setBorder(new EmptyBorder(10, 10, 10, 10));
    add(midPanel);
    add(lstPanel);
  }

  /** 处理用户点击注册按钮的事件 */
  private void clickRegister(ActionEvent actionEvent) {
    String inputId       = idField.getText();
    String inputName     = nmField.getText();
    String inputPassword = pwField.getText();

    boolean success = false;
    //noinspection ChainOfInstanceofChecks
    if (type == Student.class) {
      Student newlyCreated = Student.createAccount(inputId, inputName, inputPassword);
      success = JDBCUtilities.dbop((conn) -> {return Student.persistOne(conn, newlyCreated);});
    } else if (type == Teacher.class) {
      Teacher newlyCreated = Teacher.createAccount(inputId, inputName, inputPassword);
      success = JDBCUtilities.dbop((conn) -> {return Teacher.persistOne(conn, newlyCreated);});
    }

    if (success) {
      backward(actionEvent); // return to previous page
    } else {
      idField.requestFocusInWindow();
      idField.selectAll();
    }
  }

  /** 处理用户点击返回按钮的事件 */
  private void backward(ActionEvent actionEvent) {
    if (parentState != null) {
      parentState.revert();
    }
  }

  /** 储存来源JFrame */
  private static Memento backup(JFrame parent) {
    Memento memento = new Memento();
    memento.frame = parent;
    memento.upLayer = parent.getContentPane();
    memento.defaultButton = parent.getRootPane().getDefaultButton();
    memento.title = parent.getTitle();
    return memento;
  }

  private static class Memento {

    JFrame frame;
    Container upLayer;
    JButton defaultButton;
    String title;

    void revert() {
      if (frame.isShowing()) {
        frame.setContentPane(upLayer);
        frame.setTitle(title);
        frame.getRootPane().setDefaultButton(defaultButton);
        frame.setMinimumSize(frame.getPreferredSize());
        frame.pack();
        upLayer.setVisible(true);
      }
    }
  }
}
