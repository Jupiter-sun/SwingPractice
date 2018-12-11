package zwei.dao;

import org.junit.Test;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created on 2018-12-09
 *
 * @author 九条涼果 chunxiang.huang@hypers.com
 */
public class BaseDaoTest {

  private static class DummyDaoImpl extends BaseDao {

  }

  @Test
  public void testConnection() throws SQLException {
    assertThat(DummyDaoImpl.initConnect("jdbc:derby:test;create=true")).isNotNull();
  }
}
