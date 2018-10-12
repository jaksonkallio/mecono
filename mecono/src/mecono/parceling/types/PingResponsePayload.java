package mecono.parceling.types;

import mecono.node.Mailbox;
import mecono.node.PathStats;
import mecono.parceling.MissingParcelDetailsException;
import mecono.parceling.PayloadType;
import mecono.parceling.ResponseParcel;
import mecono.parceling.Handshake;
import mecono.parceling.Parcel;
import mecono.parceling.ResponsePayload;
import mecono.protocol.BadProtocolException;

/**
 *
 * @author jak
 */
public class PingResponsePayload extends ResponsePayload {

	@Override
	public void onReceiveAction() throws BadProtocolException, MissingParcelDetailsException {
		super.onReceiveAction();

		Handshake sent_parcel = getParcel().getHandshake();

		if (sent_parcel.hasResponse()) {
			long ping = sent_parcel.getPing();
			Parcel original_parcel = sent_parcel.getTriggerParcel();
			PathStats used_path = original_parcel.getOutboundActualPath();

			used_path.setPing(ping);
		}
	}

	@Override
	public PayloadType getParcelType() {
		return PayloadType.PING_RESPONSE;
	}

	@Override
	public boolean requiresOnlinePath() {
		return false;
	}

	@Override
	public boolean getRequireOnlinePath() {
		return false;
	}
}