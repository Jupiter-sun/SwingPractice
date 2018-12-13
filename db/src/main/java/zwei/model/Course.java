package zwei.model;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public class Course {

  @NotNull private Long id;
  @NotNull private String name;
  @NotNull private Teacher teacher;

  public Long getId() {
    return id;
  }

  public List<Student> getStudents() {
    return new LinkedList<>();
  }
}
