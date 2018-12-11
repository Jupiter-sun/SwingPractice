package zwei.ui.mediator;

import zwei.ui.component.SplashComponent;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created on 2018-12-11
 *
 * @author 九条涼果 chunxiang.huang@hypers.com
 */
@Retention(RetentionPolicy.RUNTIME)
@interface Reference {

  Class<? extends SplashComponent> value();
}
