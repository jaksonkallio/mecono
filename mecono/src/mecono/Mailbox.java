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

	public void receiveParcel(String ser_parcel) {
		try {
			Parcel parcel = unserializeParcel(ser_parcel);
			
			if (parcel instanceof DestinationParcel) {
				// TODO: Call self node received parcel method
			} else if(parcel instanceof ForeignParcel) {
				outbound_queue.offer((ForeignParcel) parcel);
			}
		} catch (UnknownResponsibilityException | BadProtocolException ex) {
			owner.nodeLog(2, "Bad parcel received.");
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
		if(((RemoteNode) parcel.getDestination()).isReady()){
			
		}
	}

	/**
	 * Convert an unencrypted serialized parcel into a Parcel object.
	 *
	 * @param ser_parcel
	 * @return Parcel
	 */
	private Parcel unserializeParcel(String ser_parcel) throws BadProtocolException, UnknownResponsibilityException {
		/*
		`[...]` denotes encrypted payload that only destination may access.
		
		pathhistory,[destination,parceltype,originator,content,signature(destination+originator+content)]
		 */
		Parcel received_parcel = null;
		List<String> pieces = Arrays.asList(ser_parcel.split(","));
		Path path = Path.unserialize(pieces.get(0), owner);

		if (pieces.get(1).equals(owner.getAddress())) {			
			// We are the destination
			ParcelType parcel_type = DestinationParcel.unserializePalletType(pieces.get(2));
			
			switch(parcel_type){
				case PING:
					received_parcel = new PingParcel();
				case PING_RESPONSE:
					received_parcel = new PingResponseParcel();
				case FIND:
					received_parcel = new FindParcel();
				case FIND_RESPONSE:
					received_parcel = new FindResponseParcel();
				case DATA:
					received_parcel = new DataParcel();
				case DATA_RECEIPT:
					received_parcel = new DataReceiptParcel();
				case UNKNOWN:
					throw new UnknownResponsibilityException("Parcel type not recognized.");
			}
			
			received_parcel.setPath(path);
		} else {
			// We are not the destination

			// If selfnode is the second to last node in the path history (last node in history would be the next node) then parcel is in the right place
			if (!path.getStop(path.getPathLength() - 2).equals(owner)) {
				throw new UnknownResponsibilityException("SelfNode isn't meant to have this parcel at this point in the path.");
			}

			//return new ForeignParcel(path_history, pieces.get(1));
		}
		
		return received_parcel;
	}

	private final SelfNode owner; // The selfnode that runs the mailbox
	//private ArrayList<Pallet> partial_pallets = new ArrayList<Pallet>(); // Inbound, for building up pallets
	private ArrayList<UponResponseAction> upon_response_actions;
	private final NetworkController network_controller;
	private Queue<ForeignParcel> outbound_queue; // Outbound queue
	private ArrayList<DestinationParcel> outbox = new ArrayList<>();
}
