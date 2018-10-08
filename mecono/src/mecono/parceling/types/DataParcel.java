package mecono.parceling.types;

import java.util.ArrayList;
import mecono.node.Mailbox;
import mecono.node.Path;
import mecono.node.RemoteNode;
import mecono.parceling.Parcel;
import mecono.parceling.MissingParcelDetailsException;
import mecono.parceling.ParcelType;
import mecono.protocol.BadProtocolException;
import org.json.JSONObject;

/**
 *
 * @author jak
 */
public class DataParcel extends Parcel {

	public DataParcel(Mailbox mailbox, TransferDirection direction) {
		super(mailbox);
	}

	@Override
	public ParcelType getParcelType() {
		return ParcelType.DATA;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof DataParcel) {
			DataParcel other = (DataParcel) o;
			try {
				return (other.getMessage().equals(this.getMessage()) && other.getDestination().equals(this.getDestination()) && other.getOriginator().equals(this.getOriginator()));
			} catch (MissingParcelDetailsException ex) {
				return false;
			}
		}

		return false;
	}

	@Override
	public void onReceiveAction() throws BadProtocolException, MissingParcelDetailsException {
		super.onReceiveAction();

		mailbox.getOwner().messageReceived(this.getMessage());

		RemoteNode received_originator = (RemoteNode) getOriginator();
		DataReceiptParcel response = new DataReceiptParcel(mailbox, TransferDirection.OUTBOUND);
		response.setRespondedID(getUniqueID());
		response.setDestination(received_originator); // Set the destination to the person that contacted us (a response)
		getMailbox().getHandshakeHistory().enqueueSend(response); // Send the response
	}

	public void setMessage(String message) {
		if (!isInOutbox()) {
			this.message = message;
		}
	}

	public String getMessage() {
		return message;
	}

	@Override
	public JSONObject getSerializedContent() {
		JSONObject json_content = new JSONObject();
		json_content = json_content.put("message", message);
		return json_content;
	}

	public static final long RESEND_COOLDOWN = 10000;

	private String message = "";
}
