package zwei;

import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.Nullable;

import javax.sql.RowSet;
import javax.sql.rowset.FilteredRowSet;
import javax.sql.rowset.RowSetProvider;
import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created on 2018-12-09
 *
 * @author 九条涼果 chunxiang.huang@hypers.com
 */
public final class JDBCUtilities {

  private String dbUrl;
  private String username;
  private String password;
  private static JDBCUtilities self;
  private Connection connection;

  private JDBCUtilities(String propertiesFile) {
    setProperties(propertiesFile);
  }

  public static JDBCUtilities getInstance() {
    if (self == null) {
      self = getInstance("app.properties");
    }
    return self;
  }

  public static JDBCUtilities getInstance(String propertiesFile) {
    if (self == null) {
      self = new JDBCUtilities(propertiesFile);
      self.connection = self.getConnection();
    }
    return self;
  }

  /** 连接到数据库，以sql的结果创建RowSet对象 */
  public FilteredRowSet newRowSet(@Language("sql") String sql) {
    return newRowSet(sql, null);
  }

  /** 连接到数据库，以sql的结果创建RowSet对象 */
  public FilteredRowSet newRowSet(@Language("sql") String sql,
      @Nullable StatementPromoter<? super RowSet> addOn) {
    FilteredRowSet frs;
    try {
      frs = RowSetProvider.newFactory().createFilteredRowSet();
      frs.setType(ResultSet.TYPE_SCROLL_INSENSITIVE);
      frs.setConcurrency(ResultSet.CONCUR_UPDATABLE);
      frs.setCommand(sql);
      if (addOn != null) addOn.accept(frs);
      frs.execute(connection);
    } catch (SQLException e) {
      printSQLException(e);
      throw new RuntimeException(e);
    }
    return frs;
  }

  /** 连接到数据库，以sql的结果创建RowSet对象 */
  public FilteredRowSet newRowSet() {
    FilteredRowSet frs;
    try {
      frs = RowSetProvider.newFactory().createFilteredRowSet();
      frs.setType(ResultSet.TYPE_SCROLL_INSENSITIVE);
      frs.setConcurrency(ResultSet.CONCUR_UPDATABLE);
    } catch (SQLException e) {
      printSQLException(e);
      throw new RuntimeException(e);
    }
    return frs;
  }


  /** @throws NullPointerException cannot get connection */
  @SuppressWarnings("CallToDriverManagerGetConnection")
  public Connection getConnection() {
    try {
      if (connection != null && connection.isValid(2)) return connection;

      Connection conn = DriverManager.getConnection(dbUrl, username, password);
      if (conn != null) {
        System.out.println("Connected to database");
        return conn;
      }
    } catch (SQLException e) {
      printSQLException(e);
    }

    JOptionPane.showMessageDialog(null, "无法连接到数据库", "错误", JOptionPane.ERROR_MESSAGE);
    throw new NullPointerException("connection is null");
  }

  public PreparedStatement createStatement(@Language("SQL") String sql) {
    try {
      return getConnection().prepareStatement(sql);
    } catch (SQLException e) {
      printSQLException(e);
      throw new RuntimeException();
    }
  }

  public static void executeSqlFromResource(Connection conn, String resourcePath) {
    String sql = readStringFromStream(resourcePath);

    try (Statement statement = conn.createStatement()) {
      statement.execute(sql);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static void executeUpdateSqlFromResource(Connection conn, String resourcePath) {
    String sql = readStringFromStream(resourcePath);

    try (Statement statement = conn.createStatement()) {
      statement.executeUpdate(sql);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private static String readStringFromStream(String resourcePath) {
    ClassLoader classLoader = ClassLoader.getSystemClassLoader();
    //noinspection ConstantConditions
    try (
        InputStream resourceStream = classLoader.getResourceAsStream(resourcePath);
        Reader inputStreamReader = new InputStreamReader(resourceStream, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(inputStreamReader)) {
      return reader.lines().collect(Collectors.joining());
    } catch (IOException | NullPointerException e) {
      throw new RuntimeException(e);
    }
  }

  public static void close(Connection connArg) {
    System.out.println("Releasing all open resources ...");
    try {
      if (connArg != null) {
        connArg.close();
      }
    } catch (SQLException sqlE) {
      printSQLException(sqlE);
    }
  }

  public static void close(ResultSet arg) {
    try {
      if (arg != null && !arg.isClosed()) {
        arg.close();
      }
    } catch (SQLException sqlE) {
      printSQLException(sqlE);
    }
  }

  public static void close(AutoCloseable arg) {
    try {
      if (arg != null) {
        arg.close();
      }
    } catch (SQLException sqlE) {
      printSQLException(sqlE);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /** @throws RuntimeException reading failed */
  private void setProperties(String fileName) {
    Path       path = Paths.get(fileName);
    Properties prop = new Properties();
    try (
        InputStream is = Files.exists(path) ?
            Files.newInputStream(path) :
            ClassLoader.getSystemResourceAsStream(fileName)) {
      prop.load(is);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    this.dbUrl = prop.getProperty("db.url");
    this.username = prop.getProperty("db.username");
    this.password = prop.getProperty("db.password");
  }

  /** Database operation */
  @SuppressWarnings("SpellCheckingInspection")
  public static void dbop(Consumer<? super Connection> function) {
    Connection connection = getInstance().getConnection();
    function.accept(connection);
  }

  /** Database operation */
  @SuppressWarnings("SpellCheckingInspection")
  public static <R> R dbop(Function<? super Connection, R> function) {
    Connection connection = getInstance().getConnection();
    return function.apply(connection);
  }

  public static void printWarnings(SQLWarning warning) throws SQLException {
    if (warning != null) {
      System.out.println("---Warning---");
      while (warning != null) {
        System.out.println("Message: " + warning.getMessage());
        System.out.println("SQLState: " + warning.getSQLState());
        System.out.print("Vendor error code: " + warning.getErrorCode());
        System.out.println();
        warning = warning.getNextWarning();
      }
    }
  }

  public static void printSQLException(SQLException ex) {
    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null,
        ex.getMessage(), "SQL错误", JOptionPane.ERROR_MESSAGE));
    Collection<Throwable> processed = new LinkedList<>();
    for (Throwable e : ex) {
      if (processed.contains(e)) continue;

      e.printStackTrace();
      processed.add(e);

      if (e instanceof SQLException) {
        if (ignoreSQLException(((SQLException) e).getSQLState())) {
          continue;
        }
        System.err.println("SQLState: " + ((SQLException) e).getSQLState());
        System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
      }
      System.err.println("Message: " + e.getMessage());
      Throwable t = ex.getCause();
      while (t != null) {
        processed.add(t);
        System.err.println("Cause: " + t);
        t = t.getCause();
      }
    }
  }

  @SuppressWarnings( {"LiteralAsArgToStringEquals", "RedundantIfStatement"})
  private static boolean ignoreSQLException(String sqlState) {
    if (sqlState == null) {
      System.out.println("The SQL state is not defined!");
      return false;
    }
    // X0Y32: Jar file already exists in schema
    if (sqlState.equalsIgnoreCase("X0Y32")) { return true; }
    // 42Y55: Table already exists in schema
    if (sqlState.equalsIgnoreCase("42Y55")) { return true; }

    return false;
  }

  @FunctionalInterface
  public interface StatementPromoter<T> {

    void accept(T statement) throws SQLException;
  }
}
