package mecono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
	}

	/*public boolean sendMessage(ParcelType stream_type, RemoteNode destination, String message_text) {
		Pallet message = new Pallet(this);
		message.createNewMessage(stream_type, destination, message_text);

		return true;
	}*/

	public void receiveParcel(Parcel parcel) {
		if (parcel instanceof DestinationParcel) {
			// TODO: Call self node received parcel method
		} else if(parcel instanceof ForeignParcel) {
			outbound_queue.offer((ForeignParcel) parcel);
		}
	}
	
	private void enqueueOutbound(ForeignParcel parcel){
		outbound_queue.offer(parcel);
	}

	/*public Pallet getPalletByID(String stream_id) {
		// Search known streams for the Stream ID.
		for (Pallet pallet : partial_pallets) {
			if (pallet.getPalletID() == stream_id) {
				return pallet;
			}
		}

		// Stream ID not found
		return new Pallet(this, stream_id);
	}*/
	
	/**
	 * Gets the owner of the mailbox.
	 * @return 
	 */
	public SelfNode getOwner() {
		return owner;
	}
	
	public NetworkController getNetworkController() {
		return network_controller;
	}
	
	public void placeInOutbox(DestinationParcel parcel){
		parcel.setInOutbox();
		outbox.add(parcel);
	}
	
	private void processOutboxItem(int i){
		DestinationParcel parcel = outbox.get(i);
		if((((RemoteNode) parcel.getDestination()).isReady()) && parcel.hasCompletePath()){
			// The remote node has at least one sufficient path to it, and the parcel has a complete path to the destination.
			
			// Create the response action/expectation
			UponResponseAction response_action = new UponResponseAction(this, parcel);
			
			// Give to the network controller for sending
			network_controller.sendParcel(parcel);
		}
	}

	private final SelfNode owner; // The selfnode that runs the mailbox
	//private ArrayList<Pallet> partial_pallets = new ArrayList<Pallet>(); // Inbound, for building up pallets
	private ArrayList<UponResponseAction> upon_response_actions;
	private final NetworkController network_controller;
	private Queue<ForeignParcel> outbound_queue; // Outbound queue
	private ArrayList<DestinationParcel> outbox = new ArrayList<>();
}
