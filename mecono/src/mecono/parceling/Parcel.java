package mecono.parceling;

import java.util.ArrayList;
import mecono.parceling.types.DataParcel;
import mecono.parceling.types.FindResponseParcel;
import mecono.parceling.types.DataReceiptParcel;
import mecono.parceling.types.FindParcel;
import mecono.parceling.types.PingResponseParcel;
import mecono.parceling.types.PingParcel;
import mecono.protocol.BadProtocolException;
import mecono.node.Mailbox;
import mecono.node.Node;
import mecono.node.Path;
import mecono.protocol.Protocol;
import mecono.node.RemoteNode;
import mecono.node.SelfNode;
import mecono.parceling.DestinationParcel.TransferDirection;
import mecono.protocol.UnknownResponsibilityException;
import org.json.*;

/**
 *
 * @author jak
 */
public abstract class Parcel {

	public Parcel(Mailbox mailbox) {
		this.mailbox = mailbox;
	}

	/**
	 * Which node originated this parcel, supposedly.
	 *
	 * @return RemoteNode Originator node object.
	 */
	public abstract Node getOriginator() throws MissingParcelDetailsException;

	public final void setPathHistory(Path path_history) {
		this.path_history = path_history;
	}

	public static boolean validUniqueID(String unique_id) {
		return unique_id.length() == 6;
	}

	public Path getPathHistory() throws MissingParcelDetailsException {
		if (path_history == null) {
			throw new MissingParcelDetailsException("No path history was supplied");
		}

		return path_history;
	}

	/**
	 * Gets the next node in the path.
	 *
	 * @return
	 */
	public RemoteNode getNextNode() throws MissingParcelDetailsException {
		return null;
	}

	public JSONObject serialize() {
		return null;
	}

	public static Parcel unserialize(JSONObject json_parcel, SelfNode relative_self) throws MissingParcelDetailsException {
		//mailbox.getOwner().nodeLog(0, "Unserializing received parcel: "+json_parcel.toString());

		if (json_parcel.has("payload")) {
			json_parcel.put("payload", new JSONObject(json_parcel.getString("payload")));
		} else {
			throw new MissingParcelDetailsException("Missing payload");
		}

		Path actual_path = null;
		
		if (json_parcel.getJSONObject("payload").has("actual_path")) {
			actual_path = DestinationParcel.unserializeActualPath(json_parcel.getJSONObject("payload").getJSONArray("actual_path"), relative_self);
		}
		
		if(actual_path != null && actual_path.getStop(actual_path.getPathLength() - 1).equals(relative_self)){
			// Destination parcel
			DestinationParcel base_parcel = new DestinationParcel(relative_self.getMailbox(), TransferDirection.INBOUND);
			return DestinationParcel.unserialize(json_parcel, relative_self);
		} else {
			// Foreign parcel
			if(json_parcel.has("path_history")){
				Path path_history = DestinationParcel.unserializeActualPath(json_parcel.getJSONArray("path_history"), relative_self);
				if(json_parcel.has("payload")){
					String payload_string = json_parcel.getString("payload");
					ForeignParcel parcel = new ForeignParcel(relative_self.getMailbox(), path_history, payload_string);
					
					return parcel;
				}else{
					throw new MissingParcelDetailsException("Missing encrypted payload");
				}
			}else{
				throw new MissingParcelDetailsException("Foreign parcel lacking path history");
			}
		}

		/*if (json_parcel_payload.has("actual_path")) {
			JSONArray actual_stops_json = json_parcel_payload.getJSONArray("actual_path");
			ArrayList<Node> stops = new ArrayList<>();
			for(int i = 0; i < actual_stops_json.length(); i++){
				stops.add(relative_self.getMemoryController().loadRemoteNode(actual_stops_json.getString(i)));
			}
			Path actual_path = new Path(stops);
			
			String destination_address = actual_path.getStop(actual_path.getPathLength() - 1).getAddress();
			
			if (destination_address != null && destination_address.equals(relative_self.getAddress())) {
				DestinationParcel.parse();
			}else{
				throw new BadProtocolException("Node is not the final destination");
			}
		}else{
			throw new BadProtocolException("Missing destination parameter");
		}
		
		return received_parcel;*/
	}

	public static int getParcelTypeCode(ParcelType target) {
		for (int i = 0; i < Protocol.parcel_type_codes.length; i++) {
			if (Protocol.parcel_type_codes[i] == target) {
				return i;
			}
		}
		return -1;
	}

	public static ParcelType parseParcelType(String parcel_type) {
		switch (parcel_type) {
			case "PING":
				return ParcelType.PING;
			case "PING_RESPONSE":
				return ParcelType.PING_RESPONSE;
			case "FIND":
				return ParcelType.FIND;
			case "FIND_RESPONSE":
				return ParcelType.FIND_RESPONSE;
			case "DATA":
				return ParcelType.DATA;
			case "DATA_RECEIPT":
				return ParcelType.DATA_RECEIPT;
			case "COMMUNITY":
				return ParcelType.COMMUNITY;
			case "COMMUNITY_RESPONSE":
				return ParcelType.COMMUNITY_RESPONSE;
			default:
				return ParcelType.UNKNOWN;
		}
	}

	protected final Mailbox mailbox;
	protected Node originator;
	protected Path path_history;
}
