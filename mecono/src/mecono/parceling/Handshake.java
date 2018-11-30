package mecono.parceling;

import mecono.node.Mailbox;
import mecono.node.NodeChain;
import mecono.protocol.Protocol;

/**
 * @description An action potential waiting for a response from a node after
 * initially sending out a signal.
 * @author jak
 */
public class Handshake {

	public Handshake(Parcel original_parcel) {
		this.trigger_parcel = original_parcel;
		this.time_created = Protocol.getEpochMilliSecond();
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

	public Parcel getResponseParcel() {
		return response_parcel;
	}

	public Parcel getTriggerParcel() {
		return trigger_parcel;
	}

	public long getPing() throws MissingParcelDetailsException {
		if (getResponseParcel() == null) {
			throw new MissingParcelDetailsException("Cannot get ping, response wasn't received yet.");
		}

		return Math.max(0, (getTimeResponded() - getTimeSent()));
	}

	public PayloadType getResponseType() {
		if (responded) {
			return response_parcel.getPayloadType();
		} else {
			return determineResponseType();
		}
	}

	public boolean hasResponse() {
		return getResponseParcel() != null;
	}

	public boolean isSent() {
		return retries > 0;
	}

	private PayloadType determineResponseType() {
		switch (trigger_parcel.getPayloadType()) {
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

	public boolean stale() {
		return (Protocol.elapsedMillis(getTimeSent()) > trigger_parcel.getPayload().getStaleTime());
	}
	
	public void responded(Parcel response_parcel){
		System.out.println("responded");
		if (getResponseType() == response_parcel.getPayloadType() && trigger_parcel.getUniqueID().equals(((ResponsePayload) response_parcel.getPayload()).getRespondedID())) {
			this.response_parcel = response_parcel;
			time_responded = Protocol.getEpochMilliSecond();
			responded = true;
		}
	}
	
	// Sets the handshake as sent
	public void sent(){
		time_sent = Protocol.getEpochMilliSecond();
		retries++;
	}
	
	public long getTimeSent(){
		return time_sent;
	}
	
	public int getRetryCount(){
		return retries;
	}
	
	public long getTimeResponded(){
		return time_responded;
	}

	private final Parcel trigger_parcel;
	private Parcel response_parcel;
	private boolean responded = false;
	private final long time_created; // Time the handshake was created and put in the pending parcel list
	private long time_sent; // Time the parcel was sent onto the Mecono network
	private long time_responded; // Time the handshake was given a response
	private int retries;
}
