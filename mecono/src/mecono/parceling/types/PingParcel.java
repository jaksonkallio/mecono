package mecono.parceling.types;

import mecono.node.Mailbox;
import mecono.node.RemoteNode;
import mecono.node.SelfNode;
import mecono.parceling.Parcel;
import mecono.parceling.MissingParcelDetailsException;
import mecono.parceling.ParcelType;
import mecono.parceling.Payload;
import mecono.protocol.BadProtocolException;

/**
 *
 * @author jak
 */
public class PingParcel extends Payload {

	@Override
	public ParcelType getParcelType() {
		return ParcelType.PING;
	}

	@Override
	public boolean requiresOnlinePath() {
		return false;
	}

	@Override
	public void onReceiveAction() throws BadProtocolException, MissingParcelDetailsException {
		super.onReceiveAction();

		PingResponseParcel response = new PingResponseParcel(getParcel().getMailbox());
		response.setRespondedID(getParcel().getUniqueID());
		response.setDestination((RemoteNode) getParcel().getOriginator()); // Set the destination to the person that contacted us (a response)
		getParcel().getMailbox().getHandshakeHistory().enqueueSend(response); // Send the response
		getParcel().getMailbox().getOwner().nodeLog(SelfNode.ErrorStatus.GOOD, SelfNode.LogLevel.VERBOSE, "Responding with parcel", response.toString());
	}
}
