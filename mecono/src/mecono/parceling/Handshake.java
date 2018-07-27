package mecono.parceling;

import mecono.node.Mailbox;
import mecono.node.Path;
import mecono.protocol.Protocol;

/**
 * @description An action potential waiting for a response from a node after
 * initially sending out a signal.
 * @author jak
 */
public class Handshake {

	public Handshake(DestinationParcel original_parcel) {
		this.original_parcel = original_parcel;
		this.original_time_sent = Protocol.getEpochMilliSecond();
	}

	@Override
	public String toString() {
		return "Awaiting " + getResponseType() + " after sending " + getOriginalParcel().getUniqueID();
	}

	@Override
	public boolean equals(Object o) {
		Handshake other = (Handshake) o;
		return (this.getOriginalParcel().equals(other.getOriginalParcel()));
	}

	public void giveResponse(ResponseParcel response_parcel) {
		if (getResponseType() == response_parcel.getParcelType() && original_parcel.getUniqueID().equals(response_parcel.getRespondedID())) {
			this.response_parcel = response_parcel;
			responded = true;
			response_parcel.setTimeReceived();
		}
	}

	public ResponseParcel getResponseParcel() {
		return response_parcel;
	}

	/**
	 * There are only a couple cases where action is required upon response.
	 * Ping to determine the latency, and data to tell if a chunk has been sent
	 * and doesn't need re-broadcast.
	 */
	public void runAction() {
		if (responded) {
			switch (getResponseType()) {
				case PING_RESPONSE:
					actionFromPing();
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

	public long getPing() throws MissingParcelDetailsException {
		if (getResponseParcel() == null) {
			throw new MissingParcelDetailsException("Cannot get ping, response wasn't received yet.");
		}

		return Math.max(0, (getResponseParcel().getTimeCreated() - original_time_sent));
	}

	public ParcelType getResponseType() {
		if (responded) {
			return response_parcel.getParcelType();
		} else {
			return determineResponseType();
		}
	}

	/**
	 * What do do when a ping is responded to.
	 */
	private void actionFromPing() {
		// TODO: Verify that the destination signed the original pallet
		//original_parcel.getDestination().updateSuccessfulPing((int) (Protocol.getEpochSecond() - response_parcel.getTimeSent()));
	}

	private void actionFromData() {
		// TODO: What to do after data was successfully received remotely
	}
        
	public boolean hasResponse(){
		return getResponseParcel() != null;
	}
	
	public boolean isSent(){
		return is_sent;
	}
	
	public void updateLastSendAttempt(){
		last_send_attempt = Protocol.getEpochMilliSecond();
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
	
	public boolean isStale(){
		return (Protocol.elapsedMillis(original_time_sent) > original_parcel.STALE_TIME);
	}
	
	public boolean readyResend(){
		return (Protocol.elapsedMillis(last_send_attempt) > original_parcel.RESEND_COOLDOWN);
	}

	private final DestinationParcel original_parcel;
	private ResponseParcel response_parcel;
	private boolean responded = false;
	private boolean is_sent = false;
	private long last_send_attempt = 0;
	private final long original_time_sent;
}
