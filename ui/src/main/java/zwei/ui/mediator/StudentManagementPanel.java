package zwei.ui.mediator;

import zwei.model.Student;
import zwei.model.Teacher;
import zwei.ui.UiHelper;
import zwei.ui.dialog.CreateStudentDialog;
import zwei.ui.table.StudentTableModel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * 教师界面，学生管理面板
 * Created on 2018-12-12
 *
 * @author 九条涼果 chunxiang.huang@hypers.com
 */
public class StudentManagementPanel extends JPanel {

  private static final long serialVersionUID = -5906088835222512417L;

  private JTable table;
  /** 添加学生按钮 */
  private JButton plusBtn;
  /** 移除学生按钮 */
  private JButton minusBtn;
  /** 刷新列表按钮 */
  private JButton refreshBtn;
  /** 过滤器输入框 */
  private JTextField searchBox;

  private StudentTableModel model;

  public StudentManagementPanel() {
    createSelf();

    model = new StudentTableModel();
    table.setModel(model);

    searchBox.getDocument().addDocumentListener(new MyDocumentListener());
    plusBtn.addActionListener(this::createRow);
    minusBtn.addActionListener(this::removeRow);
    refreshBtn.addActionListener(this::requestRefresh);
  }

  /**
   * 传递参数，现在登录的是哪个老师。
   * 这个面板暂时不需要用到这个参数
   */
  public void setUser(Teacher teacher) { }

  /** 跳转到这个面板的时候，给窗体设置菜单栏 */
  @SuppressWarnings("Duplicates")
  public void setMenubar(JMenuBar menuBar) {
    JMenu     menu        = new JMenu("学生管理");
    JMenuItem plusMenu    = new JMenuItem("添加学生");
    JMenuItem minusMenu   = new JMenuItem("删除选定学生");
    JMenuItem refreshMenu = new JMenuItem("刷新表格");

    plusMenu.addActionListener(this::createRow);
    minusMenu.addActionListener(this::removeRow);
    refreshMenu.addActionListener(this::requestRefresh);
    menu.add(plusMenu);
    menu.add(minusMenu);
    menu.add(refreshMenu);
    menuBar.add(menu);
  }

  /** 处理用户点击添加按钮的事件，弹出对话框 */
  private void createRow(ActionEvent actionEvent) {
    CreateStudentDialog dialog = new CreateStudentDialog();
    dialog.setModal(true);
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
    Student student = dialog.getUserInput();
    if (student != null) {
      model.createRow(student);
    }
  }

  /** 处理用户点击移除按钮的事件，支持多选 */
  private void removeRow(ActionEvent actionEvent) {
    int[] selectedRows = table.getSelectedRows();
    if (selectedRows.length == 0) return;
    model.removeRow(selectedRows);
  }

  /** 处理用户点击刷新按钮的事件 */
  private void requestRefresh(ActionEvent actionEvent) {
    model.refreshTable();
  }

  @SuppressWarnings("MagicNumber")
  private void createSelf() {
    table = new JTable();
    UiHelper.setZebraStyle(table, String.class);

    searchBox = new JTextField(8);
    searchBox.setMaximumSize(searchBox.getPreferredSize());
    JLabel searchBoxLabel = new JLabel("过滤姓名:");
    searchBoxLabel.setLabelFor(searchBox);

    plusBtn = new JButton("添加");
    minusBtn = new JButton("删除");
    refreshBtn = new JButton("刷新");

    JPanel tablePanel = new JPanel(new BorderLayout());
    table.setFillsViewportHeight(true);
    tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);
    Dimension preferredSize = tablePanel.getPreferredSize();
    preferredSize.height = 300;
    tablePanel.setPreferredSize(preferredSize);

    JPanel controlPanel = new JPanel();
    controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.LINE_AXIS));
    controlPanel.add(searchBoxLabel);
    controlPanel.add(searchBox);
    controlPanel.add(Box.createHorizontalGlue());
    controlPanel.add(refreshBtn);
    controlPanel.add(plusBtn);
    controlPanel.add(minusBtn);
    controlPanel.setBackground(UserInterface.commonBackground);

    setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
    setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
    setBackground(UserInterface.commonBackground);
    add(tablePanel);
    add(controlPanel);
  }

  /**处理搜索框输入*/
  private class MyDocumentListener implements DocumentListener {

    /**每次输入框更新，都执行过滤*/
    private void fireNameFilter() {
      String searchFor = searchBox.getText();
      System.out.println("Filter name: " + searchFor);
      model.narrowDown(searchFor);
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
      fireNameFilter();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
      fireNameFilter();
    }

    @Override
    public void changedUpdate(DocumentEvent e) { }
  }
}

