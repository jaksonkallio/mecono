package mecono;

/**
 * @description An action potential waiting for a response from a node after
 * initially sending out a signal.
 * @author jak
 */
public class UponResponseAction {

	public UponResponseAction(Mailbox mailbox, DestinationParcel original_parcel) {
		this.mailbox = mailbox;
		this.original_parcel = original_parcel;
		this.response_type = determineResponseType();
		this.original_time_sent = Protocol.getEpochSecond();
	}

	@Override
	public boolean equals(Object o) {
		UponResponseAction other = (UponResponseAction) o;
		return (this.getOriginalParcel().equals(other.getOriginalParcel()));
	}

	public void giveResponse(DestinationParcel response_parcel) {
		if (response_type == response_parcel.getParcelType()) {
			// The response pallet is indeed the response to the original sent pallet
			// TODO: Verify parcel ID is the same
			this.response_parcel = response_parcel;
			responded = true;
		}
	}

	public void runAction() {
		if (responded) {
			switch (getResponseType()) {
				case PING_RESPONSE:
					actionFromPing();
					break;
				case FIND_RESPONSE:
					actionFromFind();
					break;
				case DATA_RECEIPT:
					actionFromData();
					break;
				default:
					break;
			}
		}
	}

	public DestinationParcel getOriginalParcel() {
		return original_parcel;
	}

	public ParcelType getResponseType() {
		if (responded) {
			return response_parcel.getParcelType();
		} else {
			return response_type;
		}
	}

	/**
	 * What do do when a ping is responded to.
	 */
	private void actionFromPing() {
		// TODO: Verify that the destination signed the original pallet
		//original_parcel.getDestination().updateSuccessfulPing((int) (Protocol.getEpochSecond() - response_parcel.getTimeSent()));
	}

	private void actionFromFind() {
		// TODO: What to do after a find is responded to
	}

	private void actionFromData() {
		// TODO: What to do after data was successfully received remotely
	}

	private ParcelType determineResponseType() {
		switch (original_parcel.getParcelType()) {
			case PING:
				return ParcelType.PING_RESPONSE;
			case FIND:
				return ParcelType.FIND_RESPONSE;
			case DATA:
				return ParcelType.DATA_RECEIPT;
			default:
				return ParcelType.UNKNOWN;
		}
	}

	private final Mailbox mailbox;
	private final DestinationParcel original_parcel;
	private DestinationParcel response_parcel;
	private boolean responded = false;
	private final long original_time_sent;
	private final ParcelType response_type;
}
