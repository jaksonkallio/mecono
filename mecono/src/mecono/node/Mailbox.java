package mecono.node;

import mecono.protocol.BadProtocolException;
import mecono.parceling.ForeignParcel;
import mecono.parceling.MissingParcelDetailsException;
import mecono.parceling.Handshake;
import mecono.parceling.Parcel;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import mecono.parceling.BadPathException;
import mecono.parceling.types.PingPayload;
import mecono.protocol.Protocol;
import org.json.JSONObject;

/**
 * The mailbox is responsible for managing parcel sending/receiving, queuing
 * parcels, and giving received parcels to the self node.
 *
 * @author jak
 */
public class Mailbox {

	public Mailbox(SelfNode owner) {
		this.owner = owner;
		this.network_controller = new NetworkController(this);
		this.worker = new MailboxWorker(this);
	}

	public void receiveParcel(Parcel parcel) {
		if (parcel != null) {
			processParcel(parcel);
		}else{
			getOwner().nodeLog(SelfNode.ErrorStatus.FAIL, SelfNode.LogLevel.COMMON, "Mailbox received null parcel from network controller");
		}
	}

	public void processParcel(Parcel parcel) {
		getOwner().nodeLog(SelfNode.ErrorStatus.INFO, SelfNode.LogLevel.COMMON, "Processing received destination parcel", parcel.toString());

		try {
			// Do the required action as defined by the Parcel
			// Parcel will call onReceiveAction for the Payload, and the specific action is different based on the Payload type
			// onReceiveMetaAction hands off responsibility to the Parcel (transmission meta-data), then onReceiveAction is customized for the type of Payload (actual content)
			// The two are separated because the Mailbox shouldn't be responsible for the contents/meta of a Parcel
			parcel.onReceiveMetaAction();
		} catch (MissingParcelDetailsException | BadProtocolException ex) {
			getOwner().nodeLog(SelfNode.ErrorStatus.FAIL, SelfNode.LogLevel.COMMON, "Could not process received parcel", ex.getMessage());
		}
	}

	/**
	 * Gets the owner of the mailbox.
	 *
	 * @return
	 */
	public SelfNode getOwner() {
		return owner;
	}

	public MailboxWorker getWorker() {
		return worker;
	}

	public NetworkController getNetworkController() {
		return network_controller;
	}

	public void processForwardQueue() {
		if (forward_queue.size() > 0) {
			ForeignParcel parcel = forward_queue.poll();
			try {
				network_controller.sendParcel(parcel);
			} catch (MissingParcelDetailsException | BadProtocolException ex) {
				getOwner().nodeLog(2, "Could not hand off to network controller: " + ex.getMessage());
			}
		}
	}

	public void pingPinnedNode(int i) {
		if (pinned_nodes == null) {
			return;
		}

		RemoteNode pinned_node = pinned_nodes.get(i);
		if (Protocol.elapsedSeconds(pinned_node.getLastPinged()) >= getOwner().PINNED_NODE_PING_RATE) {
			//pingRemote(pinned_node);
		}
	}

	public int getPinnedNodeCount() {
		return pinned_nodes.size();
	}

	public ParcelHistoryStats getParcelHistoryArchive() {
		return parcel_history_archive;
	}

	public int getSentParcelCount() {
		return sent_parcels.size();
	}

	public HandshakeHistory getHandshakeHistory() {
		return handshake_history;
	}

	/**
	 * Checks if there is an active signal out in the network that we are
	 * expecting a response to. Used to protect against spamming the network.
	 */
	private boolean expectingResponse(Parcel parcel) {
		for (Handshake existing_action : sent_parcels) {
			Parcel original_parcel = existing_action.getTriggerParcel();

			if (original_parcel.equals(parcel) && !existing_action.hasResponse() && Protocol.elapsedMillis(original_parcel.getTimeSent()) < original_parcel.getResendCooldown()) {
				return true;
			}
		}

		return false;
	}

	public void enqueueInbound(JSONObject serialized_parcel) {
		if (serialized_parcel != null) {
			inbound_queue.offer(serialized_parcel);
		}
	}

	public void enqueueForward(ForeignParcel foreign) {
		forward_queue.offer(foreign);
	}

	public void pingPinnedNodes() {
		for (RemoteNode pinned : owner.getPinnedNodes()) {
			PathStats ideal_path = pinned.getIdealPath();

			if (ideal_path != null && !ideal_path.online()) {
				Parcel parcel = new Parcel(this);
				PingPayload payload = new PingPayload();
				parcel.setPayload(payload);
				parcel.setDestination(pinned);
				getHandshakeHistory().enqueueSend(parcel);
			}
		}
	}

	public void processInboundQueue() {
		if (inbound_queue.size() > 0) {
			try {
				receiveParcel(Parcel.unserialize(inbound_queue.poll(), getOwner()));
			} catch (MissingParcelDetailsException | BadProtocolException ex) {
				getOwner().nodeLog(2, "Could not receive parcel", ex.getMessage());
			}
		}
	}
	
	public final long getParcelNonce(){
		parcel_nonce_counter++;
		return parcel_nonce_counter;
	}

	private final SelfNode owner; // The selfnode that runs the mailbox
	private final MailboxWorker worker;
	private final ArrayList<Handshake> sent_parcels = new ArrayList<>();
	private final HandshakeHistory handshake_history = new HandshakeHistory(this);
	private final NetworkController network_controller;
	private final ArrayList<RemoteNode> pinned_nodes = new ArrayList<>();
	private final Queue<ForeignParcel> forward_queue = new LinkedBlockingQueue<>(); // The forward queue is made up of foreign parcels ready to be sent.
	private final Queue<JSONObject> inbound_queue = new LinkedBlockingQueue<>(); // The inbound queue is made up of received JSON objects that need to be processed
	private final ParcelHistoryStats parcel_history_archive = new ParcelHistoryStats();
	private long parcel_nonce_counter = 0;
}
