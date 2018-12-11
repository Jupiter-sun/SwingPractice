package zwei.ui.component;


import zwei.ui.mediator.SplashMediator;

public class RegisterButton implements SplashComponent {

  SplashMediator mediator;

  @Override
  public void setMediator(SplashMediator mediator) {
    this.mediator = mediator;
  }
}
