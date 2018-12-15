package zwei.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zwei.JDBCUtilities;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class Course implements Serializable {

  private static final long serialVersionUID = 990509107342952766L;
  @NotNull private Long id;
  @NotNull private String name;
  @NotNull private Teacher teacher;

  private Course() {}

  public void updateInsertRow(ResultSet rowSet) throws SQLException {
    rowSet.updateLong("id", id);
    rowSet.updateString("name", name);
    rowSet.updateString("teacher", teacher.getId());
  }

  /* Static methods */

  public static Course reference(Long courseId) {
    Course course = new Course();
    course.id = courseId;
    return course;
  }

  public static Course reference(Long courseId, String courseName) {
    Course course = new Course();
    course.id = courseId;
    course.name = courseName;
    return course;
  }

  public static Course createOne(String name, String teacherId) {
    return createOne(name, Teacher.reference(teacherId));
  }

  public static Course createOne(String name, Teacher teacher) {
    Course course = new Course();
    course.name = name;
    course.teacher = teacher;
    return course;
  }

  @NotNull
  public static List<Course> parseResultSet(ResultSet resultSet) {
    List<Course> courses = new LinkedList<>();
    try {
      while (resultSet.next()) {
        Course c = parseOneRow(resultSet);
        if (c != null) {
          courses.add(c);
        }
      }
    } catch (SQLException e) {
      JDBCUtilities.printSQLException(e);
    } finally {
      JDBCUtilities.close(resultSet);
    }
    return courses;
  }

  @Nullable
  public static Course parseOneRow(ResultSet resultSet) {
    try {
      Course c = new Course();
      c.id = resultSet.getLong("id");
      c.name = resultSet.getString("name");
      c.teacher = Teacher.reference(resultSet.getString("teacher"));
      return c;
    } catch (SQLException e) {
      JDBCUtilities.printSQLException(e);
    }
    return null;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public List<Student> getStudents() {
    return new LinkedList<>();
  }
}
