package mecono;

import org.json.JSONObject;

/**
 * A destination parcel is a parcel that has reached the SelfNode and is at it's
 * final destination.
 *
 * @author jak
 */
public class DestinationParcel extends Parcel {

	/**
	 * Constructor
	 */
	//pathhistory,[destination,parceltype,originator,content,signature(destination+originator+content)]
	public DestinationParcel() {
		generateUniqueID();
	}

	public void generateUniqueID() {
		char[] text = new char[Protocol.parcel_unique_id_length];

		for (int i = 0; i < Protocol.parcel_unique_id_length; i++) {
			text[i] = Protocol.hex_chars[Protocol.rng.nextInt(Protocol.hex_chars.length)];
		}

		this.unique_id = new String(text);
	}

	public void updateReceivedTime() {
		time_received = Protocol.getEpochMinute();
	}

	public int getTimeReceived() {
		return time_received;
	}

	public int age() {
		return Math.max(Protocol.getEpochMinute() - time_received, 0);
	}

	public Node getDestination() {
		if (path == null) {
			return this.destination;
		} else {
			return path_history.getStop(path_history.getPathLength() - 1);
		}
	}

	public void setDestination(RemoteNode destination) throws BadProtocolException {
		if (!isInOutbox()) {
			if (path_history == null) {
				this.destination = destination;
			}
		}
	}

	public boolean isFinalDest() {
		return destination.equals(mailbox.getOwner());
	}

	public boolean originatorIsSelf() {
		return getOriginator().equals(mailbox.getOwner());
	}

	public ParcelType getParcelType() {
		return ParcelType.UNKNOWN;
	}

	public void setParcelType(ParcelType parcel_type) {
		if (!isInOutbox()) {
			this.parcel_type = parcel_type;
		}
	}

	public static ParcelType unserializePalletType(String representation) throws BadProtocolException {
		switch (representation) {
			case "p":
				return ParcelType.PING;
			case "pr":
				return ParcelType.PING_RESPONSE;
			case "d":
				return ParcelType.DATA;
			case "dr":
				return ParcelType.DATA_RECEIPT;
			case "f":
				return ParcelType.FIND;
			case "fr":
				return ParcelType.FIND_RESPONSE;
			default:
				throw new BadProtocolException("Unknown pallet type.");
		}
	}

	public boolean isInOutbox() {
		// TODO: A better function that actually checks if this parcel is in the mailbox's outbox, versus just checking if there's a variable that says it is.
		return in_outbox;
	}

	public void findIdealPath() {
		if (!isFinalDest() && (getDestination() instanceof RemoteNode)) {
			setPath(((RemoteNode) getDestination()).getIdealPath());
		}
	}

	public boolean hasCompletePath() {
		return (path != null && path.getPathLength() > 2);
	}

	/**
	 * Place the parcel in the outbox. The mailbox will de
	 *
	 * @throws UnknownResponsibilityException
	 */
	public void placeInOutbox() throws UnknownResponsibilityException {
		if (!isInOutbox()) {
			if (!getOriginator().equals(this)) {
				throw new UnknownResponsibilityException("The self node is not the originator of the parcel to send.");
			}

			if (getDestination().equals(this)) {
				throw new UnknownResponsibilityException("The destination of a parcel to send cannot be the self node.");
			}

			mailbox.placeInOutbox(this);
		}
	}

	public void setInOutbox() {
		in_outbox = true;
	}

	@Override
	public RemoteNode getNextNode() {
		if (originatorIsSelf()) {
			// Originator -> neighbor -> node 2 -> node 3 -> ... -> destination
			return (RemoteNode) path.getStop(1);
		} else {
			// There is no next node, if the self node is not the first node in the path. 
			return null;
		}
	}

	/**
	 * Whether this kind of parcel can be sent without a valid/tested path.
	 *
	 * @return
	 */
	public boolean requiresTestedPath() {
		return mailbox.getOwner().require_tested_path_before_send;
	}

	public String getUniqueID() {
		return unique_id;
	}

	public String getSignature() {
		return signature;
	}

	@Override
	public JSONObject serialize() {
		JSONObject serialized = new JSONObject();

		serialized = serialized.put("path_history", getPath());
		serialized = serialized.put("destination", getDestination().getAddress());
		serialized = serialized.put("parcel_type", Parcel.getParcelTypeCode(parcel_type));
		serialized = serialized.put("unique_id", getUniqueID());
		serialized = serialized.put("official_path", getPath());
		serialized = serialized.put("content", getSerializedContent());
		serialized = serialized.put("signature", getSignature());

		return serialized;
	}

	public JSONObject getSerializedContent() {
		JSONObject json_content = new JSONObject();
		json_content = json_content.put("data", "empty");
		return json_content;
	}

	private String payload;
	private Node destination;
	private Node originator;
	private int time_received = 0;
	private Mailbox mailbox;
	private boolean in_outbox;
	private String unique_id;
	private String signature;
	private ParcelType parcel_type = ParcelType.UNKNOWN;
}
