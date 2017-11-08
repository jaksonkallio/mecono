package mecono;

/**
 *
 * @author jak
 */
public class NodeAddress {

	public NodeAddress(String address) {
		this.address = address;
	}

	public NodeAddress() {
		this.address = generateAddress();
	}

	public String getAddressString() {
		return address;
	}

	private String generateAddress() {
		char[] text = new char[address_length];

		for (int i = 0; i < address_length; i++) {
			text[i] = Protocol.hex_chars[Protocol.rng.nextInt(Protocol.hex_chars.length)];
		}

		return new String(text);
	}

	private final String address;
	public static final int address_length = 16;
}
