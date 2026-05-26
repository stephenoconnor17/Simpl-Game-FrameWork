package exceptions;

/** Thrown when adding a component to an entity that already has one of the same type. */
public class DuplicateComponentException extends RuntimeException {
	public DuplicateComponentException(String msg){
		super(msg);
	}
}
