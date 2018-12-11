package zwei.model;

import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zwei.JDBCUtilities;

import java.sql.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Teacher extends User {

  @NotNull private String name;
  @NotNull private List<ClassUnit> taughtClasses;

  public void persist(Connection conn) {
    persistOne(conn, this);
  }

  public void update(Connection conn) {
    updateOne(conn, this);
  }

  /* Static Methods */

  public static void createTable(@NotNull Connection conn) {
    JDBCUtilities.executeSqlFromResource(conn, "sql/init_teacher.ddl.sql");
  }

  @NotNull
  public static List<Teacher> retrieveALl(Connection conn) {
    try (
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from teacher")
    ) {
      return parseResultSet(resultSet);
    } catch (SQLException e) {
      JDBCUtilities.printSQLException(e);
    }
    return Collections.emptyList();
  }

  @SuppressWarnings("Duplicates")
  @Nullable
  public static Teacher retrieveOne(@NotNull Connection conn, String uid) {
    String sql = "select * from teacher where id=?1";
    try (PreparedStatement statement = conn.prepareStatement(sql)) {
      statement.setString(1, uid);
      List<Teacher> list = parseResultSet(statement.executeQuery());
      return list.isEmpty() ? null : list.get(0);
    } catch (SQLException e) {
      JDBCUtilities.printSQLException(e);
    }
    return null;
  }

  public static void persistOne(Connection conn, Teacher stu) {
    String sql = "insert into teacher (id, password, name) values (?1, ?2, ?3)";
    populateStatement(conn, stu, sql);
  }

  public static void updateOne(Connection conn, Teacher stu) {
    String sql = "update student set password=?2, name=?3, class_id=?4, subject=?5 where id = ?1";
    populateStatement(conn, stu, sql);
  }

  private static void populateStatement(Connection conn, Teacher stu, @Language("sql") String sql) {
    try (PreparedStatement statement = conn.prepareStatement(sql)) {
      statement.setString(1, stu.uid);
      statement.setString(2, stu.password);
      statement.setString(3, stu.name);
      statement.executeUpdate();
    } catch (SQLException e) {
      JDBCUtilities.printSQLException(e);
    }
  }

  @NotNull
  static List<Teacher> parseResultSet(ResultSet resultSet) {
    List<Teacher> students = new LinkedList<>();
    try {
      while (resultSet.next()) {
        // @formatter:off
        Teacher s   = new Teacher();
        s.uid       = resultSet.getString("uid");
        s.password  = resultSet.getString("password");
        s.name      = resultSet.getString("name");
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

  /** 获取教师姓名 */
  public String getTeacherName() {
    return name;
  }
}
