package mecono;

import java.util.ArrayList;
import java.util.Queue;

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

	/*public boolean sendMessage(ParcelType stream_type, RemoteNode destination, String message_text) {
		Pallet message = new Pallet(this);
		message.createNewMessage(stream_type, destination, message_text);

		return true;
	}*/
	public void receiveParcel(Parcel parcel) {
		if (parcel instanceof DestinationParcel) {
			getOwner().receiveParcel((DestinationParcel) parcel);
		} else if (parcel instanceof ForeignParcel) {
			outbound_queue.offer((ForeignParcel) parcel);
		}
	}

	private void enqueueOutbound(ForeignParcel parcel) {
		outbound_queue.offer(parcel);
	}

	/**
	 * Gets the owner of the mailbox.
	 *
	 * @return
	 */
	public SelfNode getOwner() {
		return owner;
	}
	
	public MailboxWorker getWorker(){
		return worker;
	}
	
	public int getOutboxCount(){
		return outbox.size();
	}
	
	public NetworkController getNetworkController() {
		return network_controller;
	}

	public void placeInOutbox(DestinationParcel parcel) {
		upon_response_actions.add(parcel.getUponResponseAction());
		parcel.setInOutbox();
		outbox.add(parcel);
	}

	public String listOutbox() {
		String construct = "No parcels in outbox.";

		if (outbox.size() > 0) {
			construct = "Parcels in outbox:";

			for (DestinationParcel parcel : outbox) {
				construct += "\n--" + parcel.getUniqueID() + ": " + parcel.getParcelType() + " to " + parcel.getDestination().getAddress() + " ";

				if (!((RemoteNode) parcel.getDestination()).isReady()) {
					construct += "[Dest Not Ready]";
				}

				if (!parcel.hasCompletePath()) {
					construct += "[Missing Path]";
				}
			}
		}

		return construct;
	}

	public void processOutboxItem(int i) {
		DestinationParcel parcel = outbox.get(i);
		RemoteNode destination = (RemoteNode) parcel.getDestination();
		
		if(parcel.pathKnown()){
			if (parcel.readyToSend()) {
				// Create the response action/expectation
				UponResponseAction response_action = new UponResponseAction(this, parcel);

				// Give to the network controller for sending
				network_controller.sendParcel(parcel);
			}
		}else if(parcel.consultWhenPathUnknown()){
			consultTrustedForPath(destination);
		}
	}
	
	/**
	 * Consult trusted nodes for a path to a specific node.
	 * @param node Target node to look for.
	 */
	private void consultTrustedForPath(RemoteNode node) {
		ArrayList<RemoteNode> consult_list = new ArrayList<>();

		for (ArrayList<RemoteNode> community_hop : getOwner().getCommunity()) {
			for (RemoteNode community_member : community_hop) {
				// Add every community member to the consult list.
				if (!consult_list.contains(community_member)) {
					consult_list.add(community_member);
				}
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
			FindParcel find = new FindParcel(this);
			find.setTarget(node);

			try {
				find.setDestination(consultant);
				
				if (!expectingResponse(find)) {
					placeInOutbox(find);
				}
			} catch (BadProtocolException ex) {

			}
		}
	}

	/**
	 * Checks if there is an active signal out in the network that we are
	 * expecting a response to. Used to protect against spamming the network.
	 */
	private boolean expectingResponse(DestinationParcel parcel) {
		for (UponResponseAction existing_action : upon_response_actions) {
			if(existing_action.getOriginalParcel().equals(parcel)){
				return true;
			}
			/*if (existing_action.getOriginalParcel() instanceof FindParcel) {
				if(((FindParcel) parcel).getTarget().equals(((FindParcel) existing_action.getOriginalParcel()).getTarget()) && parcel.getDestination().equals(existing_action.getOriginalParcel().getDestination())){
					// A find response is a duplicate if 
				}
			}*/
		}

		return false;
	}

	private final SelfNode owner; // The selfnode that runs the mailbox
	private final MailboxWorker worker;
	private ArrayList<UponResponseAction> upon_response_actions = new ArrayList<>();
	private final NetworkController network_controller;
	private Queue<ForeignParcel> outbound_queue; // Outbound queue
	private ArrayList<DestinationParcel> outbox = new ArrayList<>();
}
