package mecono.parceling;

/**
 *
 * @author jak
 */
public class MissingParcelDetailsException extends Exception {

	public MissingParcelDetailsException() {
		super();
	}

	public MissingParcelDetailsException(String message) {
		super(message);
	}
}
