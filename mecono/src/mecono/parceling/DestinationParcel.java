package mecono.parceling;

import java.util.ArrayList;
import mecono.protocol.BadProtocolException;
import mecono.node.Mailbox;
import mecono.node.Node;
import mecono.node.OutwardPath;
import mecono.node.Path;
import mecono.node.PathStats;
import mecono.protocol.Protocol;
import mecono.node.RemoteNode;
import mecono.node.SelfNode;
import static mecono.parceling.Parcel.parseParcelType;
import mecono.parceling.types.DataParcel;
import mecono.parceling.types.DataReceiptParcel;
import mecono.parceling.types.FindParcel;
import mecono.parceling.types.FindResponseParcel;
import mecono.parceling.types.PingParcel;
import mecono.parceling.types.PingResponseParcel;
import mecono.protocol.UnknownResponsibilityException;
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
     *
     * @param mailbox
     * @param direction
     */
    //pathhistory,[destination,parceltype,originator,content,signature(destination+originator+content)]
    public DestinationParcel(Mailbox mailbox, TransferDirection direction) {
        super(mailbox);
        generateUniqueID();
        this.direction = direction;
    }

    @Override
    public boolean equals(Object o) {
        DestinationParcel other = (DestinationParcel) o;

        return this.getDestination().equals(other.getDestination());
    }

	@Override
	public Path getPathHistory() throws MissingParcelDetailsException {
		if(getTransferDirection() == TransferDirection.OUTBOUND){
			return new Path(new ArrayList<Node>());
		}
		
		return super.getPathHistory();
	}
	
    @Override
    public String toString() {
		String str = "";
		
		str = getParcelType() + " Parcel ";
		
		try {
			str += "[ID: " + getUniqueID() + "]";
			str += "[Direction: " + getTransferDirection().name() + "]";
			if(getTransferDirection() == TransferDirection.OUTBOUND){
				str += "[Destination: " + getDestination().getAddress() + "]";
				
				OutwardPath outward_path = (OutwardPath) getActualPath();
				
				if(outward_path == null){
					str += "[Unknown Path]";
				}else{
					str += outward_path.toString();
				}
				
				if(readyToSend()){
					str += "[Ready]";
				}else{
					str += "[Not Ready]";
				}
			}else{
				str += "[Origin: " + getOriginator().getAddress() + "]";
			}
			
			
		} catch(MissingParcelDetailsException ex){
			str += "[Insufficient Details: " + ex.getMessage() + "]";
		} catch(BadProtocolException ex){
			if(getTransferDirection() == TransferDirection.OUTBOUND){
				str += "[Illegal to Send: " + ex.getMessage() + "]";
			}else{
				str += "[Illegal: " + ex.getMessage() + "]";
			}
		}
		
		
		
		return str;
	}

    public Mailbox getMailbox() {
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

    public boolean consultIfPathNotKnown() {
        return true;
    }

    /**
     * Checks if this parcel has all the send prerequisites met.
     *
     * @return
	 * @throws mecono.parceling.MissingParcelDetailsException
	 * @throws mecono.protocol.BadProtocolException
     */
    public boolean readyToSend() throws MissingParcelDetailsException, BadProtocolException {
		Path outward_path = getActualPath();
		
		if(getTransferDirection() != TransferDirection.OUTBOUND){
			throw new BadProtocolException("Cannot send when transfer direction is not outbound");
		}
		
		if(!(outward_path instanceof OutwardPath)){
			return false;
		}

		if((!isActualPathKnown() || !((OutwardPath) outward_path).isTested()) && !(this instanceof FindParcel)){
			return false;
		}

		return true;
		//return (isActualPathKnown() && ((OutwardPath) outward_path).isTested()) || mailbox.getOwner().isNeighbor((RemoteNode) getDestination());
    }

    public int getTimeReceived() {
        return time_received;
    }
	
	public static Path unserializeActualPath(JSONArray actual_path_json, SelfNode relative_self){
		ArrayList<Node> stops = new ArrayList<>();
		for(int i = 0; i < actual_path_json.length(); i++){
			stops.add(relative_self.getMemoryController().loadRemoteNode(actual_path_json.getString(i)));
		}
		return new Path(stops);
	}

	public static DestinationParcel unserialize(JSONObject parcel_json, SelfNode relative_self) throws MissingParcelDetailsException {
		DestinationParcel parcel;
		JSONObject content_json;
		JSONObject payload_json;
		
		if(parcel_json.has("payload")){
			payload_json = parcel_json.getJSONObject("payload");
		}else{
			throw new MissingParcelDetailsException("Missing payload");
		}
		
		if(parcel_json.getJSONObject("payload").has("content")){
			content_json = parcel_json.getJSONObject("payload").getJSONObject("content");
		}else{
			throw new MissingParcelDetailsException("Missing content");
		}
		
		switch (parseParcelType(payload_json.getString("parcel_type"))) {
			case PING:
				parcel = new PingParcel(relative_self.getMailbox(), DestinationParcel.TransferDirection.INBOUND);
				break;
			case PING_RESPONSE:
				parcel = new PingResponseParcel(relative_self.getMailbox(), DestinationParcel.TransferDirection.INBOUND);
				break;
			case FIND:
				parcel = new FindParcel(relative_self.getMailbox(), DestinationParcel.TransferDirection.INBOUND);
				
				if(content_json.has("target")){
					((FindParcel) parcel).setTarget(relative_self.getMemoryController().loadRemoteNode(content_json.getString("target")));
				}else{
					throw new MissingParcelDetailsException("Missing target");
				}
				
				break;
			case FIND_RESPONSE:
				parcel = new FindResponseParcel(relative_self.getMailbox(), DestinationParcel.TransferDirection.INBOUND);
				break;
			case DATA:
				parcel = new DataParcel(relative_self.getMailbox(), DestinationParcel.TransferDirection.INBOUND);

				if (content_json.has("message")) {
					((DataParcel) parcel).setMessage(content_json.getString("message"));
				} else {
					throw new MissingParcelDetailsException("Invalid data parcel content");
				}

				break;
			case DATA_RECEIPT:
				parcel = new DataReceiptParcel(relative_self.getMailbox(), DestinationParcel.TransferDirection.INBOUND);
				break;
			default:
				parcel = new DestinationParcel(relative_self.getMailbox(), DestinationParcel.TransferDirection.INBOUND);
		}
		
		parcel.setActualPath(DestinationParcel.unserializeActualPath(payload_json.getJSONArray("actual_path"), relative_self));
		
		return parcel;
	}
	
    public int getAge() {
        return Math.max(Protocol.getEpochMinute() - time_received, 0);
    }

    public Node getDestination() {
        //try {
        return destination;
        //} catch (MissingParcelDetailsException ex) {
        //    mailbox.getOwner().nodeLog(2, "Could not get destination: " + ex.getMessage());
        //}

        //return null;
    }

    public void setDestination(RemoteNode destination) {
		if(!isInOutbox() && getTransferDirection() == TransferDirection.OUTBOUND){
			this.destination = destination;
		}
    }

    public boolean consultWhenPathUnknown() {
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

    public UponResponseAction getUponResponseAction() {
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

    /**
     * Place the parcel in the outbox. A destination parcel can only be in the
     * outbox if (1) the originator is the self node and (2) the destination is
     * not the self node.
     *
     * @throws UnknownResponsibilityException
     * @throws mecono.parceling.MissingParcelDetailsException
     */
    public void placeInOutbox() throws UnknownResponsibilityException, MissingParcelDetailsException {
        if (!isInOutbox()) {
            if (!originatorIsSelf()) {
                throw new UnknownResponsibilityException("The self node is not the originator of the parcel to send.");
            }

            if (getDestination().equals(mailbox.getOwner())) {
                throw new UnknownResponsibilityException("The destination of a parcel to send cannot be the self node.");
            }

            if (getDestination().equals(getOriginator())) {
                throw new UnknownResponsibilityException("The destination cannot be the parcel's originator.");
            }

            mailbox.placeInOutbox(this);
        }
    }

	public final TransferDirection getTransferDirection(){
		return direction;
	}
	
    public void setInOutbox() {
        in_outbox = true;
    }

    @Override
    public RemoteNode getNextNode() throws MissingParcelDetailsException {
        if(getTransferDirection() == TransferDirection.OUTBOUND){
			return (RemoteNode) getActualPath().getStop(1);
		}
		
		return null;
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
	
	public void setUniqueID(String unique_id) {
        this.unique_id = unique_id;
    }

    public String getSignature() {
        return signature;
    }

    @Override
    public JSONObject serialize() {
        JSONObject serialized = new JSONObject();

        try {
            serialized.put("path_history", getPathHistory());
            serialized.put("destination", getDestination().getAddress());
            serialized.put("parcel_type", Parcel.getParcelTypeCode(parcel_type));
            serialized.put("unique_id", getUniqueID());
            serialized.put("actual_path", getActualPath());
            serialized.put("content", getSerializedContent());
            serialized.put("signature", getSignature());
        } catch (MissingParcelDetailsException ex) {
            mailbox.getOwner().nodeLog(2, "Could not serialized parcel: " + ex.getMessage());
        }

        return serialized;
    }

    public JSONObject getSerializedContent() {
        JSONObject json_content = new JSONObject();
        json_content.put("data", "empty");
        return json_content;
    }

    public ForeignParcel constructForeignParcel() throws UnknownResponsibilityException, BadProtocolException, MissingParcelDetailsException {
		//mailbox.getOwner().nodeLog(0, "In constructForeignParcel");
		
        // We only want to construct foreign parcels if we are the originator
        if (originatorIsSelf()) {
            // Only construct the foreign parcel if the path is completely built.
            if (isActualPathKnown()) {
				ForeignParcel outbound_foreign_parcel = new ForeignParcel(mailbox, getActualPath(), encryptAsPayload());
				Path new_path_history = new Path();
				// The path history will contain current node + next node
				new_path_history = getActualPath().getSubpath(1);
				outbound_foreign_parcel.setPathHistory(new_path_history);
                return outbound_foreign_parcel;
            } else {
                throw new BadProtocolException("Cannot construct a foreign parcel without a path.");
            }
        } else {
            throw new UnknownResponsibilityException("May only construct foreign nodes when the self node is the originator.");
        }
    }

	public void setActualPath(Path actual_path) {
		if(getTransferDirection() == TransferDirection.INBOUND){
			this.actual_path = actual_path;
		}
	}
	
	public Path getActualPath() throws MissingParcelDetailsException {
		if(direction == TransferDirection.OUTBOUND){
			if(destination == null){
				throw new MissingParcelDetailsException("No path set and missing destination");
			}
			
			/*if(isInOutbox()){
				throw new MissingParcelDetailsException("Cannot generate path after being placed in outbox");
			}*/
			PathStats ideal_path = ((RemoteNode) destination).getIdealPath();
			if(ideal_path != null){
				actual_path = ideal_path.getPath();
			}
		}
		
		return actual_path;
	}
	
	public boolean isActualPathKnown() throws MissingParcelDetailsException {
		return getActualPath() != null;
	}
	
    @Override
    public Node getOriginator() throws MissingParcelDetailsException {
        if (getTransferDirection() == TransferDirection.OUTBOUND) {
            return (Node) mailbox.getOwner();
        } else {
            return getActualPath().getStop(0);
        }
    }

    /**
     * Encrypt this destination parcel as a payload.
     *
     * @return
     */
    private String encryptAsPayload() throws MissingParcelDetailsException {
        JSONObject plaintext_payload = new JSONObject();
        JSONArray actual_path = new JSONArray();
        ArrayList<Node> stops = getActualPath().getStops();

        for (Node stop : stops) {
            actual_path.put(stop.getAddress());
        }

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

    private boolean in_outbox;
    private String unique_id;
    private String signature;
    private Path actual_path;
    private ParcelType parcel_type = ParcelType.UNKNOWN;
    private final TransferDirection direction;

    public enum TransferDirection {
        OUTBOUND, INBOUND
    };
}
