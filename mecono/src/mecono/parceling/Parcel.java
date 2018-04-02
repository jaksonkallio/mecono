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

	public void setPath(Path path) {
		this.path = path;
	}

	public Path getPath() throws MissingParcelDetailsException {
		return path;
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

	public static Parcel unserialize(JSONObject json_parcel, SelfNode relative_self) throws BadProtocolException, UnknownResponsibilityException {
		Parcel received_parcel = null;
		Mailbox mailbox = relative_self.getMailbox();
		mailbox.getOwner().nodeLog(0, "Unserializing received parcel: "+json_parcel.toString());
		
		JSONObject json_parcel_payload = new JSONObject(json_parcel.getString("payload"));
		if (json_parcel_payload.has("actual_path")) {
			JSONArray actual_path = json_parcel_payload.getJSONArray("actual_path");
			String destination_address = actual_path.getString(actual_path.length() - 1);
			
			if (destination_address != null && destination_address.equals(relative_self.getAddress())) {
				switch (parseParcelType(json_parcel_payload.getString("parcel_type"))) {
					case PING:
						received_parcel = new PingParcel(mailbox, DestinationParcel.TransferDirection.INBOUND);
						break;
					case PING_RESPONSE:
						received_parcel = new PingResponseParcel(mailbox, DestinationParcel.TransferDirection.INBOUND);
						break;
					case FIND:
						received_parcel = new FindParcel(mailbox, DestinationParcel.TransferDirection.INBOUND);
						break;
					case FIND_RESPONSE:
						received_parcel = new FindResponseParcel(mailbox, DestinationParcel.TransferDirection.INBOUND);
						break;
					case DATA:
						received_parcel = new DataParcel(mailbox, DestinationParcel.TransferDirection.INBOUND);
						
						if (json_parcel_payload.has("content") && json_parcel_payload.getJSONObject("content").has("message")) {
							((DataParcel) received_parcel).setMessage(json_parcel_payload.getJSONObject("content").getString("message"));
						} else {
							throw new BadProtocolException("Bad content format.");
						}

						break;
					case DATA_RECEIPT:
						received_parcel = new DataReceiptParcel(mailbox, DestinationParcel.TransferDirection.INBOUND);
						break;
					default:
						received_parcel = new DestinationParcel(mailbox, DestinationParcel.TransferDirection.INBOUND);
				}
			}else{
				throw new BadProtocolException("Node is not the final destination");
			}
		}else{
			throw new BadProtocolException("Missing destination parameter");
		}
		
		return received_parcel;
	}

	public static int getParcelTypeCode(ParcelType target) {
		for (int i = 0; i < Protocol.parcel_type_codes.length; i++) {
			if (Protocol.parcel_type_codes[i] == target) {
				return i;
			}
		}
		return -1;
	}
	
	public static ParcelType parseParcelType(String parcel_type){
		switch(parcel_type){
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
	protected Path path;
}
