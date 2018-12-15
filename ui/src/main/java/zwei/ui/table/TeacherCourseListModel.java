package zwei.ui.table;

import org.jetbrains.annotations.Nullable;
import zwei.JDBCUtilities;
import zwei.model.Course;
import zwei.model.Teacher;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.spi.SyncProviderException;
import javax.sql.rowset.spi.SyncResolver;
import javax.swing.*;
import java.sql.SQLException;

/**
 * Created on 2018-12-15
 *
 * @author 九条涼果 chunxiang.huang@hypers.com
 */
public class TeacherCourseListModel extends AbstractListModel<Course> {

  private static final long serialVersionUID = 6995538272562660787L;

  private Teacher teacher;

  private CachedRowSet rowSet;

  public TeacherCourseListModel(Teacher teacher) {
    this.teacher = teacher;
    refreshList();
  }

  @SuppressWarnings("Duplicates")
  public void createOne(Course course) {
    try {
      try {
        rowSet.moveToInsertRow();
        int rowNum = rowSet.getRow();
        course.updateInsertRow(rowSet);
        rowSet.insertRow();
        rowSet.moveToCurrentRow();
        rowSet.acceptChanges();
        System.out.println("Save course named " + course.getName());
        fireIntervalAdded(this, rowNum, rowNum);
      } catch (SyncProviderException e) {
        refreshList();

        SyncResolver resolver = e.getSyncResolver();
        resolver.nextConflict();
        if (resolver.getStatus() == SyncResolver.INSERT_ROW_CONFLICT) {
          JOptionPane.showMessageDialog(null, "同名课程已存在", "保存失败", JOptionPane.ERROR_MESSAGE);
        } else {
          throw e;
        }
      }
    } catch (SQLException e) {
      JDBCUtilities.printSQLException(e);
    }
  }

  public void deleteOne(int index) {
    try {
      rowSet.absolute(index + 1);
      String name = rowSet.getString("name");
      rowSet.deleteRow();
      rowSet.acceptChanges();
      System.out.println("Delete course named " + name);
    } catch (SQLException e) {
      JDBCUtilities.printSQLException(e);
      refreshList();
    }
  }

  public void refreshList() {
    rowSet = JDBCUtilities.getInstance()
        .newRowSet("select id, name, teacher from course where teacher = ?",
            set -> set.setString(1, teacher.getId()));

    System.out.println("Refresh course list");
    fireContentsChanged(this, 0, rowSet.size());
  }

  @Override
  public int getSize() {
    return rowSet.size();
  }

  @Nullable
  @Override
  public Course getElementAt(int index) {
    try {
      rowSet.absolute(index + 1);
      return Course.parseOneRow(rowSet);
    } catch (SQLException e) {
      JDBCUtilities.printSQLException(e);
    }
    return null;
  }
}
