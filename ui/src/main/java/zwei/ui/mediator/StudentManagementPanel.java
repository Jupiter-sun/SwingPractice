package zwei.ui.mediator;

import zwei.ui.table.StudentTableModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;

/**
 * Created on 2018-12-12
 *
 * @author 九条涼果 chunxiang.huang@hypers.com
 */
public class StudentManagementPanel extends JPanel {

  private static final long serialVersionUID = -5906088835222512417L;
  private Connection connection;

  private JTable table;
  private JButton plusBtn;
  private JButton minusBtn;
  private JButton refreshBtn;
  private JTextField searchBox;

  private StudentTableModel model;

  public StudentManagementPanel(Connection conn) {
    connection = conn;

    model = new StudentTableModel(conn);

    createSelf();
  }

  void createSelf() {
    table = new JTable();
    table.setModel(new DefaultTableModel(3, 3));

    searchBox = new JTextField(8);
    searchBox.setMaximumSize(searchBox.getPreferredSize());
    JLabel searchBoxLabel = new JLabel("过滤姓名:");
    searchBoxLabel.setLabelFor(searchBox);

    plusBtn = new JButton("添加");
    minusBtn = new JButton("删除");
    refreshBtn = new JButton("刷新");

    JPanel tablePanel = new JPanel(new BorderLayout());
    tablePanel.add(table, BorderLayout.CENTER);

    JPanel controlPanel = new JPanel();
    controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.LINE_AXIS));
    controlPanel.add(searchBoxLabel);
    controlPanel.add(searchBox);
    controlPanel.add(Box.createHorizontalGlue());
    controlPanel.add(refreshBtn);
    controlPanel.add(plusBtn);
    controlPanel.add(refreshBtn);

    setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
    add(tablePanel);
    add(controlPanel);
  }
}

