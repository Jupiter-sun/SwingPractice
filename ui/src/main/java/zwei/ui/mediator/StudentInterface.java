package zwei.ui.mediator;

import zwei.model.Course;

import javax.swing.*;

/**
 * Created on 2018-12-11
 *
 * @author 九条涼果 chunxiang.huang@hypers.com
 */
public class StudentInterface extends JPanel {

  private static final long serialVersionUID = -3427522772908306608L;

  private JLabel idLabel;
  private JLabel nmLabel;
  private JLabel clLabel;
  private JLabel sjLabel;

  private JButton searchBtn;
  private JComboBox<Course> courseDropdown;
  private JTable scoreDisplay;
}
