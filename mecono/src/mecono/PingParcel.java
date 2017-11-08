package mecono;

/**
 *
 * @author jak
 */
public class PingParcel extends DestinationParcel {

	@Override
	public ParcelType getParcelType() {
		return ParcelType.PING;
	}

	@Override
	public boolean requiresTestedPath() {
		return false;
	}
}
