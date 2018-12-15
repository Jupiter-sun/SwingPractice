package zwei.ui.mediator;

import zwei.JDBCUtilities;
import zwei.model.Student;
import zwei.model.Teacher;
import zwei.model.User;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.function.Supplier;

/**
 * Created on 2018-12-11
 *
 * @author 九条涼果 chunxiang.huang@hypers.com
 */
public class SplashInterface extends JPanel implements UserInterface {

  private static final long serialVersionUID = 5251739093646189753L;

  private JTextField idField;
  private JTextField pwField;

  private JRadioButton stuRadioBtn;
  private JRadioButton teaRadioBtn;

  private JButton loginBtn;
  private JButton registerBtn;

  public SplashInterface() {
    createSelf();
    idField.addActionListener(this::enterOnIdField);
    pwField.addActionListener(this::clickLogin);
    loginBtn.addActionListener(this::clickLogin);
    registerBtn.addActionListener(this::clickRegister);
  }

  @Override
  public void showInFrame(JFrame parent) {
    parent.setContentPane(this);
    parent.getRootPane().setDefaultButton(loginBtn);
    parent.setTitle("用户登录");
    parent.pack();
  }

  @Override
  public void putArgument(String key, Object value) { }

  @SuppressWarnings("Duplicates")
  private void createSelf() {
    idField = new JTextField("001");
    pwField = new JPasswordField("111");
    JLabel idLabel = new JLabel(" ID:", JLabel.TRAILING);
    JLabel pwLabel = new JLabel("PWD:", JLabel.TRAILING);
    idLabel.setLabelFor(idField);
    pwLabel.setLabelFor(pwField);


    loginBtn = new JButton("登录");
    registerBtn = new JButton("注册");

    stuRadioBtn = new JRadioButton("学生");
    teaRadioBtn = new JRadioButton("教师");
    ButtonGroup buttonGroup = new ButtonGroup();
    buttonGroup.add(teaRadioBtn);
    buttonGroup.add(stuRadioBtn);
    teaRadioBtn.setSelected(true);

    JPanel topPanel = new JPanel();
    topPanel.add(stuRadioBtn);
    topPanel.add(teaRadioBtn);
    topPanel.setMaximumSize(topPanel.getPreferredSize());

    JPanel midPanel = new JPanel(new SpringLayout());
    midPanel.add(idLabel);
    midPanel.add(idField);
    midPanel.add(pwLabel);
    midPanel.add(pwField);
    SpringUtilities.makeCompactGrid(midPanel, 2, 2, 6, 6, 6, 6);

    JPanel lstPanel = new JPanel();
    lstPanel.add(registerBtn);
    lstPanel.add(loginBtn);

    setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
    setBorder(paddingBorder);
    add(topPanel);
    add(midPanel);
    add(lstPanel);
  }

  /** @see #idField */
  private void enterOnIdField(ActionEvent actionEvent) {
    if (pwField.getText().isEmpty()) {
      pwField.transferFocus();
    } else {
      clickLogin(actionEvent);
    }
  }

  /** @see #loginBtn */
  private void clickLogin(ActionEvent actionEvent) {
    String inputId       = idField.getText();
    String inputPassword = pwField.getText();

    if (inputId == null || inputId.isEmpty()) {
      idField.requestFocusInWindow();
      return;
    }
    if (inputPassword == null || inputPassword.isEmpty()) {
      pwField.requestFocusInWindow();
      return;
    }

    User                    user;
    Supplier<UserInterface> Interface;
    if (stuRadioBtn.isSelected()) {
      user = JDBCUtilities.dbop(conn -> {return Student.retrieveOne(conn, inputId);});
      Interface = StudentInterface::new;
    } else if (teaRadioBtn.isSelected()) {
      user = JDBCUtilities.dbop(conn -> {return Teacher.retrieveOne(conn, inputId);});
      Interface = TeacherInterface::new;
    } else {
      JOptionPane.showMessageDialog(this, "单选框未选中", "异常发生", JOptionPane.WARNING_MESSAGE);
      return;
    }

    if (user == null) {
      JOptionPane.showMessageDialog(this, "用户不存在", "登录失败", JOptionPane.WARNING_MESSAGE);
      return;
    }

    if (user.comparePassword(inputPassword)) {
      UserInterface panel = Interface.get();
      panel.putArgument("user", user);
      switchPanel(panel);
    } else {
      JOptionPane.showMessageDialog(this, "密码错误", "登录失败", JOptionPane.WARNING_MESSAGE);
    }
  }

  /** @see #registerBtn */
  private void clickRegister(ActionEvent actionEvent) {
    Class<? extends User> type;
    if (stuRadioBtn.isSelected()) {
      type = Student.class;
    } else if (teaRadioBtn.isSelected()) {
      type = Teacher.class;
    } else {
      JOptionPane.showMessageDialog(this, "单选框未选中", "异常发生", JOptionPane.WARNING_MESSAGE);
      return;
    }

    switchPanel(new RegisterInterface(type));
  }

  /**
   * Replace window's content panel to new user interface, take user to next step.
   */
  private void switchPanel(UserInterface Interface) {
    JFrame frame = (JFrame) SwingUtilities.windowForComponent(this);
    // replace content panel
    setVisible(false);
    Interface.showInFrame(frame);
    // no need to pack
  }

  /** Preview UI */
  public static void main(String[] args) {
    JFrame frame = new JFrame();
    UserInterface Interface = new SplashInterface();
    Interface.showInFrame(frame);
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }
}

