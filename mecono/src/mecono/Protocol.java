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
	
	public static int getEpochMinute(){
		return (int) (System.currentTimeMillis() / 60000L);
	}
	
	public static long getEpochSecond(){
		return (int) (System.currentTimeMillis() / 1000L);
	}
	
	public static int elapsedMinutes(int since){
		return getEpochMinute() - since;
	}

	public static final Random rng = new Random();
	public static final char[] hex_chars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
	public static final int parcel_unique_id_length = 6;
	public static final ParcelType[] parcel_type_codes = {ParcelType.PING, ParcelType.PING_RESPONSE, ParcelType.FIND, ParcelType.FIND_RESPONSE, ParcelType.DATA, ParcelType.DATA_RECEIPT, ParcelType.COMMUNITY, ParcelType.COMMUNITY_RESPONSE, ParcelType.UNKNOWN};
}
