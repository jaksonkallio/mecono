package mecono.parceling.types;

import mecono.node.Mailbox;
import mecono.node.RemoteNode;
import mecono.node.SelfNode;
import mecono.parceling.Parcel;
import mecono.parceling.MissingParcelDetailsException;
import mecono.parceling.ParcelType;
import mecono.protocol.BadProtocolException;

/**
 *
 * @author jak
 */
public class PingParcel extends Parcel {

	public PingParcel(Mailbox mailbox) {
		super(mailbox);
	}

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

		PingResponseParcel response = new PingResponseParcel(getMailbox());
		response.setRespondedID(getUniqueID());
		response.setDestination((RemoteNode) getOriginator()); // Set the destination to the person that contacted us (a response)
		getMailbox().getHandshakeHistory().enqueueSend(response); // Send the response
		getMailbox().getOwner().nodeLog(SelfNode.ErrorStatus.GOOD, SelfNode.LogLevel.VERBOSE, "Responding with parcel", response.toString());
	}

	/**
	 * Ping parcels, unlike normal destination parcels, don't require a tested
	 * path before being sent.
	 *
	 * @return
	 * @throws mecono.parceling.MissingParcelDetailsException
	 */
	@Override
	public boolean readyToSend() throws MissingParcelDetailsException {
		return pathKnown();
	}

	@Override
	public long getResendCooldown() {
		return 2000;
	}

	@Override
	public boolean getRequireOnlinePath() {
		return false;
	}
}
