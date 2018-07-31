package mecono.parceling.types;

import java.util.ArrayList;
import mecono.node.Mailbox;
import mecono.node.Path;
import mecono.node.RemoteNode;
import mecono.parceling.DestinationParcel;
import mecono.parceling.MissingParcelDetailsException;
import mecono.parceling.ParcelType;
import mecono.protocol.BadProtocolException;

/**
 *
 * @author jak
 */
public class PingParcel extends DestinationParcel {

	public PingParcel(Mailbox mailbox, TransferDirection direction) {
		super(mailbox, direction);
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
	public void onReceiveAction() throws BadProtocolException, MissingParcelDetailsException{
		super.onReceiveAction();
		
		PingResponseParcel response = new PingResponseParcel(mailbox, TransferDirection.OUTBOUND);
		response.setRespondedID(getUniqueID());
		response.setDestination((RemoteNode) getOriginator()); // Set the destination to the person that contacted us (a response)
		getMailbox().getHandshakeHistory().enqueueSend(response); // Send the response
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
	public long getResendCooldown(){
		return 2000;
	}
	
	@Override
	public boolean getRequireOnlinePath(){
		return false;
	}
}
