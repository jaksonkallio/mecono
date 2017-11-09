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
}
