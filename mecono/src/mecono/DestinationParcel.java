package mecono;

import org.json.JSONArray;
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
	 * @param mailbox
	 * @param direction
	 */
	//pathhistory,[destination,parceltype,originator,content,signature(destination+originator+content)]
	public DestinationParcel(Mailbox mailbox, TransferDirection direction) {
		generateUniqueID();
		this.mailbox = mailbox;
		this.direction = direction;
	}
	
	@Override
	public boolean equals(Object o){
		DestinationParcel other = (DestinationParcel) o;
		
		return this.getDestination().equals(other.getDestination());
	}
	
	@Override
	public String toString(){
		return getParcelType()+" Parcel -  ID: "+getUniqueID();
	}
	
	public Mailbox getMailbox(){
		return mailbox;
	}

	private void generateUniqueID() {
		char[] text = new char[Protocol.parcel_unique_id_length];

		for (int i = 0; i < Protocol.parcel_unique_id_length; i++) {
			text[i] = Protocol.hex_chars[Protocol.rng.nextInt(Protocol.hex_chars.length)];
		}

		this.unique_id = new String(text);
	}

	public void updateReceivedTime() {
		time_received = Protocol.getEpochMinute();
	}
	
	public boolean consultIfPathNotKnown(){
		return true;
	}
	
	/**
	 * Checks if this parcel has all the send prerequisites met.
	 * @return 
	 */
	public boolean readyToSend() {
		try{
			return (pathKnown() && getPath().isTested()) || mailbox.getOwner().isNeighbor((RemoteNode) getDestination());
		}catch(MissingParcelDetailsException ex){
			return false;
		}
	}
	
	public boolean pathKnown() {
		try{
			return getPath() != null;
		}catch(MissingParcelDetailsException ex){
			return false;
		}
	}

	public int getTimeReceived() {
		return time_received;
	}

	public int age() {
		return Math.max(Protocol.getEpochMinute() - time_received, 0);
	}
	
	public Node getDestination() {
		try{
			Path dest_path = getPath();
			return dest_path.getStop(dest_path.getPathLength() - 1);
		}catch(MissingParcelDetailsException ex){
			mailbox.getOwner().nodeLog(2, "Could not get destination: "+ex.getMessage());
		}
		
		return null;
	}

	public void setDestination(RemoteNode destination) throws MissingParcelDetailsException {
		if (!isInOutbox()) {
			if (getPath() == null) {
				this.destination = destination;
			}
		}
	}
	
	public boolean consultWhenPathUnknown(){
		return true;
	}

	public boolean isFinalDest() {
		return destination.equals(mailbox.getOwner());
	}

	public boolean originatorIsSelf() throws MissingParcelDetailsException {
            return getOriginator() != null && getOriginator().equals(mailbox.getOwner());
	}

	public ParcelType getParcelType() {
		return ParcelType.UNKNOWN;
	}

	public UponResponseAction getUponResponseAction(){
		return new UponResponseAction(mailbox, this);
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
		if (!isFinalDest()) {
			setPath(((RemoteNode) getDestination()).getIdealPath());
		}
	}

	public boolean hasCompletePath() {
		return path != null;
	}

	/**
	 * Place the parcel in the outbox. A destination parcel can only be in the outbox if (1) the originator is the self node and (2) the destination is not the self node.
	 *
	 * @throws UnknownResponsibilityException
	 */
	public void placeInOutbox() throws UnknownResponsibilityException, MissingParcelDetailsException {
		if (!isInOutbox()) {
			if (!originatorIsSelf()) {
				throw new UnknownResponsibilityException("The self node is not the originator of the parcel to send.");
			}

			if (getDestination().equals(mailbox.getOwner())) {
				throw new UnknownResponsibilityException("The destination of a parcel to send cannot be the self node.");
			}
			
			if (getDestination().equals(originator)) {
				throw new UnknownResponsibilityException("The destination cannot be the parcel's originator.");
			}

			mailbox.placeInOutbox(this);
		}
	}

	public void setInOutbox() {
		in_outbox = true;
	}

	@Override
	public RemoteNode getNextNode() throws MissingParcelDetailsException {
		if (originatorIsSelf()) {
			// (Self Originator ->) neighbor -> node 2 -> node 3 -> ... -> destination
			return (RemoteNode) path.getStop(0);
		} else {
			// There is no next node, if the self node is not the first node in the path. 
			return null;
		}
	}

	/**
	 * Whether this kind of parcel can be sent without a valid/tested path.
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
	public Path getPath() throws MissingParcelDetailsException {
		if(fixed_path == null){
			if(destination != null){
				return ((RemoteNode) destination).getIdealPath();
			}else{
				throw new MissingParcelDetailsException("Attempting to get path when destination is not set.");
			}
		}
		
		return fixed_path;
	}

	@Override
	public JSONObject serialize() {
		JSONObject serialized = new JSONObject();

		try{
			serialized = serialized.put("path_history", getPath());
			serialized = serialized.put("destination", getDestination().getAddress());
			serialized = serialized.put("parcel_type", Parcel.getParcelTypeCode(parcel_type));
			serialized = serialized.put("unique_id", getUniqueID());
			serialized = serialized.put("official_path", getPath());
			serialized = serialized.put("content", getSerializedContent());
			serialized = serialized.put("signature", getSignature());
		}catch(MissingParcelDetailsException ex){
			mailbox.getOwner().nodeLog(2, "Could not serialized parcel: "+ex.getMessage());
		}
		
		return serialized;
	}

	public JSONObject getSerializedContent() {
		JSONObject json_content = new JSONObject();
		json_content = json_content.put("data", "empty");
		return json_content;
	}
	
	public ForeignParcel constructForeignParcel() throws UnknownResponsibilityException, BadProtocolException, MissingParcelDetailsException {
		// We only want to construct foreign parcels if we are the originator
		if(originatorIsSelf()){
			// Only construct the foreign parcel if the path is completely built.
			if(hasCompletePath()){
				return new ForeignParcel(getPath(), encryptAsPayload());
			}else{
				throw new BadProtocolException("Cannot construct a foreign parcel without a path.");
			}
		}else{
			throw new UnknownResponsibilityException("May only construct foreign nodes when the self node is the originator.");
		}
	}
	
	public void setFixedPath(Path fixed_path){
		if(fixed_path != null){
			this.fixed_path = fixed_path;
		}
	}
	
	public void setFixedPath() throws MissingParcelDetailsException {
		setFixedPath(getPath());
	}
	
	@Override
	public Node getOriginator() throws MissingParcelDetailsException {
		return getPath().getStop(0);
	}
	
	/**
	 * Encrypt this destination parcel as a payload.
	 * @return 
	 */
	private String encryptAsPayload() throws MissingParcelDetailsException{
		JSONObject plaintext_payload = new JSONObject();
		JSONArray actual_path = new JSONArray(getPath());

		plaintext_payload.put("actual_path", actual_path);
		plaintext_payload.put("parcel_type", getParcelType());
		plaintext_payload.put("unique_id", getUniqueID());
		plaintext_payload.put("content", getSerializedContent());
		plaintext_payload.put("signature", "parcel signature here");

		// TODO: Payload encryption operation.

		return plaintext_payload.toString();
	}
	
	private String payload;
	private Node destination;
	private int time_received = 0;
	private final Mailbox mailbox;
	private boolean in_outbox;
	private String unique_id;
	private String signature;
	private Path fixed_path;
	private ParcelType parcel_type = ParcelType.UNKNOWN;
	private final TransferDirection direction;

	public enum TransferDirection {
		OUTBOUND, INBOUND
	};
}
