package zwei;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Properties;
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

  private JDBCUtilities() {}

  @SuppressWarnings("CallToDriverManagerGetConnection")
  public Connection getConnection() throws SQLException {
    Connection connection = DriverManager.getConnection(dbUrl, username, password);
    System.out.println("Connected to database");
    return connection;
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

  private void setProperties(String fileName) throws IOException {
    Properties prop = new Properties();
    Path path = Paths.get(fileName);
    try (InputStream fis = Files.newInputStream(path)) {
      prop.load(fis);
    }

    this.dbUrl = prop.getProperty("db.url");
    this.username = prop.getProperty("db.username");
    this.password = prop.getProperty("db.password");

    System.out.println("Set the following properties:");
    System.out.println("username: " + username);
    System.out.println("password: " + password);
    System.out.println("url string: " + dbUrl);
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
    for (Throwable e : ex) {
      e.printStackTrace();

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
}
