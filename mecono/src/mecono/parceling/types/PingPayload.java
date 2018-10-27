package mecono.parceling.types;

import mecono.node.RemoteNode;
import mecono.node.SelfNode;
import mecono.parceling.Parcel;
import mecono.parceling.MissingParcelDetailsException;
import mecono.parceling.PayloadType;
import mecono.parceling.Payload;
import mecono.protocol.BadProtocolException;

/**
 *
 * @author jak
 */
public class PingPayload extends Payload {

	@Override
	public PayloadType getPayloadType() {
		return PayloadType.PING;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof PingPayload) {
			// All PingPayloads are identical
			return true;
		}

		return false;
	}

	@Override
	public boolean requiresOnlinePath() {
		return false;
	}

	@Override
	public void onReceiveAction() throws BadProtocolException, MissingParcelDetailsException {
		super.onReceiveAction();

		Parcel parcel = new Parcel(getParcel().getMailbox());
		PingResponsePayload response_payload = new PingResponsePayload();
		parcel.setPayload(response_payload);
		response_payload.setRespondedID(getParcel().getUniqueID());
		parcel.setDestination((RemoteNode) getParcel().getOriginator()); // Set the destination to the person that contacted us (a response)
		getParcel().getMailbox().getHandshakeHistory().enqueueSend(parcel); // Send the response
		getParcel().getMailbox().getOwner().nodeLog(SelfNode.ErrorStatus.GOOD, SelfNode.LogLevel.VERBOSE, "Responding with parcel", parcel.toString());
	}
}
