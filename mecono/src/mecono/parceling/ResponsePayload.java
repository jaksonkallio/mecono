package mecono.parceling;

import mecono.node.ParcelHistoryStats;
import mecono.node.PathStats;
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
	
	public String getRespondedID() {
		return respond_to_id;
	}
	
	@Override
	public void onReceiveAction() throws BadProtocolException, MissingParcelDetailsException {
		super.onReceiveAction();

		// Update the sent parcel
		Handshake responding_to = getHandshake();

		if (responding_to != null) {
			responding_to.giveResponse(getParcel());

			// There are a few things we'd like to do upon a successful send with a good response
			// - Mark the path as successful
			// - Update the response value in the parcel history archive
			if (responding_to.hasResponse()) {
				Parcel original_parcel = responding_to.getTriggerParcel();
				PathStats path_used = original_parcel.getOutboundActualPath();
				path_used.success();
				//ParcelHistoryArchive parcel_history_archive = getParcel().getMailbox().getParcelHistoryArchive();
				//parcel_history_archive.markParcelResponded(original_parcel.getUniqueID(), getParcel());
				getParcel().getMailbox().getOwner().nodeLog(SelfNode.ErrorStatus.GOOD, SelfNode.LogLevel.VERBOSE, "Marked parcel history archive item as responded to.");
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
