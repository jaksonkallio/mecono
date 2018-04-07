package mecono.protocol;

/**
 *
 * @author jak
 */
public abstract class MeconoException extends Exception {
	public MeconoException() {
		super();
	}

	public MeconoException(String message) {
		super(message);
	}
	
	@Override
	public String toString(){
		return getMessage();
	}
}
