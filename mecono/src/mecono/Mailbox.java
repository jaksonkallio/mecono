package mecono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

/**
 * The mailbox is responsible for managing parcel sending/receiving, queuing
 * parcels, and piecing together parcel streams and notifying the SelfNode with
 * complete streams.
 *
 * @author jak
 */
public class Mailbox {

	public Mailbox(SelfNode owner) {
		this.owner = owner;
		this.network_controller = new NetworkController(this);
	}

	public boolean sendMessage(PalletType stream_type, RemoteNode destination, String message_text) {
		Pallet message = new Pallet(this);
		message.createNewMessage(stream_type, destination, message_text);

		return true;
	}

	public void receiveParcel(String ser_parcel) {
		try {
			Parcel parcel = unserializeParcel(ser_parcel);
			
			if (parcel.isFinalDest()) {
				partial_pallets.add(parcel.getPalletParent());
				checkForCompletedPallets();
			} else {
				outbound_queue.offer(parcel);
			}
		} catch (UnknownResponsibilityException | BadProtocolException ex) {
			owner.nodeLog(2, "Bad parcel received.");
		}
	}
	
	private void checkForCompletedPallets(){
		for (int i = 0; i < partial_pallets.size(); i++) {
			if (partial_pallets.get(i).allParcelsReceived()) {
				owner.receiveCompletePallet(partial_pallets.get(i));
				partial_pallets.remove(i);
			}
		}
	}
	
	private void enqueueOutbound(Pallet pallet){
		for(int i = 0; i < pallet.getParcelCount(); i++){
			enqueueOutbound(pallet.getParcelByIndex(i));
		}
	}
	
	private void enqueueOutbound(Parcel parcel){
		if(!parcel.isFinalDest()){
			outbound_queue.offer(parcel);
		}
	}

	public Pallet getPalletByID(String stream_id) {
		// Search known streams for the Stream ID.
		for (Pallet pallet : partial_pallets) {
			if (pallet.getStreamID() == stream_id) {
				return pallet;
			}
		}

		// Stream ID not found
		return new Pallet(this, stream_id);
	}
	
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
	
	public void listPartialStreams(){
		String str = "";
		
		if(partial_pallets.size() > 0){
			for(Pallet pallet : partial_pallets){
				str += "  "+pallet.toString()+"\n";
			}
		}else{
			str = "Mailbox has no partially built pallets.";
		}
		
		
		owner.nodeLog(0, str);
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
		
		pathhistory,[destination,pallettype,streamid,originator,parcelcount,parcelid,content,signature(destination+originator+streamid+parcelcount+content)]
		 */

		List<String> pieces = Arrays.asList(ser_parcel.split(","));
		ArrayList<RemoteNode> path_nodes = new ArrayList<>();
		for (String remote_node_address : pieces.get(0).split("-")) {
			path_nodes.add(SelfNode.getRemoteNode(remote_node_address));
		}
		Path path_history = new Path(path_nodes);

		if (pieces.get(1).equals(owner.getAddress())) {
			// We are the destination

			Pallet pallet_parent = getPalletByID(pieces.get(3));
			if (pallet_parent.getPalletType() == PalletType.UNKNOWN) {
				pallet_parent.setPalletType(Protocol.unserializePalletType(pieces.get(2)));
			}

			RemoteNode originator = SelfNode.getRemoteNode(pieces.get(4));

			return new Parcel(pallet_parent, path_history, originator, Integer.parseInt(pieces.get(6)), pieces.get(7), pieces.get(8));
		} else {
			// We are not the destination

			// If selfnode is the second to last node in the path history (last node in history would be the next node) then parcel is in the right place
			if (!path_nodes.get(path_nodes.size() - 2).equals(owner)) {
				throw new UnknownResponsibilityException("SelfNode isn't meant to have this parcel at this point in the path.");
			}

			return new Parcel(path_history, pieces.get(1));
		}
	}

	private final SelfNode owner; // The selfnode that runs the mailbox
	private ArrayList<Pallet> partial_pallets = new ArrayList<Pallet>(); // Inbound, for building up parcel streams
	private final NetworkController network_controller;
	private Queue<Parcel> outbound_queue; // Outbound queue
}
