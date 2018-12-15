package zwei.ui.table;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zwei.JDBCUtilities;
import zwei.model.Student;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.spi.SyncProviderException;
import javax.sql.rowset.spi.SyncResolver;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.sql.SQLException;

/**
 * Created on 2018-12-12
 *
 * @author 九条涼果 chunxiang.huang@hypers.com
 */
public class StudentTableModel extends AbstractTableModel {

  private static final long serialVersionUID = -2667246886106680138L;
  private CachedRowSet rowSet;
  private String[] columnNames = {"学号", "姓名", "班级", "专业"};
  private int rowCount;
  private int trueRowCount;

  public StudentTableModel() {
    try {
      CachedRowSet crs = JDBCUtilities.getInstance()
          .newCachedRowSet("select id, name, class_name, major_name, password from student");
      crs.execute(JDBCUtilities.getInstance().getConnection());
      rowSet = crs;

      rowSet.last();
      trueRowCount = rowCount = rowSet.getRow();
    } catch (SQLException e) {
      JDBCUtilities.printSQLException(e);
    }
  }

  /** 创建新的行，使用用户输入的数据创建学生账号 */
  public void createRow(@NotNull Student student) {
    try {
      try {
        rowSet.moveToInsertRow();
        student.updateInsertRow(rowSet);
        rowSet.insertRow();
        rowSet.moveToCurrentRow();
        rowSet.acceptChanges();
        System.out.println("Save one student named " + student.getStudentName());
      } catch (SyncProviderException e) {
        SyncResolver resolver = e.getSyncResolver();
        while (resolver.nextConflict()) {
          if (resolver.getStatus() == SyncResolver.INSERT_ROW_CONFLICT) {
            JOptionPane.showMessageDialog(null, "ID已存在", "保存失败", JOptionPane.ERROR_MESSAGE);
            return;
          }
        }
      }
    } catch (SQLException e) {
      JDBCUtilities.printSQLException(e);
      return;
    }
    rowCount = trueRowCount += 1;
    fireTableRowsInserted(rowCount, rowCount);
  }

  public void removeRow(int[] selectedRows) {
    rowCount -= 1;
  }
  @Override
  public int getRowCount() {
    return rowCount;
  }

  @Override
  public int getColumnCount() {
    return columnNames.length;
  }

  @Override
  public String getColumnName(int column) {
    return columnNames[column];
  }

  @Override
  public Class<?> getColumnClass(int columnIndex) {
    return String.class;
  }

  @Override
  public boolean isCellEditable(int rowIndex, int columnIndex) {
    return columnIndex > 0 || rowIndex >= trueRowCount;
  }

  @Nullable
  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    try {
      if (rowIndex >= trueRowCount) return null;

      rowSet.absolute(rowIndex + 1);
      Object object = rowSet.getObject(columnIndex + 1);
      return (object == null) ? null : object.toString();
    } catch (SQLException e) {
      return e.toString();
    }
  }

  @Override
  public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    super.setValueAt(aValue, rowIndex, columnIndex);
  }

  public void narrowDown(String searchFor) {

  }

  public void refreshTable() {

  }
}
