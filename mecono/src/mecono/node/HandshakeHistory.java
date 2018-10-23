package mecono.node;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import mecono.parceling.Parcel;
import mecono.parceling.MissingParcelDetailsException;
import mecono.parceling.Handshake;
import mecono.parceling.PayloadType;
import mecono.parceling.types.FindPayload;
import mecono.parceling.types.PingPayload;
import mecono.protocol.BadProtocolException;
import mecono.protocol.Protocol;
import mecono.protocol.UnknownResponsibilityException;

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
			pending.add(handshake);
		}
	}

	public void enqueueSend(Parcel parcel) {
		if (parcel.validSend()) {
			Handshake handshake = new Handshake(parcel);
			enqueueSend(handshake);
		}
	}

	public int count(boolean has_response, PayloadType parcel_type) {
		return count(has_response, false, parcel_type);
	}

	public int count(boolean has_response, boolean pending_list, PayloadType parcel_type) {
		int count = 0;
		List<Handshake> status = completed;

		if (pending_list) {
			status = pending;
		}

		for (Handshake handshake : status) {
			if (handshake.hasResponse() == has_response && (parcel_type == null || handshake.getTriggerParcel().getParcelType() == parcel_type)) {
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
		if (send_cursor < pending.size()) {
			Handshake handshake = pending.get(send_cursor);

			// First check consists of readiness based on outbound information
			// Must be unsent, must not be stale, and must be ready to resend
			if (!handshake.isSent()
					&& !handshake.isStale()
					&& handshake.readyResend()) {
				Parcel original_parcel = handshake.getTriggerParcel();

				try {
					// Second check consists of readiness based on the actual parcel metadata
					if (original_parcel.pathKnown()) {
						if (original_parcel.pathOnline()) {
							// Send
							try {
								mailbox.enqueueForward(original_parcel.constructForeignParcel());
								original_parcel.setUsedPath();
								original_parcel.setIsSent();
								original_parcel.setTimeSent();
								pending.remove(send_cursor);
								completed.add(handshake);
								send_cursor = 0;
							} catch (UnknownResponsibilityException | MissingParcelDetailsException | BadProtocolException ex) {
								mailbox.getOwner().nodeLog(SelfNode.ErrorStatus.FAIL, SelfNode.LogLevel.COMMON, "Could not send parcel through network controller:", ex.getMessage());
							}
						} else {
							// Ping the destination
							pingPath(original_parcel.getActualPath());
						}
					} else {
						// Consult for a path to the destination
						consultPath(((RemoteNode) original_parcel.getDestination()));
					}
				} catch (MissingParcelDetailsException ex) {

				}
			}
		}

		// Increment the cursor
		if (pending.size() > 0) {
			send_cursor = (send_cursor + 1) % pending.size();
		}
	}
	
	// Find a handshake using a response parcel
	public Handshake lookup(Parcel response) {
		for (Handshake handshake : completed) {
			if (handshake.getTriggerParcel().getUniqueID().equals(response.getUniqueID())) {
				return handshake;
			}
		}

		return null;
	}

	public List getPendingParcels() {
		return pending;
	}

	public String listPending() {
		String construct = "No parcels in outbox.";

		if (pending.size() > 0) {
			construct = "";

			for (Handshake handshake : pending) {
				construct += "\n-- " + handshake.getTriggerParcel().toString();
			}
		}

		return construct;
	}

	public int getPendingCount() {
		return pending.size();
	}

	private void pingPath(Path path) {
		Parcel ping = new Parcel(mailbox);
		PingPayload ping_payload = new PingPayload();
		ping.setPayload(ping_payload);
		ping.setDestination((RemoteNode) path.getLastStop());
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
		for (Handshake handshake : pending) {
			if (handshake.getTriggerParcel().equals(parcel)) {
				return true;
			}
		}

		return false;
	}

	private int send_cursor = 0;
	private final List<Handshake> pending = new ArrayList<>();
	private final List<Handshake> completed = new LinkedList<>();
	private final Mailbox mailbox;
}
