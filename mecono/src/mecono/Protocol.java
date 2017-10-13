package mecono;

import java.util.Random;

/**
 *
 * @author jak
 */
public class Protocol {
	public static final int max_nuggets_per_stream = 100;
	
	public static String generateAddress() {
		char[] text = new char[address_length];
		
		for (int i = 0; i < address_length; i++)
		{
			text[i] = hex_chars[rng.nextInt(hex_chars.length)];
		}
		
		return new String(text);
	}
	
	private static Random rng = new Random();
	private static final int address_length = 10;
	private static final char[] hex_chars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
}
