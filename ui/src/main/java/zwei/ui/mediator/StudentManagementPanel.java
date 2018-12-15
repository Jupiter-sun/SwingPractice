package zwei.ui.mediator;

import zwei.model.Student;
import zwei.ui.table.StudentTableModel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Created on 2018-12-12
 *
 * @author 九条涼果 chunxiang.huang@hypers.com
 */
public class StudentManagementPanel extends JPanel {

  private static final long serialVersionUID = -5906088835222512417L;

  private JTable table;
  private JButton plusBtn;
  private JButton minusBtn;
  private JButton refreshBtn;
  private JTextField searchBox;

  private StudentTableModel model;

  public StudentManagementPanel() {
    createSelf();

    model = new StudentTableModel();
    table.setModel(model);

    searchBox.getDocument().addDocumentListener(new DocumentListener() {
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
    });

    plusBtn.addActionListener(this::createRow);
    minusBtn.addActionListener(this::removeRow);
    refreshBtn.addActionListener(this::requestRefresh);
  }

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

  private void removeRow(ActionEvent actionEvent) {
    int[] selectedRows = table.getSelectedRows();
    if (selectedRows.length == 0) return;
    model.removeRow(selectedRows);
  }

  private void requestRefresh(ActionEvent actionEvent) {
    model.refreshTable();
  }

  @SuppressWarnings("MagicNumber")
  private void createSelf() {
    table = new JTable();
    setTableRender();
    // table.setDefaultRenderer(Object.class, new MyDefaultTableCellRenderer());

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
    controlPanel.setBackground(UserInterface.commonBackGround);

    setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
    setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
    setBackground(UserInterface.commonBackGround);
    add(tablePanel);
    add(controlPanel);
  }

  private void fireNameFilter() {
    String searchFor = searchBox.getText();
    System.out.println("Filter name: " + searchFor);
    model.narrowDown(searchFor);
  }

  private void setTableRender() {
    TableCellRenderer renderer = table.getDefaultRenderer(Object.class);
    table.setDefaultRenderer(Object.class, (table1, value, isSelected, hasFocus, row, column) -> {
      Component c =
          renderer.getTableCellRendererComponent(table1, value, isSelected, hasFocus, row, column);
      c.setBackground(row % 2 == 0 ? new Color(0xEFF0F1) : new Color(0xF9F9F9));
      c.setBackground(isSelected ? new Color(0xB4D1F5) : c.getBackground());
      c.setForeground(isSelected ? Color.BLACK : new Color(0x222426));
      return c;
    });
  }
}

