package zwei.model;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Course implements Serializable {

  private static final long serialVersionUID = 990509107342952766L;
  @NotNull private Long id;
  @NotNull private String name;
  @NotNull private Teacher teacher;

  public static Course reference(Long courseId) {
    Course course = new Course();
    course.id = courseId;
    return course;
  }

  public static Course reference(Long courseId, String courseName) {
    Course course = new Course();
    course.id = courseId;
    course.name = courseName;
    return course;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public List<Student> getStudents() {
    return new LinkedList<>();
  }
}
