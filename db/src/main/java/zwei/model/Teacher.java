package zwei.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zwei.JDBCUtilities;

import java.sql.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Teacher extends User {

  private static final long serialVersionUID = -5053780242741971192L;
  @NotNull private String name;

  public void persist(Connection conn) {
    persistOne(conn, this);
  }

  public void update(Connection conn) {
    updateOne(conn, this);
  }

  /* Static Methods */

  public static Teacher createAccount(@NotNull String id, @NotNull String name,
      @NotNull String password) {
    Teacher created = new Teacher();
    created.name = name;
    created.uid = id;
    created.setPassword(password);
    return created;
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

  /**
   * 通过id获取一个Teacher
   */
  @SuppressWarnings("Duplicates")
  @Nullable
  public static Teacher retrieveOne(@NotNull Connection conn, String uid) {
    if (uid == null) return null;

    String sql = "select * from teacher where id=?";
    try (PreparedStatement statement = conn.prepareStatement(sql)) {
      statement.setString(1, uid);
      List<Teacher> list = parseResultSet(statement.executeQuery());
      return list.isEmpty() ? null : list.get(0);
    } catch (SQLException e) {
      JDBCUtilities.printSQLException(e);
    }
    return null;
  }

  public static boolean persistOne(Connection conn, Teacher stu) {
    String sql = "insert into teacher (id, password, name) values (?, ?, ?)";
    try (PreparedStatement statement = conn.prepareStatement(sql)) {
      statement.setString(1, stu.uid);
      statement.setString(2, stu.password);
      statement.setString(3, stu.name);
      statement.executeUpdate();
      return true;
    } catch (SQLException e) {
      JDBCUtilities.printSQLException(e);
    }
    return false;
  }

  public static void updateOne(Connection conn, Teacher stu) {
    String sql = "update student set password=?, name=? where id=?";
    try (PreparedStatement statement = conn.prepareStatement(sql)) {
      statement.setString(1, stu.password);
      statement.setString(2, stu.name);
      statement.setString(3, stu.uid);
      statement.executeUpdate();
    } catch (SQLException e) {
      JDBCUtilities.printSQLException(e);
    }
  }

  @NotNull
  private static List<Teacher> parseResultSet(ResultSet resultSet) {
    List<Teacher> students = new LinkedList<>();
    try {
      while (resultSet.next()) {
        // @formatter:off
        Teacher s   = new Teacher();
        s.uid       = resultSet.getString("id");
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
