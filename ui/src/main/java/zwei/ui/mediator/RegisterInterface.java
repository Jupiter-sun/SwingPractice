package zwei.ui.mediator;

import zwei.JDBCUtilities;
import zwei.model.Student;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class RegisterInterface extends JPanel implements UserInterface {

  private static final long serialVersionUID = -5795654569586694048L;

  private JTextField idField;
  private JTextField pwField;
  private JTextField nmField;

  private JButton backBtn;
  private JButton registerBtn;

  private Memento parentState;

  public RegisterInterface() {
    createSelf();
  }

  @Override
  public void showInFrame(JFrame parent) {
    parentState = backup(parent);
    parent.setContentPane(this);
    parent.getRootPane().setDefaultButton(registerBtn);
    parent.setTitle("用户注册");
  }

  @Override
  public void putArgument(String key, Object value) { }

  @SuppressWarnings("Duplicates")
  private void createSelf() {
    idField = new JTextField();
    nmField = new JTextField();
    pwField = new JPasswordField();
    JLabel idLabel = new JLabel("学号:", JLabel.TRAILING);
    JLabel nmLabel = new JLabel("姓名:", JLabel.TRAILING);
    JLabel pwLabel = new JLabel("密码:", JLabel.TRAILING);
    idLabel.setLabelFor(idField);
    nmLabel.setLabelFor(nmField);
    pwLabel.setLabelFor(pwField);
    idField.addActionListener((e) -> idField.transferFocus());
    nmField.addActionListener((e) -> nmField.transferFocus());
    pwField.addActionListener(this::clickRegister);

    backBtn = new JButton("返回");
    backBtn.addActionListener(this::backward);
    registerBtn = new JButton("注册");
    registerBtn.addActionListener(this::clickRegister);

    JPanel midPanel = new JPanel(new SpringLayout());
    midPanel.add(idLabel);
    midPanel.add(idField);
    midPanel.add(nmLabel);
    midPanel.add(nmField);
    midPanel.add(pwLabel);
    midPanel.add(pwField);
    SpringUtilities.makeCompactGrid(midPanel, 3, 2, 6, 6, 6, 6);

    JPanel lstPanel = new JPanel();
    lstPanel.add(backBtn);
    lstPanel.add(registerBtn);

    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    setBorder(new EmptyBorder(10, 10, 10, 10));
    add(midPanel);
    add(lstPanel);
  }

  private void clickRegister(ActionEvent actionEvent) {
    String inputId       = idField.getText();
    String inputName     = nmField.getText();
    String inputPassword = pwField.getText();

    Student newlyCreated = Student.createAccount(inputId, inputName, inputPassword);
    boolean success = JDBCUtilities.dbop((conn) -> {return Student.persistOne(conn, newlyCreated);});
    if (success) {
      backward(actionEvent); // return to previous page
    } else {
      idField.transferFocus();
      idField.selectAll();
    }
  }

  private void backward(ActionEvent actionEvent) {
    if (parentState != null) {
      parentState.revert();
    }
  }

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
        upLayer.setVisible(true);
      }
    }
  }
}
