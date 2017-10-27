package mecono;

/**
 * A destination parcel is a parcel that has reached the SelfNode and is at it's final destination.
 * @author jak
 */
public class DestinationParcel extends Parcel implements MeconoSerializable{

	/**
	 * Constructor
	 *
	 * @param path_history
	 * @param originator
	 * @param signature
	 * @throws mecono.BadProtocolException
	 */
	//pathhistory,[destination,parceltype,originator,content,signature(destination+originator+content)]
	public DestinationParcel() {

	}
	
	public void setContent(String content){
		this.content = content;
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

	@Override
	public boolean equals(Object o) {
		DestinationParcel other = (DestinationParcel) o;
		return (other.getContent().equals(this.getContent()) && other.getDestination().equals(this.getDestination()) && other.getOriginator().equals(this.getOriginator()));
	}

	public String getContent() {
		return content;
	}
	
	public Node getDestination(){
		return path_history.getStop(path_history.getPathLength() - 1);
	}

	public boolean isFinalDest() {
		return destination.equals(mailbox.getOwner());
	}

	public void setMessagePiece(String message_piece) {
		if (message_piece.length() <= 140) {
			this.content = message_piece;
		} else {
			// TODO: Throw ProtocolException
		}
	}
	
	public boolean originatorIsSelf() {
		return getOriginator().equals(mailbox.getOwner());
	}

	public ParcelType getParcelType(){
		return ParcelType.UNKNOWN;
	}
	
	public void setParcelType(ParcelType parcel_type){
		this.parcel_type = parcel_type;
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
	
	private String content;
	private String payload;
	private Node destination;
	private Node originator;
	private int time_received = 0;
	private Mailbox mailbox;
	private ParcelType parcel_type = ParcelType.UNKNOWN;
}
