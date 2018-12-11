package zwei.model;

public class Score implements Persistable<Long> {

  private  Long id;

  @Override
  public Long getId() {
    return id;
  }
}
