package mecono.parceling;

import java.util.ArrayList;
import mecono.node.CryptoManager;
import mecono.node.Mailbox;
import mecono.node.Node;
import mecono.node.NodeChain;
import mecono.node.Path;
import mecono.node.RemoteNode;
import mecono.node.SelfNode;
import mecono.protocol.BadProtocolException;
import mecono.protocol.Protocol;
import mecono.protocol.UnknownResponsibilityException;
import mecono.ui.UtilGUI;
import org.json.*;

public class Parcel implements MeconoSerializable {

	public Parcel(Mailbox mailbox) {
		this.mailbox = mailbox;
		this.nonce = getMailbox().getParcelNonce();
		generateUniqueID();
		setOriginator(mailbox.getOwner());
	}

	// Gets the originator of a parcel
	public Node getOriginator() throws MissingParcelDetailsException {
		if(originator != null){
			return originator;
		}
		
		throw new MissingParcelDetailsException("Originator not set");
	}
	
	public final void setOriginator(Node originator) {
		this.originator = originator;
	}
	
	// "Duplicate" means originator, destination, and payload is the same
	public boolean isDuplicate(Parcel parcel) throws MissingParcelDetailsException {
		if(getOriginator().equals(parcel.getOriginator())
				&& getDestination().equals(parcel.getDestination())
				&& getPayload().equals(parcel.getPayload())){
			return true;
		}
		
		return false;
	}

	public void setPath(Path path) throws MissingParcelDetailsException {
		if(getTransferDirection() == TransferDirection.INBOUND){
			this.path = path;
		}
	}
	
	public Handshake getHandshake() {
		return getMailbox().getHandshakeHistory().lookup(this);
	}

	public static boolean validUniqueID(String unique_id) {
		return unique_id.length() == 6;
	}

	public Path getPath() throws MissingParcelDetailsException {
		if (getTransferDirection() == TransferDirection.OUTBOUND && !isSent()) {
			if (getDestination() == null) {
				throw new MissingParcelDetailsException("No path set and missing destination");
			}

			Path ideal_path = ((RemoteNode) destination).getIdealPath();
			this.path = ideal_path;
		}

		return path;
	}

	public Node getNextNode() throws MissingParcelDetailsException {
		for(int i = 0; i < (getPath().getNodeChain().getPathLength() - 1); i++){
			if(getPath().getNodeChain().getStop(i).equals(getMailbox().getOwner())){
				return getPath().getNodeChain().getStop(i + 1);
			}
		}
		
		return null;
	}

	@Override
	public JSONObject serialize(){
		JSONObject parcel_json = new JSONObject();
		
		try {
			parcel_json.put("chain", getPath().getNodeChain().serialize());
			parcel_json.put("nonce", getNonce());
			parcel_json.put("payload", getPayload().serialize());
			parcel_json.put("signature", getSignature());
			
			return parcel_json;
		}catch(MissingParcelDetailsException ex){
			getMailbox().getOwner().nodeLog(SelfNode.ErrorStatus.FAIL, SelfNode.LogLevel.COMMON, "Could not serialize parcel", ex.getMessage());
		}
		
		return null;
	}

	public static int getParcelTypeCode(PayloadType target) {
		for (int i = 0; i < Protocol.parcel_type_codes.length; i++) {
			if (Protocol.parcel_type_codes[i] == target) {
				return i;
			}
		}
		return -1;
	}

	public static PayloadType parseParcelType(String parcel_type) {
		switch (parcel_type) {
			case "PING":
				return PayloadType.PING;
			case "PING_RESPONSE":
				return PayloadType.PING_RESPONSE;
			case "FIND":
				return PayloadType.FIND;
			case "FIND_RESPONSE":
				return PayloadType.FIND_RESPONSE;
			case "DATA":
				return PayloadType.DATA;
			case "DATA_RECEIPT":
				return PayloadType.DATA_RESPONSE;
			case "ANNC":
				return PayloadType.ANNC;
			default:
				return PayloadType.UNKNOWN;
		}
	}

	public boolean validSend() {
		try {
			if (getTransferDirection() != TransferDirection.OUTBOUND) {
				return false;
			}
			if (getDestination() == null) {
				return false;
			}
			if (getDestination().equals(mailbox.getOwner())) {
				return false;
			}
			if (!getOriginator().equals(mailbox.getOwner())) {
				return false;
			}
		} catch (MissingParcelDetailsException ex) {
			return false;
		}

		return true;
	}

	public final void setTimeCreated() {
		setTimeCreated(Protocol.getEpochMilliSecond());
	}

