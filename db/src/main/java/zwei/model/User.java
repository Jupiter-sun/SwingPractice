package zwei.model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 2018-12-09
 *
 * @author 九条涼果 chunxiang.huang@hypers.com
 */
public abstract class User implements Persistable<String> {

  protected String uid;
  @NotNull protected String password;

  @Override
  public String getId() {
    return uid;
  }


  protected void setPassword(@NotNull String password) {
    this.password = hashString(password);
  }

  public boolean comparePassword(@NotNull String rawPassword) {
    return password.equals(hashString(rawPassword));
  }

  /**
   * 摘要算法对密码取哈希值
   */
  private static String hashString(String input) {
    return input;
  }

  /* Getters */

  protected String getUid() {
    return uid;
  }

  protected String getPassword() {
    return password;
  }
}
