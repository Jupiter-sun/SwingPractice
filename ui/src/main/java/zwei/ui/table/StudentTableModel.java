package zwei.ui.table;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zwei.JDBCUtilities;
import zwei.model.Student;

import javax.sql.RowSet;
import javax.sql.rowset.FilteredRowSet;
import javax.sql.rowset.Predicate;
import javax.sql.rowset.spi.SyncProviderException;
import javax.sql.rowset.spi.SyncResolver;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Objects;

/**
 * Created on 2018-12-12
 *
 * @author 九条涼果 chunxiang.huang@hypers.com
 */
public class StudentTableModel extends AbstractTableModel {

  private static final long serialVersionUID = -2667246886106680138L;
  private transient FilteredRowSet rowSet;
  private transient MyPredicate filter;
  private String[] columnNames = {"学号", "姓名", "班级", "专业"};

  public StudentTableModel() {
    filter = new MyPredicate();
    refreshTable();
  }

  /** 创建新的行，使用用户输入的数据创建学生账号 */
  public void createRow(@NotNull Student student) {
    try {
      try {
        rowSet.moveToInsertRow();
        int rowNum = rowSet.getRow();
        student.updateInsertRow(rowSet);
        rowSet.insertRow();
        rowSet.moveToCurrentRow();
        rowSet.acceptChanges();
        System.out.println("Save student named " + student.getStudentName());
        fireTableRowsInserted(rowNum, rowNum);
      } catch (SyncProviderException e) {
        refreshTable();

        SyncResolver resolver = e.getSyncResolver();
        resolver.nextConflict();
        if (resolver.getStatus() == SyncResolver.INSERT_ROW_CONFLICT) {
          JOptionPane.showMessageDialog(null, "ID已存在", "保存失败", JOptionPane.ERROR_MESSAGE);
        } else {
          throw e;
        }
      }
    } catch (SQLException e) {
      JDBCUtilities.printSQLException(e);
    }
  }

  public void removeRow(@NotNull int[] selectedRows) {
    try {
      for (int row : selectedRows) {
        rowSet.absolute(row + 1); // row number start with one
        String name = rowSet.getString("name");
        rowSet.deleteRow();
        rowSet.acceptChanges();
        System.out.println("Delete student named " + name);
        fireTableRowsDeleted(row, row);
      }
    } catch (SQLException e) {
      JDBCUtilities.printSQLException(e);
      refreshTable();
    }
  }

  public void refreshTable() {
    rowSet = JDBCUtilities.getInstance()
        .newRowSet("select id, name, class_name, major_name, password from student");

    try {
      rowSet.setFilter(filter);
      fireTableDataChanged();
    } catch (SQLException e) {
      JDBCUtilities.printSQLException(e);
    }
  }

  public void narrowDown(String searchFor) {
    filter.setKeyword(searchFor);
    fireTableDataChanged();
  }

  @Override
  public int getRowCount() {
    int count = 0;
    try {
      rowSet.beforeFirst();
      while (rowSet.next()) {
        count++;
      }
    } catch (SQLException e) {
      JDBCUtilities.printSQLException(e);
    }
    return count;
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
    return columnIndex > 0;
  }

  @Nullable
  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    try {
      rowSet.absolute(rowIndex + 1);
      if (rowSet.isAfterLast()) {
        return null;
      }
      Object object = rowSet.getObject(columnIndex + 1);
      return (object == null) ? null : object.toString();
    } catch (SQLException e) {
      return e.getMessage();
    }
  }

  @Override
  public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    try {
      rowSet.absolute(rowIndex + 1);
      String originalValue = rowSet.getString(columnIndex + 1);
      String newValue      = (String) aValue;
      if (Objects.equals(originalValue, newValue)) return;
      rowSet.updateString(columnIndex + 1, newValue);
      rowSet.updateRow();
      rowSet.acceptChanges();
      String id  = rowSet.getString("id");
      String col = getColumnName(columnIndex);
      System.out.println(MessageFormat.format(
          "Update student id {0} column {1} from {2} to {3}", id, col, originalValue, newValue));
    } catch (SQLException e) {
      JDBCUtilities.printSQLException(e);
      refreshTable();
    }
  }

  private static class MyPredicate implements Predicate {

    private String keyword;

    void setKeyword(String keyword) {
      this.keyword = keyword;
    }

    @Override
    public boolean evaluate(RowSet rs) {
      if (keyword == null || keyword.isEmpty()) return true;

      try {
        String name = rs.getString("name");
        if (name != null && name.contains(keyword)) {
          return true;
        }
      } catch (SQLException e) {
        return false;
      }
      return false;
    }

    @Override
    public boolean evaluate(Object value, int column) {
      return true;
    }

    @Override
    public boolean evaluate(Object value, String columnName) {
      return true;
    }
  }
}