	public void setTimeSent() {
		this.time_sent = Protocol.getEpochMilliSecond();
	}

	public final void setTimeCreated(long time_created) {
		this.time_created = time_created;
	}

	public void setTimeReceived() {
		this.time_received = Protocol.getEpochMilliSecond();
	}

	public final long getTimeCreated() {
		return time_created;
	}
	
	public void onReceiveMetaAction() throws BadProtocolException, MissingParcelDetailsException {
		if (getTransferDirection() != Parcel.TransferDirection.INBOUND) {
			throw new BadProtocolException("The parcel isn't inbound");
		}
		
		try {
			// Learn the path
			getMailbox().getOwner().learnPath(getPath(), (RemoteNode) getOriginator());
		} catch (BadPathException ex) {
			getMailbox().getOwner().nodeLog(SelfNode.ErrorStatus.FAIL, SelfNode.LogLevel.COMMON, "Cannot learn path from received parcel", ex.getMessage());
		}
		
		// Mark path as successfully assisting this parcel
		statPathSuccess();
		
		getPayload().onReceiveAction();
	}
	
	private void statPathSuccess() throws MissingParcelDetailsException{
		// Update path statistics
		
		
		// Give each stop an "assist"
		for(Node stop : getPath().getStops()){
			if(stop instanceof RemoteNode){
				((RemoteNode) stop).markAssist();
			}
		}
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Parcel) {
			Parcel other = (Parcel) o;
			try {
				if (this.getDestination().equals(other.getDestination())
						&& this.getPayloadType() == other.getPayloadType()
						&& this.getUniqueID().equals(other.getUniqueID())) {
					return true;
				}
			}catch(MissingParcelDetailsException ex){
				return false;
			}
		}

