package zwei.ui.mediator;

import zwei.ui.component.SplashComponent;

import java.awt.event.ActionEvent;

public interface SplashMediator extends Mediator {

  void registerComponent(SplashComponent component);

  void clickLogin(ActionEvent actionEvent);

  void clickRegister(ActionEvent actionEvent);

  void switchToStudentPanel();

  void switchToTeacherPanel();

}
