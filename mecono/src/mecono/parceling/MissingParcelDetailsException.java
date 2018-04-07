package mecono.parceling;

import mecono.protocol.MeconoException;

/**
 *
 * @author jak
 */
public class MissingParcelDetailsException extends MeconoException {

	public MissingParcelDetailsException() {
		super();
	}

	public MissingParcelDetailsException(String message) {
		super(message);
	}
}
