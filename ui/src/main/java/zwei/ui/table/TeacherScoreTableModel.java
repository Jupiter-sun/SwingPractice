package zwei.ui.table;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zwei.JDBCUtilities;
import zwei.model.Course;
import zwei.model.CourseStudentLink;
import zwei.model.Student;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.spi.SyncProviderException;
import javax.sql.rowset.spi.SyncResolver;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.MessageFormat;

/**
 * Created on 2018-12-16
 *
 * @author 九条涼果 chunxiang.huang@hypers.com
 */
public class TeacherScoreTableModel extends AbstractTableModel {

  private static final long serialVersionUID = 8099441618718210822L;
  private static String[] columnNames = {"学号", "姓名", "班级", "专业", "分数"};

  private transient CachedRowSet rowSet;
  private Course course;

  public void setCourse(Course selectedValue) {
    course = selectedValue;
    refreshTable();
  }

  /** 创建新的行，使用用户输入的数据创建学生账号 */
  @SuppressWarnings("Duplicates")
  public void createRow(@NotNull Student student, BigDecimal score) {
    CourseStudentLink link = CourseStudentLink.createOne(student, course, score);
    try {
      try {
        rowSet.moveToInsertRow();
        int rowNum = rowSet.getRow();
        link.updateInsertRow(rowSet);
        rowSet.insertRow();
        rowSet.moveToCurrentRow();
        rowSet.acceptChanges();
        System.out.println(MessageFormat.format(
            "Assign score for student {0} on course {1} to {3} points",
            student.getStudentName(), course.getName(), score));
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
        String studentId = rowSet.getString("id"); // FIXME
        rowSet.deleteRow();
        rowSet.acceptChanges();
        System.out.println("Delete student named " + studentId);
        fireTableRowsDeleted(row, row);
      }
    } catch (SQLException e) {
      JDBCUtilities.printSQLException(e);
      refreshTable();
    }
  }

  public void refreshTable() {
    if (course == null) return;
    rowSet = JDBCUtilities.getInstance().newRowSet(
        "select id, name, class_name, major_name, score from course_student l join student s on s.id = l.student where l.course = ?",
        statement -> statement.setLong(1, course.getId()));

    System.out.println("Refresh score table");
    fireTableDataChanged();
  }

  @Override
  public int getRowCount() {
    if (rowSet == null) return 0;
    return rowSet.size();
  }

  @Override
  public int getColumnCount() {
    return columnNames.length;
  }

  @Override
  public Class<?> getColumnClass(int columnIndex) {
    if (columnIndex == columnNames.length - 1) return BigDecimal.class;
    return String.class;
  }

  @Override
  public boolean isCellEditable(int rowIndex, int columnIndex) {
    return columnIndex == columnNames.length - 1;
  }

  @Nullable
  @Override
  @SuppressWarnings("Duplicates")
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
      BigDecimal originalValue = rowSet.getBigDecimal(columnIndex + 1);
      BigDecimal newValue      = (BigDecimal) aValue;
      String     studentId     = rowSet.getString("id");
      if (originalValue != null && originalValue.compareTo(newValue) == 0) return;
      try (
          PreparedStatement statement = JDBCUtilities.getInstance().createStatement(
              "update course_student set score = ? where course=? and student =?")) {
        statement.setBigDecimal(1, newValue);
        statement.setLong(2, course.getId());
        statement.setString(3, studentId);
        statement.executeUpdate();
      }
      String studentName = rowSet.getString("name");
      String courseName  = course.getName();
      System.out.println(MessageFormat.format(
          "Update score for student {0} in course {1} from {2} to {3}", studentName, courseName,
          originalValue, newValue));
    } catch (SQLException e) {
      JDBCUtilities.printSQLException(e);
      refreshTable();
    }
  }
}
