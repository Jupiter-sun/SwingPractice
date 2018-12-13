package zwei.ui.table;

import org.jetbrains.annotations.Nullable;
import zwei.JDBCUtilities;

import javax.sql.rowset.CachedRowSet;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * Created on 2018-12-12
 *
 * @author 九条涼果 chunxiang.huang@hypers.com
 */
public class StudentTableModel implements TableModel {

  private CachedRowSet rowSet;
  private String[] columnNames;
  private int rowCount;
  private ResultSetMetaData metaData;

  public StudentTableModel(Connection connection) {
    try {
      CachedRowSet crs = JDBCUtilities.getInstance().newCachedRowSet("select * from student");
      crs.execute(connection);
      rowSet = crs;
      metaData = rowSet.getMetaData();

      columnNames = new String[metaData.getColumnCount()];
      for (int i = 0; i < metaData.getColumnCount(); i++) {
        columnNames[i] = metaData.getColumnName(i);
      }

      rowSet.last();
      rowCount = rowSet.getRow();
    } catch (SQLException e) {
      JDBCUtilities.printSQLException(e);
    }
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
  public String getColumnName(int columnIndex) {
    return columnNames[columnIndex];
  }

  @Override
  public Class<?> getColumnClass(int columnIndex) {
    return String.class;
  }

  @Override
  public boolean isCellEditable(int rowIndex, int columnIndex) {
    return false;
  }

  @Nullable
  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    try {
      rowSet.absolute(rowIndex);
      Object object = rowSet.getObject(columnIndex + 1);
      return (object == null) ? null : object.toString();
    } catch (SQLException e) {
      return e.toString();
    }
  }

  @Override
  public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

  }

  @Override
  public void addTableModelListener(TableModelListener l) {

  }

  @Override
  public void removeTableModelListener(TableModelListener l) {

  }
}
