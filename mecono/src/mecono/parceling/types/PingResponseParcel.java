package mecono.parceling.types;

import mecono.node.Mailbox;
import mecono.node.Path;
import mecono.parceling.MissingParcelDetailsException;
import mecono.parceling.ResponseParcel;
import mecono.parceling.SentParcel;
import mecono.protocol.BadProtocolException;

/**
 *
 * @author jak
 */
public class PingResponseParcel extends ResponseParcel {

	public PingResponseParcel(Mailbox mailbox, TransferDirection direction) {
		super(mailbox, direction);
	}
	@Override
	public void onReceiveAction() throws BadProtocolException, MissingParcelDetailsException {
		SentParcel sent_parcel = mailbox.getSentParcel(getRespondedID());
		sent_parcel.giveResponse(this);
		long ping = sent_parcel.getPing();

		// Update the ping on the path
		PingParcel original_parcel = (PingParcel) sent_parcel.getOriginalParcel();
		Path used_path = original_parcel.getUsedPath();
	}
}
