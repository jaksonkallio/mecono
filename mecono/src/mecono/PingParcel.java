package mecono;

/**
 *
 * @author jak
 */
public class PingParcel extends DestinationParcel {

	public PingParcel(Mailbox mailbox) {
		super(mailbox);
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
