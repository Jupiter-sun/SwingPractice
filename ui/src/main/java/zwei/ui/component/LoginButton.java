package zwei.ui.component;

import zwei.ui.mediator.SplashMediator;

import javax.swing.*;

/**
 * Created on 2018-12-11
 *
 * @author 九条涼果 chunxiang.huang@hypers.com
 */
public class LoginButton extends JButton implements SplashComponent {

  private static final long serialVersionUID = 2827066534958960911L;
  SplashMediator mediator;

  @Override
  public void setMediator(SplashMediator mediator) {
    this.mediator = mediator;
  }
}

