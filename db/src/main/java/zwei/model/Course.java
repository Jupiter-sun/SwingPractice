package zwei.model;

public class Course implements Persistable<Long> {
  private Long id;
  private String name;

  @Override
  public Long getId() {
    return id;
  }
}
