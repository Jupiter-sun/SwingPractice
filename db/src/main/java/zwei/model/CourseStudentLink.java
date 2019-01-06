package zwei.model;

import zwei.dao.JDBCUtilities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class CourseStudentLink implements Serializable {

  private static final long serialVersionUID = -6612984593148644436L;
  private Course course;
  private Student student;
  private BigDecimal score;

  private CourseStudentLink(){}


  public void updateInsertRow(ResultSet rowSet) throws SQLException {
    rowSet.updateLong("course", course.getId());
    rowSet.updateString("student", student.getId());
    rowSet.updateBigDecimal("score", score);
  }

  public void updateStatement(PreparedStatement statement) throws SQLException {
    statement.setString(1, student.getId());
    statement.setLong(2, course.getId());
    statement.setBigDecimal(3, score);
  }

  /* Static methods */

  public static CourseStudentLink createOne(Student student, Course course, BigDecimal score) {
    CourseStudentLink link =new CourseStudentLink();
    link.student = student;
    link.course = course;
    link.score = score;
    return link;
  }

  public static List<CourseStudentLink> getScore(Student student) {
    String sql = "select student, course, score, c.name\n"
        + "from course_student\n"
        + "     join course c on c.id = course_student.course\n"
        + "where student = ?";
    try (PreparedStatement statement = JDBCUtilities.getInstance().createStatement(sql)) {
      statement.setString(1, student.getId());
      try (ResultSet resultSet = statement.executeQuery()) {
        List<CourseStudentLink> results = new LinkedList<>();
        while (resultSet.next()) {
          String     studentId  = resultSet.getString(1);
          Long       courseId   = resultSet.getLong(2);
          BigDecimal score      = resultSet.getBigDecimal(3);
          String     courseName = resultSet.getString(4);

          CourseStudentLink link = new CourseStudentLink();
          link.student = Student.reference(studentId);
          link.course = Course.reference(courseId, courseName);
          link.score = score;
          results.add(link);
        }
        return results;
      }
    } catch (SQLException e) {JDBCUtilities.printSQLException(e);}
    return Collections.emptyList();
  }

  /* Getters */

  public Course getCourse() {
    return course;
  }

  public Student getStudent() {
    return student;
  }

  public BigDecimal getScore() {
    return score;
  }
}
