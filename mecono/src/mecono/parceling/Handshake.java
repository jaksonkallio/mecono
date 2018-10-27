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

	public Handshake(Parcel original_parcel) {
		this.original_parcel = original_parcel;
		this.original_time_sent = Protocol.getEpochMilliSecond();
	}

	@Override
	public String toString() {
		return "Awaiting " + getResponseType() + " after sending " + getTriggerParcel().getUniqueID();
	}

	@Override
	public boolean equals(Object o) {
		Handshake other = (Handshake) o;
		return (this.getTriggerParcel().equals(other.getTriggerParcel()));
	}

	public void giveResponse(Parcel response_parcel) {
		if (getResponseType() == response_parcel.getPayloadType() && original_parcel.getUniqueID().equals(((ResponsePayload) response_parcel.getPayload()).getRespondedID())) {
			this.response_parcel = response_parcel;
			responded = true;
			response_parcel.setTimeReceived();
		}
	}

	public Parcel getResponseParcel() {
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
				case DATA_RESPONSE:
					actionFromData();
					break;
				default:
					break;
			}
		}
	}

	public Parcel getTriggerParcel() {
		return original_parcel;
	}

	public long getPing() throws MissingParcelDetailsException {
		if (getResponseParcel() == null) {
			throw new MissingParcelDetailsException("Cannot get ping, response wasn't received yet.");
		}

		return Math.max(0, (getResponseParcel().getTimeCreated() - original_time_sent));
	}

	public PayloadType getResponseType() {
		if (responded) {
			return response_parcel.getPayloadType();
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

	public boolean hasResponse() {
		return getResponseParcel() != null;
	}

	public boolean isSent() {
		return is_sent;
	}

	public void updateLastSendAttempt() {
		last_send_attempt = Protocol.getEpochMilliSecond();
	}

	private PayloadType determineResponseType() {
		switch (original_parcel.getPayloadType()) {
			case PING:
				return PayloadType.PING_RESPONSE;
			case FIND:
				return PayloadType.FIND_RESPONSE;
			case DATA:
				return PayloadType.DATA_RESPONSE;
			default:
				return PayloadType.UNKNOWN;
		}
	}

	public boolean isStale() {
		return (Protocol.elapsedMillis(original_time_sent) > original_parcel.getStaleTime());
	}

	public boolean readyResend() {
		return (Protocol.elapsedMillis(last_send_attempt) > original_parcel.getResendCooldown());
	}

	private final Parcel original_parcel;
	private Parcel response_parcel;
	private boolean responded = false;
	private boolean is_sent = false;
	private long last_send_attempt = 0;
	private final long original_time_sent;
}
