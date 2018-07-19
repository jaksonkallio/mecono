package mecono.parceling.types;

import mecono.node.Mailbox;
import mecono.parceling.DestinationParcel;
import mecono.parceling.MissingParcelDetailsException;
import mecono.parceling.ParcelType;
import org.json.JSONObject;

/**
 *
 * @author jak
 */
public class DataParcel extends DestinationParcel {

	public DataParcel(Mailbox mailbox, TransferDirection direction) {
		super(mailbox, direction);
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
