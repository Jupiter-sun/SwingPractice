package zwei.ui.table;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zwei.model.CourseStudentLink;
import zwei.model.Student;

import javax.swing.*;
import java.util.List;

/**
 * Created on 2018-12-12
 *
 * @author 九条涼果 chunxiang.huang@hypers.com
 */
public class CourseListModel extends AbstractListModel<String>
    implements ComboBoxModel<String> {

  private static final long serialVersionUID = -4783417329489491964L;
  @Nullable private CourseStudentLink selected;
  private List<CourseStudentLink> scores;

  public CourseListModel(Student student) {
    this.scores = CourseStudentLink.getScore(student);
    selected = scores.isEmpty() ? null : scores.get(0);
  }

  @Override
  public void setSelectedItem(Object anItem) {
    this.selected = scores.stream()
        .filter(it -> it.getCourse().getName().equals(anItem))
        .findFirst().orElse(null);
  }

  @Nullable
  @Override
  public String getSelectedItem() {
    if (selected == null) return "——";
    return selected.getCourse().getName();
  }

  @Override
  public int getSize() {
    return scores.size();
  }

  @Nullable
  @Override
  public String getElementAt(int index) {
    if (0 <= index && index < scores.size()) {
      return scores.get(index).getCourse().getName();
    }
    return null;
  }

  @NotNull
  public CourseStudentLink getSelected() {
    if (selected == null) {
      throw new NullPointerException();
    }
    return selected ;
  }
}
