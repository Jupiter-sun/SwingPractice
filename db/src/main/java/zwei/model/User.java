package zwei.model;

import org.jetbrains.annotations.NotNull;

import javax.xml.bind.DatatypeConverter;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created on 2018-12-09
 *
 * @author 九条涼果 chunxiang.huang@hypers.com
 */
public abstract class User implements Serializable {

  private static final long serialVersionUID = -6494928338170854982L;
  @NotNull protected String uid;
  @NotNull protected String password;

  public String getId() {
    return uid;
  }

  protected void setPassword(@NotNull String password) {
    this.password = hashString(password);
  }

  public boolean comparePassword(@NotNull String rawPassword) {
    String anObject = hashString(rawPassword);
    return password.equals(anObject);
  }

  /**
   * 摘要算法对密码取哈希值
   */
  private static String hashString(String input) {
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");

      byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
      return DatatypeConverter.printHexBinary(digest);
    } catch (NoSuchAlgorithmException e) {throw new RuntimeException(e);}
  }

  /* Getters */

  protected String getUid() {
    return uid;
  }

  protected String getPassword() {
    return password;
  }
}