		return false;
	}
	
	@Override
	public String toString() {
		String str = "";

		str = getPayloadType() + " Parcel ";

		try {
			str += "[ID: " + getUniqueID() + "]";
			str += "[Direction: " + getTransferDirection().name() + "]";
			if (getTransferDirection() == TransferDirection.OUTBOUND) {
				str += "[Destination: " + getDestination().getAddress() + "]";

				Path path = getOutboundActualPath();

				if (path == null) {
					str += "[Unknown Path]";
				} else {
					str += path.getNodeChain().toString();

					if (getPayload().getRequireOnlinePath() && !path.online()) {
						str += "[Path Offline]";
					}
				}
			} else {
				str += "[Origin: " + getOriginator().getAddress() + "]";
			}

		} catch (MissingParcelDetailsException ex) {
			str += "[Insufficient Details: " + ex.getMessage() + "]";
		}

		return str;
	}

	public final Mailbox getMailbox() {
		return mailbox;
	}

	@Override
	public Parcel deserialize() {
		return null;
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

	/**
	 * Checks if this parcel has all the send prerequisites met.
	 *
	 * @return
	 * @throws mecono.parceling.MissingParcelDetailsException
	 * @throws mecono.protocol.BadProtocolException
	 * @throws mecono.parceling.BadPathException
	 */
	public boolean readyToSend() throws MissingParcelDetailsException, BadProtocolException, BadPathException {
		getActualPath();

		if (getTransferDirection() != TransferDirection.OUTBOUND) {
			throw new BadProtocolException("Cannot send when transfer direction is not outbound");
		}

		if (getActualPath() == null || getOutboundActualPath() == null || (getPayload().getRequireOnlinePath() && !getOutboundActualPath().online())) {
			return false;
		}

		return true;
	}

	public boolean pathOnline() {
		return getOutboundActualPath() != null
				&& (!getPayload().getRequireOnlinePath() || getOutboundActualPath().online());
	}

	public long getTimeReceived() {
		return time_received;
	}

	public long getTimeSent() {
		return time_sent;
	}

	/**
	 * How long to wait for a response.
	 *
	 * @return
	 */
	public long getResponseWaitExpiry() {
		return 600 * 1000l;
	}

	public static NodeChain unserializeActualPath(JSONArray actual_path_json, SelfNode relative_self) throws BadProtocolException {
		ArrayList<Node> stops = new ArrayList<>();
		for (int i = 0; i < actual_path_json.length(); i++) {
			if (!Node.isValidAddress(actual_path_json.getString(i))) {
				throw new BadProtocolException("Invalid address");
			}

			stops.add(relative_self.getMemoryController().loadRemoteNode(actual_path_json.getString(i)));
		}
		return new NodeChain(stops);
	}
	
	public void setPayload(Payload payload){
		this.payload = payload;
		getPayload().setParcel(this);
	}
	
	public Payload getPayload(){
		return payload;
	}

	public static Parcel unserialize(JSONObject parcel_json, SelfNode relative_self) throws MissingParcelDetailsException, BadProtocolException {
		Parcel parcel;
		JSONObject content_json;
		JSONObject payload_json;

		if (parcel_json.has("payload")) {
			payload_json = parcel_json.getJSONObject("payload");
		} else {
			throw new MissingParcelDetailsException("Missing payload");
		}

		if (parcel_json.getJSONObject("payload").has("content")) {
			content_json = parcel_json.getJSONObject("payload").getJSONObject("content");
		} else {
			throw new MissingParcelDetailsException("Missing content");
		}

		switch (parseParcelType(payload_json.getString("parcel_type"))) {
			/*case PING:
				parcel = new PingParcel(relative_self.getMailbox());
				break;
			case PING_RESPONSE:
				parcel = new PingResponseParcel(relative_self.getMailbox());
				break;
			case FIND:
				parcel = new FindParcel(relative_self.getMailbox());

				if (content_json.has("target")) {
					((FindParcel) parcel).setTarget(relative_self.getMemoryController().loadRemoteNode(content_json.getString("target")));
				} else {
					throw new MissingParcelDetailsException("Missing target");
				}

				break;
			case FIND_RESPONSE:
				parcel = new FindResponseParcel(relative_self.getMailbox());
				if (content_json.has("target_answers")) {
					((FindResponseParcel) parcel).setTargetAnswers(content_json.getJSONArray("target_answers"));
				}

				break;
			case DATA:
				parcel = new DataParcel(relative_self.getMailbox());

				if (content_json.has("message")) {
					((DataParcel) parcel).setMessage(content_json.getString("message"));
				} else {
					throw new MissingParcelDetailsException("Invalid data parcel content");
				}

				break;
			case DATA_RESPONSE:
				parcel = new DataReceiptParcel(relative_self.getMailbox());

				break;
			case ANNC:
				parcel = new AnnounceParcel(relative_self.getMailbox());
				break;*/
			default:
				parcel = new Parcel(relative_self.getMailbox());
		}

		if (payload_json.has("unique_id") && Parcel.validUniqueID(payload_json.getString("unique_id"))) {
			parcel.setUniqueID(payload_json.getString("unique_id"));
		} else {
			throw new MissingParcelDetailsException("Missing unique ID");
		}

		/*if (parcel instanceof ResponseParcel) {
			if (payload_json.has("responding_to") && Parcel.validUniqueID(payload_json.getString("responding_to"))) {
				((ResponseParcel) parcel).setRespondedID(payload_json.getString("responding_to"));
			} else {
				throw new MissingParcelDetailsException("Response parcel missing valid responding-to field");
			}
		}*/

		parcel.setActualPath(Parcel.unserializeActualPath(payload_json.getJSONArray("actual_path"), relative_self));

		return parcel;
	}

	public Node getDestination() throws MissingParcelDetailsException {
		if(destination != null){
			return destination;
		}
		
		throw new MissingParcelDetailsException("Missing destination");
	}

	public String getOutboundActualPathString() {
		Path outbound_path = getOutboundActualPath();
		if (outbound_path != null) {
			return outbound_path.getNodeChain().toString();
		}

		return "unknown";
	}

	public String getPathOnlineString() {
		Path outbound_path = getOutboundActualPath();
		if (outbound_path != null) {
			return UtilGUI.getBooleanString(outbound_path.online());
		}

		return "unknown";
	}

	public void setDestination(Node destination) {
		this.destination = destination;
	}
	
	public boolean isFinalDest() {
		return destination.equals(mailbox.getOwner());
	}

	public boolean originatorIsSelf() throws MissingParcelDetailsException {
		return getOriginator() != null && getOriginator().equals(mailbox.getOwner());
	}
	
	public PayloadType getPayloadType() {
		// TODO: This is a violation of abstraction
		// Calling code should be consulting the payload, not the parcel
		return getPayload().getPayloadType();
	}

	public String getParcelTypeString() {
		return getPayloadType().name();
	}

	public Handshake getUponResponseAction() {
		return new Handshake(this);
	}

	public static PayloadType unserializePalletType(String representation) throws BadProtocolException {
		switch (representation) {
			case "p":
				return PayloadType.PING;
			case "pr":
				return PayloadType.PING_RESPONSE;
			case "d":
				return PayloadType.DATA;
			case "dr":
				return PayloadType.DATA_RESPONSE;
			case "f":
				return PayloadType.FIND;
			case "fr":
				return PayloadType.FIND_RESPONSE;
			default:
				throw new BadProtocolException("Unknown pallet type.");
		}
	}

	public boolean isInOutbox() {
		// TODO: A better function that actually checks if this parcel is in the mailbox's outbox, versus just checking if there's a variable that says it is.
		return in_outbox;
	}

	public final TransferDirection getTransferDirection() throws MissingParcelDetailsException {
		if(getOriginator().equals(getMailbox().getOwner())){
			return TransferDirection.OUTBOUND;
		}
		
		if(getDestination().equals(getMailbox().getOwner())){
			return TransferDirection.INBOUND;
		}
		
		return TransferDirection.FORWARD;
	}

	public void setInOutbox() {
		in_outbox = true;
	}

	public String getUniqueID() {
		return unique_id;
	}

	public void setUniqueID(String unique_id) {
		this.unique_id = unique_id;
	}
	
	public long getNonce(){
		return nonce;
	}
	
	public String getSignature() throws MissingParcelDetailsException {
		if(getTransferDirection() == TransferDirection.OUTBOUND){
			CryptoManager cm = getMailbox().getOwner().getCryptoManager();
			StringBuilder message = new StringBuilder();
			message.append(getNonce());
			message.append(",");
			message.append(path.serialize());
			message.append(",");
			message.append(getPayload().getEncryptedPayload());
			signature = cm.sign(message.toString());
		}
		
		return signature;
	}

	public JSONObject getSerializedContent() {
		JSONObject json_content = new JSONObject();
		json_content.put("data", "empty");
		return json_content;
	}

	public ForeignParcel constructForeignParcel() throws UnknownResponsibilityException, BadProtocolException, MissingParcelDetailsException {
		// We only want to construct foreign parcels if we are the originator
		if (originatorIsSelf()) {
			// Only construct the foreign parcel if the path is completely built.
			if (pathKnown()) {
				ForeignParcel outbound_foreign_parcel = new ForeignParcel(mailbox, getActualPath(), encryptAsPayload().toString());
				// The path history will contain current node + next node
				NodeChain new_path_history = getActualPath().getSubpath(0);
				outbound_foreign_parcel.setNextNode((RemoteNode) getActualPath().getStop(1));
				outbound_foreign_parcel.setPath(new_path_history);
				return outbound_foreign_parcel;
			} else {
				throw new BadProtocolException("Cannot construct a foreign parcel without a path.");
			}
		} else {
			throw new UnknownResponsibilityException("May only construct foreign nodes when the self node is the originator.");
		}
	}

	public void setIsSent() {
		if (!isSent()) {
			this.is_sent = true;
		}
	}

	public boolean isSent() {
		return is_sent;
	}

	public NodeChain getActualPath() throws MissingParcelDetailsException {
		if (getTransferDirection() == TransferDirection.OUTBOUND && !isSent()) {
			if (destination == null) {
				throw new MissingParcelDetailsException("No path set and missing destination");
			}

			Path ideal_path = ((RemoteNode) destination).getIdealPath();
			if (ideal_path != null) {
				outbound_actual_path = ideal_path;
				actual_path = outbound_actual_path.getPath();
			}
		}

		return actual_path;
	}

	public boolean pathKnown() throws MissingParcelDetailsException {
		return getActualPath() != null;
	}

	protected JSONObject encryptAsPayload() throws MissingParcelDetailsException {
		JSONObject plaintext_payload = new JSONObject();
		JSONArray actual_path = new JSONArray();
		ArrayList<Node> stops = getActualPath().getStops();

		for (Node stop : stops) {
			actual_path.put(stop.getAddress());
		}

		plaintext_payload.put("actual_path", actual_path);
		plaintext_payload.put("parcel_type", getPayloadType());
		plaintext_payload.put("unique_id", getUniqueID());
		plaintext_payload.put("content", getSerializedContent());
		plaintext_payload.put("signature", "parcel signature here");

		// TODO: Payload encryption operation.
		return plaintext_payload;
	}

	public long getResendCooldown() {
		return 30000;
	}

	public long getStaleTime() {
		return 60000;
	}
	
	public enum TransferDirection {
		OUTBOUND, INBOUND, FORWARD
	};

	private Node destination;
	private Node originator;
	private long time_received = 0;
	private boolean in_outbox;
	private String unique_id;
	private String signature;
	private boolean is_sent = false;
	private long time_created;
	private long time_sent;
	private Payload payload;
	private final Mailbox mailbox;
	private Path path;
	private final long nonce;
}
