package mecono;

/**
 * A destination parcel is a parcel that has reached the SelfNode and is at it's final destination.
 * @author jak
 */
public class DestinationParcel extends Parcel implements MeconoSerializable{

	/**
	 * Constructor
	 */
	//pathhistory,[destination,parceltype,originator,content,signature(destination+originator+content)]
	public DestinationParcel() {
		
	}
	
	public void setContent(String content){
		if(!isInOutbox()){
			this.content = content;
		}
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
		if(path == null){
			return this.destination;
		}else{
			return path_history.getStop(path_history.getPathLength() - 1);
		}
	}
	
	public void setDestination(RemoteNode destination) throws BadProtocolException {
		if(!isInOutbox()){
			if(path_history == null){
				this.destination = destination;
			}
		}
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
		if(!isInOutbox()){
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
	
	public boolean isInOutbox(){
		// TODO: A better function that actually checks if this parcel is in the mailbox's outbox, versus just checking if there's a variable that says it is.
		return in_outbox;
	}
	
	public void findIdealPath() {
		if(!isFinalDest() && getDestination() instanceof DestinationParcel){
			setPath(((RemoteNode) getDestination()).getIdealPath());
		}
	}
	
	/**
	 * Place the parcel in the outbox. The mailbox will de
	 * @throws UnknownResponsibilityException 
	 */
	public void placeInOutbox() throws UnknownResponsibilityException{
		if(!isInOutbox()){
			if(!getOriginator().equals(this)){
				throw new UnknownResponsibilityException("The self node is not the originator of the parcel to send.");
			}

			if(getDestination().equals(this)){
				throw new UnknownResponsibilityException("The destination of a parcel to send cannot be the self node.");
			}

			mailbox.placeInOutbox(this);
		}
	}
	
	public void setInOutbox(){
		in_outbox = true;
	}
	
	private String content;
	private String payload;
	private Node destination;
	private Node originator;
	private int time_received = 0;
	private Mailbox mailbox;
	private boolean in_outbox;
	private ParcelType parcel_type = ParcelType.UNKNOWN;
}
