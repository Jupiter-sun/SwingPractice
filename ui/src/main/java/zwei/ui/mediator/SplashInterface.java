package zwei.ui.mediator;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created on 2018-12-11
 *
 * @author 九条涼果 chunxiang.huang@hypers.com
 */
public class SplashInterface extends JPanel {

  private static final long serialVersionUID = 5251739093646189753L;
  private JTextField idField;
  private JTextField pwField;

  private JRadioButton stuRadioBtn;
  private JRadioButton teaRadioBtn;

  private JButton loginBtn;
  private JButton registerBtn;

  public SplashInterface() {
    createSelf();
  }

  public void showInFrame(JFrame parent) {
    parent.setContentPane(this);
    parent.getRootPane().setDefaultButton(loginBtn);
    parent.setTitle("用户登录");
  }

  @SuppressWarnings("Duplicates")
  private void createSelf() {
    idField = new JTextField();
    pwField = new JPasswordField();
    JLabel idLabel = new JLabel(" ID:", JLabel.TRAILING);
    JLabel pwLabel = new JLabel("PWD:", JLabel.TRAILING);
    idLabel.setLabelFor(idField);
    pwLabel.setLabelFor(pwField);
    idField.addActionListener((e) -> idField.transferFocus());
    pwField.addActionListener(this::clickLogin);

    loginBtn = new JButton("登录");
    loginBtn.addActionListener(this::clickLogin);
    registerBtn = new JButton("注册");
    registerBtn.addActionListener(this::clickRegister);

    stuRadioBtn = new JRadioButton("学生");
    teaRadioBtn = new JRadioButton("教师");
    ButtonGroup buttonGroup = new ButtonGroup();
    buttonGroup.add(teaRadioBtn);
    buttonGroup.add(stuRadioBtn);
    stuRadioBtn.setSelected(true);

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
    add(Box.createGlue());
    add(topPanel);
    add(midPanel);
    add(lstPanel);
  }

  private void clickLogin(ActionEvent actionEvent) {
    System.out.println(pwField.getText());
  }

  private void clickRegister(ActionEvent actionEvent) {
    JFrame frame = (JFrame) SwingUtilities.windowForComponent(this);

    RegisterInterface registerInterface = new RegisterInterface();

    // replace content panel
    setVisible(false);
    registerInterface.showInFrame(frame);
  }
}

