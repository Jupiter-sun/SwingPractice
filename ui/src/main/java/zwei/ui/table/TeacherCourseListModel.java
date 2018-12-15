package zwei.ui.table;

import zwei.JDBCUtilities;
import zwei.model.Course;
import zwei.model.Teacher;

import javax.sql.rowset.CachedRowSet;
import javax.swing.*;

/**
 * Created on 2018-12-15
 *
 * @author 九条涼果 chunxiang.huang@hypers.com
 */
public class TeacherCourseListModel extends AbstractListModel<Course> {

  private static final long serialVersionUID = 6995538272562660787L;
  private final Teacher teacher;

  private CachedRowSet rowSet;

  public TeacherCourseListModel(Teacher teacher) {
    this.teacher = teacher;
    refreshList();
  }

  public void refreshList() {
    rowSet = JDBCUtilities.getInstance()
        .newRowSet("select id, name, teacher from course where teacher = ?",
            set -> set.setString(1, teacher.getId()));

    fireContentsChanged(this, 0, rowSet.size());
  }

  @Override
  public int getSize() {
    return 0;
  }

  @Override
  public Course getElementAt(int index) {
    return null;
  }
}
