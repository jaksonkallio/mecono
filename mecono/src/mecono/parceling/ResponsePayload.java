package mecono.parceling;

/**
 *
 * @author jak
 */
public class ResponsePayload extends Payload {
	
	public void setRespondedID(String respond_to_id) {
		if (Parcel.validUniqueID(respond_to_id)) {
			this.respond_to_id = respond_to_id;
		}
	}
	
	public String getRespondedID() {
		return respond_to_id;
	}

	public Handshake getHandshake() {
		return getParcel().getMailbox().getHandshakeHistory().lookup(getParcel());
	}
	
	private String respond_to_id;
}
