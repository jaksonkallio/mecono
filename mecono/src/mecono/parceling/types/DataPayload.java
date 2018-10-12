package mecono.parceling.types;

import java.util.ArrayList;
import mecono.node.Mailbox;
import mecono.node.Path;
import mecono.node.RemoteNode;
import mecono.parceling.Parcel;
import mecono.parceling.MissingParcelDetailsException;
import mecono.parceling.PayloadType;
import mecono.parceling.Payload;
import mecono.protocol.BadProtocolException;
import org.json.JSONObject;

/**
 *
 * @author jak
 */
public class DataPayload extends Payload {
	
	@Override
	public PayloadType getParcelType() {
		return PayloadType.DATA;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof DataPayload) {
			DataPayload other = (DataPayload) o;
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
		
		Parcel parcel = new Parcel(getParcel().getMailbox());
		DataResponsePayload payload = new DataResponsePayload();
		parcel.setPayload(payload);

		payload.setRespondedID(getParcel().getUniqueID());
		parcel.setDestination((RemoteNode) getParcel().getOriginator()); // Set the destination to the person that contacted us (a response)
		
		getParcel().getMailbox().getHandshakeHistory().enqueueSend(parcel); // Send the response
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
