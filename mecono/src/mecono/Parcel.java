package mecono;

import java.util.Arrays;
import java.util.List;
import org.json.*;

/**
 *
 * @author jak
 */
public abstract class Parcel {

	/**
	 * Which node originated this parcel, supposedly.
	 *
	 * @return RemoteNode Originator node object.
	 */
	public Node getOriginator() {
		if(path != null){
			return (RemoteNode) path.getStop(0);
		}else{
			return originator;
		}
	}

	public void setPath(Path path) {
		this.path = path;
	}

	public Path getPath() {
		return path;
	}

	/**
	 * Gets the next node in the path.
	 *
	 * @return
	 */
	public RemoteNode getNextNode() {
		return null;
	}

	public JSONObject serialize() {
		return null;
	}

	public static Parcel unserialize(JSONObject json_parcel, SelfNode relative_self) throws BadProtocolException, UnknownResponsibilityException {
		Parcel received_parcel = null;
		Mailbox mailbox = relative_self.getMailbox();

		if (json_parcel.getString("destination").equals(relative_self.getAddress())) {
			switch (Protocol.parcel_type_codes[json_parcel.getInt("parcel_type")]) {
				case PING:
					received_parcel = new PingParcel(mailbox);
					break;
				case PING_RESPONSE:
					received_parcel = new PingResponseParcel(mailbox);
					break;
				case FIND:
					received_parcel = new FindParcel(mailbox);
					break;
				case FIND_RESPONSE:
					received_parcel = new FindResponseParcel(mailbox);
					break;
				case DATA:
					received_parcel = new DataParcel(mailbox);
					break;
				case DATA_RECEIPT:
					received_parcel = new DataReceiptParcel(mailbox);
					break;
				default:
					received_parcel = new DestinationParcel(mailbox);
			}
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
	
	protected Node originator;
	protected Path path_history;
	protected Path path;
}
