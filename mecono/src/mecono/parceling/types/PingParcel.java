package mecono.parceling.types;

import mecono.node.Mailbox;
import mecono.parceling.DestinationParcel;
import mecono.parceling.ParcelType;

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
	public boolean requiresTestedPath() {
		return false;
	}
	
	/**
	 * Ping parcels, unlike normal destination parcels, don't require a tested path before being sent.
	 * @return 
	 */
	@Override
	public boolean readyToSend(){
		return pathKnown();
	}
}
