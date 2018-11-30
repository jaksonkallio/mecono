package mecono.node;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import mecono.parceling.Handshake;
import mecono.parceling.MissingParcelDetailsException;
import mecono.parceling.Parcel;
import mecono.parceling.PayloadType;
import mecono.parceling.types.FindPayload;
import mecono.parceling.types.PingPayload;
import mecono.protocol.Protocol;

/**
 * The Sent Parcel History is a complete archive of recently sent, or queued to
 * be sent, parcels into the Mecono network.
 *
 * @author Jakson
 */
public class HandshakeHistory {

	public HandshakeHistory(Mailbox mailbox) {
		this.mailbox = mailbox;
	}

	public void prune() {

	}

	public void enqueueSend(Handshake handshake) {
		if (!alreadyPending(handshake.getTriggerParcel())) {
			mailbox.getOwner().nodeLog(SelfNode.ErrorStatus.INFO, SelfNode.LogLevel.COMMON, "Enqueued parcel for send", handshake.getTriggerParcel().toString());
			history.add(handshake);
		}
	}

	public void enqueueSend(Parcel parcel) {
		Handshake handshake = new Handshake(parcel);
		enqueueSend(handshake);
	}

	public int count(boolean has_response, PayloadType parcel_type) {
		int count = 0;
		List<Handshake> status = history;
		
		for (Handshake handshake : status) {
			if (handshake.hasResponse() == has_response && (parcel_type == null || handshake.getTriggerParcel().getPayload().getPayloadType() == parcel_type)) {
				count++;
			}
		}
		
		return count;
	}

	public double successRate(PayloadType parcel_type) {
		int count_success = count(true, parcel_type);
		int count_fail = count(false, parcel_type);

		return (count_success / Math.max(1, (count_fail + count_success)));
	}

	public void attemptSend() {
		if (send_cursor < history.size()) {
			Handshake handshake = history.get(send_cursor);

			// First check consists of readiness based on outbound information
			if (handshake.stale() || !handshake.isSent()) {
				Parcel original_parcel = handshake.getTriggerParcel();
				
				if(handshake.getRetryCount() < original_parcel.getPayload().getMaxRetryCount() || original_parcel.getPayload().getRetryIndefinitely()){
					try {
						if (original_parcel.pathKnown()) {
							if (original_parcel.getPath().online() || !original_parcel.getPayload().getRequireOnlinePath()) {
								mailbox.enqueueOutbound(original_parcel);
								handshake.sent();
								send_cursor = 0;
							} else {
								// Ping the destination
								pingPath(original_parcel.getPath());
							}
						} else {
							if(original_parcel.getPayload().getResolveUnknownPath()){
								// Consult for a path to the destination
								consultPath(((RemoteNode) original_parcel.getDestination()));
							}
						}
					} catch (MissingParcelDetailsException ex) {

					}
				}else{
					history.remove(send_cursor);
					getMailbox().getOwner().nodeLog(SelfNode.ErrorStatus.FAIL, SelfNode.LogLevel.COMMON, "Parcel "+original_parcel.getUniqueID()+" failed to send after several retries.");
				}
			}
		}

		// Increment the cursor
		if (history.size() > 0) {
			send_cursor = (send_cursor + 1) % history.size();
		}
	}
	
	// Find a handshake using a response parcel
	public Handshake lookup(Parcel response) {
		for (Handshake handshake : history) {
			if (handshake.isSent() && handshake.getTriggerParcel().getUniqueID().equals(response.getUniqueID())) {
				return handshake;
			}
		}

		return null;
	}

	public Mailbox getMailbox(){
		return mailbox;
	}
	
	public List getPendingParcels() {
		List<Handshake> pending = new ArrayList<>();
		
		for (Handshake handshake : history) {
			if(!handshake.hasResponse()){
				pending.add(handshake);
			}
		}
		
		return pending;
	}

	public String listPending() {
		String construct = "No parcels in outbox.";

		if (history.size() > 0) {
			construct = "";

			for (Handshake handshake : history) {
				if(!handshake.hasResponse()){
					construct += "\n-- " + handshake.getTriggerParcel().toString();
				}
			}
		}

		return construct;
	}

	public int getPendingCount() {
		return getPendingParcels().size();
	}

	private void pingPath(Path path) {
		Parcel ping = new Parcel(mailbox);
		PingPayload ping_payload = new PingPayload();
		ping.setPayload(ping_payload);
		ping.setDestination((RemoteNode) path.getNodeChain().getLastStop());
		enqueueSend(ping);
	}

	private void consultPath(RemoteNode target) {
		SelfNode self = mailbox.getOwner();

		// A neighbor has an implicitly defined path, so it can never be the target of a search.
		if (!self.isNeighbor(target)) {
			// Now consult the nodes
			for (RemoteNode consultant : self.getPinnedNodes()) {
				if (!consultant.equals(target) && Protocol.elapsedMillis(consultant.getTimeLastConsulted()) > mailbox.getOwner().CONSULTATION_COOLDOWN) {
					// Only consult a node if the consultant is NOT the node we're looking for.
					Parcel find = new Parcel(mailbox);
					FindPayload find_payload = new FindPayload();
					find.setPayload(find_payload);
					find_payload.setTarget(target);
					find.setDestination(consultant);
					enqueueSend(find);
					consultant.updateTimeLastConsulted();
				}
			}
		}
	}

	private boolean alreadyPending(Parcel parcel) {
		for (Handshake handshake : history) {
			try {
				if (!handshake.hasResponse() && handshake.getTriggerParcel().isDuplicate(parcel)) {
					return true;
				}
			}catch(MissingParcelDetailsException ex){
				
			}
		}
		
		return false;
	}

	private int send_cursor = 0;
	private final List<Handshake> history = new ArrayList<>();
	private final Mailbox mailbox;
}
