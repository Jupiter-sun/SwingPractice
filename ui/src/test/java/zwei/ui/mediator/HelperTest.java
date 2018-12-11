package zwei.ui.mediator;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Created on 2018-12-11
 *
 * @author 九条涼果 chunxiang.huang@hypers.com
 */
public class HelperTest {

  @Test
  public void testRegisterComponent() {
    Dummy dummy = new Dummy();
    Helper.registerComponent(dummy, "a", String.class);
    assertThat(dummy.a).isNotNull();

    Helper.registerComponent(dummy, 2, Integer.class);
    assertThat(dummy.b).isNotNull();

    Helper.registerComponent(dummy, "c", String.class);
    assertThat(dummy.a).isNotNull().isEqualTo("a");
    assertThat(dummy.c).isNotNull().isEqualTo("c");

    assertThatThrownBy(() -> Helper.registerComponent(dummy, "b", Integer.class))
        .isNotNull();
  }

  private static class Dummy {

    private String a;
    private Integer b;
    private String c;
  }
}
