package zwei.model;

import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zwei.JDBCUtilities;

import java.sql.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Student extends User {

  @NotNull private String name;
  @NotNull private ClassUnit belongClass;
  @NotNull private String subject;
  @NotNull private List<Course> joinedCourses = new LinkedList<>();

  public void persist(Connection conn) {
    persistOne(conn, this);
  }

  public void update(Connection conn) {
    updateOne(conn, this);
  }

  /* Static Methods */

  public static void createTable(@NotNull Connection conn) {
    JDBCUtilities.executeSqlFromResource(conn, "sql/init_student.ddl.sql");
  }

  public static void populateTable(@NotNull Connection conn) {
    JDBCUtilities.executeUpdateSqlFromResource(conn, "sql/dump_student.ddl.sql");
  }

  @NotNull
  public static List<Student> retrieveALl(Connection conn) {
    try (
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from student")
    ) {
      return parseResultSet(resultSet);
    } catch (SQLException e) {
      JDBCUtilities.printSQLException(e);
    }
    return Collections.emptyList();
  }

  @SuppressWarnings("Duplicates")
  @Nullable
  public static Student retrieveOne(@NotNull Connection conn, String uid) {
    String sql = "select * from student where id=?1";
    try (PreparedStatement statement = conn.prepareStatement(sql)) {
      statement.setString(1, uid);
      List<Student> list = parseResultSet(statement.executeQuery());
      return list.isEmpty() ? null : list.get(0);
    } catch (SQLException e) {
      JDBCUtilities.printSQLException(e);
    }
    return null;
  }

  public static void persistOne(Connection conn, Student stu) {
    String sql = "insert into student (id, password, name, class_id, subject) "
        + "values (?1, ?2, ?3, ?4, ?5)";
    populateStatement(conn, stu, sql);
  }

  public static void updateOne(Connection conn, Student stu) {
    String sql = "update student set password=?2, name=?3, class_id=?4, subject=?5 where id = ?1";
    populateStatement(conn, stu, sql);
  }

  private static void populateStatement(Connection conn, Student stu, @Language("sql") String sql) {
    try (PreparedStatement statement = conn.prepareStatement(sql)) {
      statement.setString(1, stu.uid);
      statement.setString(2, stu.password);
      statement.setString(3, stu.name);
      statement.setLong(4, stu.belongClass.getId());
      statement.setString(5, stu.subject);
      statement.executeUpdate();
    } catch (SQLException e) {
      JDBCUtilities.printSQLException(e);
    }
  }

  @NotNull
  private static List<Student> parseResultSet(ResultSet resultSet) {
    List<Student> students = new LinkedList<>();
    try {
      while (resultSet.next()) {
        // @formatter:off
        Student s     = new Student();
        s.uid         = resultSet.getString("uid");
        s.password    = resultSet.getString("password");
        s.name        = resultSet.getString("name");
        s.belongClass = ClassUnit.reference(resultSet.getLong("class_id"));
        s.subject     = resultSet.getString("subject");
        // @formatter:on
        students.add(s);
      }
    } catch (SQLException e) {
      JDBCUtilities.printSQLException(e);
    } finally {
      JDBCUtilities.close(resultSet);
    }
    return students;
  }

  /* Getters */

  /** 获取学生学号 */
  public String getStudentId() {
    return uid;
  }

  /** 获取学生姓名 */
  public String getStudentName() {
    return name;
  }

  /** 获取学生班级 */
  public ClassUnit getStudentGrade() {
    return belongClass;
  }

  /** 获取学生专业 */
  public String getStudentSubject() {
    return subject;
  }

}

