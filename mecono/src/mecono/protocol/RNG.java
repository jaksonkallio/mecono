package mecono.protocol;

/**
 * Random string generator.
 *
 * @author jak
 */
public class RNG {

	public final static String generateString(int length) {
		char[] text = new char[length];

		for (int i = 0; i < length; i++) {
			text[i] = Protocol.hex_chars[Protocol.rng.nextInt(Protocol.hex_chars.length)];
		}

		return new String(text);
	}
}
