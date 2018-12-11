package zwei.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zwei.JDBCUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created on 2018-12-10
 *
 * @author 九条涼果 chunxiang.huang@hypers.com
 */
public class ClassUnit implements Persistable<Long> {

  private Long id;
  private String name;
  private List<Student> students;
  private Teacher teacher;


  /* Static Methods */

  public static void createTable(@NotNull Connection conn) {
    JDBCUtilities.executeSqlFromResource(conn, "sql/init_class.ddl.sql");
  }

  /**
   * 创建一个Join引用
   *
   * @see #retrieveOne(Connection, long)
   */
  public static ClassUnit reference(long classId) {
    return null;
  }

  @Nullable
  public static ClassUnit retrieveOne(@NotNull Connection conn, long uid) {
    String sql = "select * from teacher where id=?1";
    try (PreparedStatement statement = conn.prepareStatement(sql)) {
      // statement.setString(1, uid);
      List<ClassUnit> list = parseResultSet(statement.executeQuery());
      return list.isEmpty() ? null : list.get(0);
    } catch (SQLException e) {
      JDBCUtilities.printSQLException(e);
    }
    return null;
  }

  @NotNull
  static List<ClassUnit> parseResultSet(ResultSet resultSet) {
    List<ClassUnit> students = new LinkedList<>();
    try {
      while (resultSet.next()) {
        // @formatter:off
        ClassUnit s   = new ClassUnit();
        s.id       = resultSet.getLong("uid");
        // s.name
        // s.password  = resultSet.getString("password");
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

  @Override
  public Long getId() {
    return id;
  }
}
