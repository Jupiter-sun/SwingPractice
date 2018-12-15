package zwei.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zwei.JDBCUtilities;

import java.sql.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Student extends User {

  private static final long serialVersionUID = 5911518347954195350L;

  @NotNull private String name;
  @NotNull private String className;
  @NotNull private String majorName;

  private Student() {}

  public static Student reference(String studentId) {
    Student student = new Student();
    student.uid = studentId;
    return student;
  }

  public void updateInsertRow(ResultSet rowSet) throws SQLException {
    rowSet.updateString("id", uid);
    rowSet.updateString("password", password);
    rowSet.updateString("name", name);
    rowSet.updateString("class_name", className);
    rowSet.updateString("major_name", majorName);
  }

  public void persist(Connection conn) {
    persistOne(conn, this);
  }

  public void update(Connection conn) {
    updateOne(conn, this);
  }

  /* Static Methods */

  public static Student createAccount(@NotNull String id, @NotNull String name,
      @NotNull String password) {
    Student created = new Student();
    created.name = name;
    created.uid = id;
    created.setPassword(password);
    return created;
  }

  public static Student createAccount(@NotNull String id, @NotNull String name,
      @NotNull String password, @NotNull String className, @NotNull String majorName) {
    Student created = new Student();
    created.name = name;
    created.uid = id;
    created.setPassword(password);
    created.className = className;
    created.majorName = majorName;
    return created;
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

  /**
   * 通过id获取一个Student
   */
  @SuppressWarnings("Duplicates")
  @Nullable
  public static Student retrieveOne(Connection conn, String uid) {
    if (uid == null) return null;

    String sql = "select * from student where id=?";
    try (PreparedStatement statement = conn.prepareStatement(sql)) {
      statement.setString(1, uid);
      List<Student> list = parseResultSet(statement.executeQuery());
      return list.isEmpty() ? null : list.get(0);
    } catch (SQLException e) {
      JDBCUtilities.printSQLException(e);
    }
    return null;
  }

  public static boolean persistOne(Connection conn, Student stu) {
    String sql =
        "insert into student (id, password, name, class_name, major_name) values (?, ?, ?, ?, ?)";
    try (PreparedStatement statement = conn.prepareStatement(sql)) {
      statement.setString(1, stu.uid);
      statement.setString(2, stu.password);
      statement.setString(3, stu.name);
      statement.setString(4, stu.className);
      statement.setString(5, stu.majorName);
      statement.executeUpdate();
      return true;
    } catch (SQLException e) {
      JDBCUtilities.printSQLException(e);
    }
    return false;
  }

  public static void updateOne(Connection conn, Student stu) {
    String sql = "update student set password=?, name=?, class_name=?, major_name=? where id=?";
    try (PreparedStatement statement = conn.prepareStatement(sql)) {
      statement.setString(1, stu.password);
      statement.setString(2, stu.name);
      statement.setString(3, stu.className);
      statement.setString(4, stu.majorName);
      statement.setString(5, stu.uid);
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

        Student s     = new Student();
        s.uid         = resultSet.getString("id");
        s.password    = resultSet.getString("password");
        s.name        = resultSet.getString("name");
        s.className = resultSet.getString("class_name");
        s.majorName = resultSet.getString("major_name");

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
  public String getStudentGrade() {
    return className;
  }

  /** 获取学生专业 */
  public String getStudentSubject() {
    return majorName;
  }

}

