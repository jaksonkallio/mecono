package mecono.node;

import mecono.protocol.BadProtocolException;
import mecono.protocol.UnknownResponsibilityException;
import mecono.parceling.Parcel;
import mecono.parceling.ForeignParcel;
import mecono.parceling.MissingParcelDetailsException;
import mecono.parceling.SentParcel;
import mecono.parceling.types.FindParcel;
import mecono.parceling.DestinationParcel;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import mecono.node.SelfNode.ErrorStatus;
import mecono.node.SelfNode.LogLevel;
import mecono.parceling.BadPathException;
import mecono.parceling.DestinationParcel.TransferDirection;
import mecono.parceling.ParcelType;
import mecono.parceling.types.PingParcel;
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
		if (parcel instanceof DestinationParcel) {
			processDestinationParcel((DestinationParcel) parcel);
			getOwner().nodeLog(SelfNode.ErrorStatus.INFO, SelfNode.LogLevel.VERBOSE, "Mailbox received destination parcel");
		} else if (parcel instanceof ForeignParcel) {
			forward_queue.offer((ForeignParcel) parcel);
			getOwner().nodeLog(SelfNode.ErrorStatus.INFO, SelfNode.LogLevel.COMMON, "Mailbox received foreign parcel", ((ForeignParcel) parcel).toString());
		} else if (parcel == null) {
			getOwner().nodeLog(SelfNode.ErrorStatus.FAIL, SelfNode.LogLevel.COMMON, "Mailbox received null parcel from network controller");
		} else {
			getOwner().nodeLog(SelfNode.ErrorStatus.FAIL, SelfNode.LogLevel.COMMON, "Mailbox received parcel with unknown classification");
		}
	}

	public void processDestinationParcel(DestinationParcel parcel) {
		getOwner().nodeLog(SelfNode.ErrorStatus.INFO, SelfNode.LogLevel.COMMON, "Processing received destination parcel", parcel.toString());

		try {
			// Learn path
			getOwner().learnPath(parcel.getActualPath(), null);
			
			// Do any required action
			parcel.onReceiveAction();
		} catch (MissingParcelDetailsException | BadProtocolException ex) {
			getOwner().nodeLog(2, "Could not handle received parcel: " + ex.getMessage());
		} catch (BadPathException ex) {
			getOwner().nodeLog(2, "Cannot learn path from received parcel: " + ex.getMessage());
		}
	}

	private void enqueueOutbound(ForeignParcel parcel) {
		forward_queue.offer(parcel);
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

	public int getOutboxCount() {
		return outbox.size();
	}

	public NetworkController getNetworkController() {
		return network_controller;
	}

	public void placeInOutbox(DestinationParcel parcel) {
		sent_parcels.add(parcel.getUponResponseAction());
		parcel.setInOutbox();
		outbox.add(parcel);
	}

	public String listOutbox() {
		String construct = "No parcels in outbox.";

		if (outbox.size() > 0) {
			construct = "";

			for (DestinationParcel parcel : outbox) {
				construct += "\n-- " + parcel.toString();
			}
		}

		return construct;
	}
	
	public void processForwardQueue(){
		if(forward_queue.size() > 0){
			ForeignParcel parcel = forward_queue.poll();
			try {
				network_controller.sendParcel(parcel);
			} catch (MissingParcelDetailsException | BadProtocolException ex) {
				getOwner().nodeLog(2, "Could not hand off to network controller: " + ex.getMessage());
			}
		}
	}
	
	public void pingPinnedNode(int i){
		if(pinned_nodes == null){
			return;
		}
		
		RemoteNode pinned_node = pinned_nodes.get(i);
		if(Protocol.elapsedSeconds(pinned_node.getLastPinged()) >= getOwner().PINNED_NODE_RE_PING_TIME){
			pingRemote(pinned_node);
		}
	}

	public int getPinnedNodeCount(){
		return pinned_nodes.size();
	}
	
	public void processOutboxItem(int i) {
		DestinationParcel parcel = outbox.get(i);
		//getOwner().nodeLog(0, "Attemping to send "+parcel.toString());
		RemoteNode destination = (RemoteNode) parcel.getDestination();

		try {
			if(!destination.isOnline() && parcel.requiresOnlinePath()){
				pingRemote(destination);
			}
			
			if (parcel.readyToSend()) {
				// Give to the network controller for sending
				try {
					forward_queue.offer(parcel.constructForeignParcel());
					parcel.setUsedPath();
					parcel.setIsSent();
					parcel.setTimeSent();
					parcel.getOutboundActualPath().pending();
					parcel_history_archive.addParcelHistoryItem(parcel.getUniqueID(), parcel.getParcelType());
					outbox.remove(i);
				} catch (UnknownResponsibilityException | MissingParcelDetailsException | BadProtocolException ex) {
					getOwner().nodeLog(2, "Could not hand off to network controller: " + ex.getMessage());
				}
			} else if (!parcel.isActualPathKnown() && parcel.consultWhenPathUnknown()) {
				consultTrustedForPath(destination);
			}
		} catch (MissingParcelDetailsException | BadProtocolException | BadPathException ex) {
			getOwner().nodeLog(2, "Could not send parcel: " + ex.getMessage());
		}
	}
	
	public void pingRemote(RemoteNode remote){
		PingParcel ping_parcel = new PingParcel(this, TransferDirection.OUTBOUND);
		ping_parcel.setDestination(remote);
		
		if(!expectingResponse(ping_parcel)){
			try {
				ping_parcel.placeInOutbox();
			} catch (MissingParcelDetailsException | BadProtocolException ex) {
				getOwner().nodeLog(ErrorStatus.FAIL, LogLevel.COMMON, "Could not ping remote", ex.getMessage());
			}
		}
	}
	
	public void cleanSentParcel(int i){
		if(i < sent_parcels.size() && i >= 0){
			SentParcel sent_parcel = sent_parcels.get(i);
			
			// Check if it was successful
			if(sent_parcel.isSuccessful()){
				if(Protocol.elapsedMillis(sent_parcel.getResponseParcel().getTimeReceived()) > sent_parcel.getOriginalParcel().getResendCooldown()){
					getOwner().nodeLog(ErrorStatus.GOOD, LogLevel.COMMON, "Sent parcel was responded to successfully and cooldown reached, erased from cache");
					// Remove successful sent/receive parcel combos
					sent_parcels.remove(i);
				}
			}else{
				if(Protocol.elapsedMillis(sent_parcel.getOriginalParcel().getTimeCreated()) > sent_parcel.getOriginalParcel().getResponseWaitExpiry()){
					getOwner().nodeLog(ErrorStatus.FAIL, LogLevel.ATTENTION, "Response wait expiry ("+sent_parcel.getOriginalParcel().getResponseWaitExpiry()+"ms) reached for original parcel", sent_parcel.getOriginalParcel().toString());
					sent_parcels.remove(i);
				}
			}
		}
	}

	public ParcelHistoryArchive getParcelHistoryArchive(){
		return parcel_history_archive;
	}
	
	public int getSentParcelCount(){
		return sent_parcels.size();
	}

	/**
	 * Consult trusted nodes for a path to a specific node.
	 *
	 * @param node Target node to look for.
	 */
	private void consultTrustedForPath(RemoteNode node) {
		ArrayList<RemoteNode> consult_list = new ArrayList<>();

		// A neighbor has an implicitly defined path, so it can never be the target of a search.
		if (!owner.isNeighbor(node)) {
			for (Neighbor neighbor : getOwner().getNeighbors()) {
				// Add every community member to the consult list.
				if (!consult_list.contains(neighbor.getNode())) {
					consult_list.add(neighbor.getNode());
				}
			}

			for (RemoteNode trusted_node : getOwner().getTrustedNodes()) {
				if (!consult_list.contains(trusted_node)) {
					// Add all trusted nodes to the consult list.
					consult_list.add(trusted_node);
				}
			}

			// Now consult the nodes
			for (RemoteNode consultant : consult_list) {
				if (!consultant.equals(node)) {
					// Only consult a node if the consultant is NOT the node we're looking for.
					FindParcel find = new FindParcel(this, TransferDirection.OUTBOUND);
					find.setTarget(node);
					find.setDestination(consultant);

					if (!expectingResponse(find)) {
						owner.nodeLog(1, "Consulting " + find.getDestination().getAddress() + " for path to " + find.getTarget().getAddress());
						placeInOutbox(find);
					}
				}
			}
		}
	}

	/**
	 * Checks if there is an active signal out in the network that we are
	 * expecting a response to. Used to protect against spamming the network.
	 */
	private boolean expectingResponse(DestinationParcel parcel) {
		for (SentParcel existing_action : sent_parcels) {
			if (existing_action.getOriginalParcel().equals(parcel)) {
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

	public void processInboundQueue() {
		if (inbound_queue.size() > 0) {
			try {
				receiveParcel(Parcel.unserialize(inbound_queue.poll(), getOwner()));
			} catch (MissingParcelDetailsException | BadProtocolException ex) {
				getOwner().nodeLog(2, "Could not receive parcel", ex.getMessage());
			}
		}
	}

	public SentParcel getSentParcel(String unique_id) {
		for (SentParcel sent_parcel : sent_parcels) {
			if (sent_parcel.getOriginalParcel().getUniqueID().equals(unique_id)) {
				return sent_parcel;
			}
		}

		return null;
	}

	private final SelfNode owner; // The selfnode that runs the mailbox
	private final MailboxWorker worker;
	private final ArrayList<SentParcel> sent_parcels = new ArrayList<>();
	private final NetworkController network_controller;
	private final ArrayList<RemoteNode> pinned_nodes = new ArrayList<>();
	private final Queue<ForeignParcel> forward_queue = new LinkedBlockingQueue<>(); // The forward queue is made up of foreign parcels ready to be sent.
	private final ArrayList<DestinationParcel> outbox = new ArrayList<>(); // The outbox is made up of destination parcels that are waiting for the right conditions to send
	private final Queue<JSONObject> inbound_queue = new LinkedBlockingQueue<>(); // The inbound queue is made up of received JSON objects that need to be processed
	private final ParcelHistoryArchive parcel_history_archive = new ParcelHistoryArchive();
}
