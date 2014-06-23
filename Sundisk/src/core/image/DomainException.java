package core.image;
/** signals a domain exception */
public class DomainException extends RuntimeException {
  
  public DomainException()
  {
    super();
  }

  public DomainException(String s)
  {
    super(s);
  }
}