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
    rowSet = JDBCUtilities.getInstance().newRowSet(
        "select id, name, class_name, major_name, score from course_student l join student s on s.id = l.student where l.course = ?",
        statement -> statement.setLong(1, course.getId()));
    System.out.println("Refresh score table");
    fireTableDataChanged();
  }

  /** 创建新的行，使用用户输入的数据创建学生账号 */
  @SuppressWarnings("Duplicates")
  public void createRow(@NotNull Student student, BigDecimal score) {
    CourseStudentLink link = CourseStudentLink.createOne(student, course, score);
    try {
      try (
          PreparedStatement statement = JDBCUtilities.getInstance().createStatement(
              "insert into course_student (student, course, score) values (?, ?, ?)")) {
        // execute database update
        link.updateStatement(statement);
        statement.executeUpdate();

        System.out.println(MessageFormat.format(
            "Assign score for student {0} on course {1} to {2} points",
            student.getStudentName(), course.getName(), score));

        // update cached table model
        rowSet.execute(JDBCUtilities.getInstance().getConnection());
        // notify data change
        int rowNum = rowSet.size() - 1;
        fireTableRowsInserted(rowNum, rowNum);
      } catch (SyncProviderException e) {
        refreshTable();

        SyncResolver resolver = e.getSyncResolver();
        if (!resolver.nextConflict()) return;
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

  /**从数据库中移除一个分数记录*/
  public void removeRow(@NotNull int[] selectedRows) {
    try {
      for (int row : selectedRows) {
        rowSet.absolute(row + 1); // row number start with one
        String studentId = rowSet.getString("id");
        try (
            PreparedStatement statement = JDBCUtilities.getInstance()
                .createStatement("delete from course_student where student=? and course=?")) {
          statement.setString(1, studentId);
          statement.setLong(2, course.getId());
          statement.executeUpdate();
        }
        System.out.println(
            "Delete score for student named " + studentId + " on course " + course.getName());
        fireTableRowsDeleted(row, row);
      }
      rowSet.execute(JDBCUtilities.getInstance().getConnection());
    } catch (SQLException e) {
      JDBCUtilities.printSQLException(e);
      refreshTable();
    }
  }

  /**刷新列表，从数据库中拉取最新数据*/
  public void refreshTable() {
    // 用户没有选中左侧列表，会导致course为null
    if (course == null) return;

    try {
      rowSet.execute(JDBCUtilities.getInstance().getConnection());
    } catch (SQLException e) {
      JDBCUtilities.printSQLException(e);
    }
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
  public String getColumnName(int column) {
    return columnNames[column];
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

  /**在列表中编辑分数，同步到数据库中*/
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
      rowSet.updateBigDecimal(columnIndex + 1, newValue);
      fireTableCellUpdated(rowIndex, columnIndex);
    } catch (SQLException e) {
      JDBCUtilities.printSQLException(e);
      refreshTable();
    }
  }
}
