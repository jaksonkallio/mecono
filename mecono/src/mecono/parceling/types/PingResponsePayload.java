package mecono.parceling.types;

import mecono.node.Path;
import mecono.parceling.MissingParcelDetailsException;
import mecono.parceling.PayloadType;
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

		Handshake handshake = getParcel().getHandshake();

		if (handshake.hasResponse()) {
			long ping = handshake.getPing();
			Parcel original_parcel = handshake.getTriggerParcel();
			Path used_path = original_parcel.getPath();

			used_path.setPing(ping);
		}
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof PingResponsePayload) {
			// All DataResponsePayloads are identical
			return super.equals(o);
		}

		return false;
	}

	@Override
	public PayloadType getPayloadType() {
		return PayloadType.PING_RESPONSE;
	}

	@Override
	public boolean getRequireOnlinePath() {
		return false;
	}
}
