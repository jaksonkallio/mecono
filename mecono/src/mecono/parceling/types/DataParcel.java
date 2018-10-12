package mecono.parceling.types;

import java.util.ArrayList;
import mecono.node.Mailbox;
import mecono.node.Path;
import mecono.node.RemoteNode;
import mecono.parceling.Parcel;
import mecono.parceling.MissingParcelDetailsException;
import mecono.parceling.ParcelType;
import mecono.parceling.Payload;
import mecono.protocol.BadProtocolException;
import org.json.JSONObject;

/**
 *
 * @author jak
 */
public class DataParcel extends Payload {
	
	@Override
	public ParcelType getParcelType() {
		return ParcelType.DATA;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof DataParcel) {
			DataParcel other = (DataParcel) o;
			try {
				return (other.getMessage().equals(this.getMessage()) && other.getParcel().getDestination().equals(this.getParcel().getDestination()) && other.getParcel().getOriginator().equals(this.getParcel().getOriginator()));
			} catch (MissingParcelDetailsException ex) {
				return false;
			}
		}

		return false;
	}

	@Override
	public void onReceiveAction() throws BadProtocolException, MissingParcelDetailsException {
		super.onReceiveAction();

		getParcel().getMailbox().getOwner().messageReceived(this.getMessage());

		RemoteNode received_originator = (RemoteNode) getParcel().getOriginator();
		DataReceiptParcel response = new DataReceiptParcel(getParcel().getMailbox());
		response.setRespondedID(getParcel().getUniqueID());
		response.setDestination(received_originator); // Set the destination to the person that contacted us (a response)
		getParcel().getMailbox().getHandshakeHistory().enqueueSend(response); // Send the response
	}

	public void setMessage(String message) {
		if (!getParcel().isInOutbox()) {
			this.message = message;
		}
	}

	public String getMessage() {
		return message;
	}

	@Override
	public JSONObject serialize() {
		JSONObject json_content = new JSONObject();
		json_content = json_content.put("message", message);
		return json_content;
	}

	public static final long RESEND_COOLDOWN = 10000;

	private String message = "";
}
