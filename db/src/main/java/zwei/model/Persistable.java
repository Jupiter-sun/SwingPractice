package zwei.model;

import java.io.Serializable;

/**
 * Created on 2018-12-09
 *
 * @author 九条涼果 chunxiang.huang@hypers.com
 */
public interface Persistable<T extends Serializable> {

  T getId();


}
