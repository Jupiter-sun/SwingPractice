package zwei.dao;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created on 2018-12-09
 *
 * @author 九条涼果 chunxiang.huang@hypers.com
 */
abstract class BaseDao {

  public static Connection initConnect(String url)
      throws SQLException {return initConnect(url, null, null);}

  public static Connection initConnect(@NotNull String url,
      @Nullable String username, @Nullable String password)
      throws SQLException {
    return DriverManager.getConnection(url, username, password);
  }

}
