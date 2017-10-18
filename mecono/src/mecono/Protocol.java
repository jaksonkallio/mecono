package mecono;

import java.util.Random;

/**
 *
 * @author jak
 */
public class Protocol {

	public static final int max_nuggets_per_stream = 100;
	public static final int pallet_id_length = 5;

	public static void validatePalletID(String stream_id) throws BadProtocolException {
		if (stream_id.length() != pallet_id_length) {
			throw new BadProtocolException("Invalid nugget stream ID.");
		}
	}
	
	public static PalletType unserializePalletType(String representation) throws BadProtocolException {
		switch (representation) {
			case "p":
				return PalletType.PING;
			case "d":
				return PalletType.DATA;
			case "f":
				return PalletType.FIND;
			default:
				throw new BadProtocolException("Unknown nugget stream type.");
		}
	}
	
	public static int getEpochMinute(){
		return (int) (System.currentTimeMillis() / 60000L);
	}

	public static final Random rng = new Random();
	public static final char[] hex_chars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
}
