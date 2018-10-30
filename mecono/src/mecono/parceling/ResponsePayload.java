package mecono.parceling;

import mecono.node.ParcelHistoryStats;
import mecono.node.Path;
import mecono.node.SelfNode;
import mecono.protocol.BadProtocolException;

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
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof ResponsePayload) {
			ResponsePayload other = (ResponsePayload) o;
			return this.getRespondedID().equals(other.getRespondedID());
		}

		return false;
	}
	
	public String getRespondedID() {
		return respond_to_id;
	}
	
	@Override
	public void onReceiveAction() throws BadProtocolException, MissingParcelDetailsException {
		super.onReceiveAction();

		// Update the sent parcel
		Handshake responding_to = getHandshake();

		if (responding_to != null) {
			responding_to.responded(getParcel());

			// There are a few things we'd like to do upon a successful send with a good response
			// - Mark the path as successful
			// - Update the response value in the parcel history archive
			if (responding_to.hasResponse()) {
				Parcel original_parcel = responding_to.getTriggerParcel();
				Path path_used = original_parcel.getPath();
				path_used.success();
				getParcel().getMailbox().getOwner().nodeLog(SelfNode.ErrorStatus.GOOD, SelfNode.LogLevel.VERBOSE, "Response received successfully.");
			}
		} else {
			throw new MissingParcelDetailsException("Unwarranted response (or original parcel timeout)");
		}
	}

	public Handshake getHandshake() {
		return getParcel().getMailbox().getHandshakeHistory().lookup(getParcel());
	}
	
	private String respond_to_id;
}
