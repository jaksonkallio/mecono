package mecono.node;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import mecono.parceling.DestinationParcel;
import mecono.parceling.MissingParcelDetailsException;
import mecono.parceling.Handshake;
import mecono.parceling.types.FindParcel;
import mecono.parceling.types.PingParcel;

/**
 * The Sent Parcel History is a complete archive of recently sent, or queued to be sent, parcels into the Mecono network.
 * @author Jakson
 */
public class HandshakeHistory {
	public HandshakeHistory(Mailbox mailbox){
		this.mailbox = mailbox;
	}
	
	public void prune(){
	
	}
	
	public void enqueueSend(Handshake handshake){
		if(!alreadyPending(handshake.getOriginalParcel())){
			mailbox.getOwner().nodeLog(SelfNode.ErrorStatus.INFO, SelfNode.LogLevel.COMMON, "Enqueued parcel for send", handshake.getOriginalParcel().toString());
			pending.add(handshake);
		}
	}
	
	public void enqueueSend(DestinationParcel parcel){
		if(parcel.getTransferDirection() == DestinationParcel.TransferDirection.OUTBOUND){
			Handshake handshake = new Handshake(parcel);
			enqueueSend(handshake);
		}	
	}
	
	public void attemptSend(){
		if(send_cursor < pending.size()){
			Handshake handshake = pending.get(send_cursor);
			
			// First check consists of readiness based on outbound information
			// Must be unsent, must not be stale, and must be ready to resend
			if(!handshake.isSent()
					&& !handshake.isStale()
					&& handshake.readyResend()){
				DestinationParcel original_parcel = handshake.getOriginalParcel();
				
				try {
					// Second check consists of readiness based on the actual parcel metadata
					if(original_parcel.pathKnown()){
						if(original_parcel.pathOnline()){
							// Send
						}else{
							// Ping the destination
							pingPath(original_parcel.getActualPath());
						}
					}else{
						// Consult for a path to the destination
						consultPath(((RemoteNode) original_parcel.getDestination()));
					}
				} catch(MissingParcelDetailsException ex){
					
				}
			}
		}
		
		// Increment the cursor
		if(pending.size() > 0){
			send_cursor = (send_cursor + 1) % pending.size();
		}
	}
	
	public String listPending() {
		String construct = "No parcels in outbox.";

		if (pending.size() > 0) {
			construct = "";

			for (Handshake handshake : pending) {
				construct += "\n-- " + handshake.getOriginalParcel().toString();
			}
		}

		return construct;
	}
	
	public int getPendingCount(){
		return pending.size();
	}
	
	private void pingPath(Path path){
		PingParcel ping = new PingParcel(mailbox, DestinationParcel.TransferDirection.OUTBOUND);
		ping.setDestination((RemoteNode) path.getLastStop());
		enqueueSend(ping);
	}
	
	private void consultPath(RemoteNode target){
		ArrayList<RemoteNode> consult_list = new ArrayList<>();
		SelfNode self = mailbox.getOwner();
		
		// A neighbor has an implicitly defined path, so it can never be the target of a search.
		if (!self.isNeighbor(target)) {
			for (Neighbor neighbor : self.getNeighbors()) {
				// Add every community member to the consult list.
				if (!consult_list.contains(neighbor.getNode())) {
					consult_list.add(neighbor.getNode());
				}
			}

			for (RemoteNode trusted_node : self.getTrustedNodes()) {
				if (!consult_list.contains(trusted_node)) {
					// Add all trusted nodes to the consult list.
					consult_list.add(trusted_node);
				}
			}

			// Now consult the nodes
			for (RemoteNode consultant : consult_list) {
				if (!consultant.equals(target)) {
					// Only consult a node if the consultant is NOT the node we're looking for.
					FindParcel find = new FindParcel(mailbox, DestinationParcel.TransferDirection.OUTBOUND);
					find.setTarget(target);
					find.setDestination(consultant);
					enqueueSend(find);
				}
			}
		}
	}
	
	private boolean alreadyPending(DestinationParcel parcel){
		for(Handshake handshake : pending){
			if(handshake.getOriginalParcel().equals(parcel)){
				return true;
			}
		}
		
		return false;
	}
	
	private int send_cursor = 0;
	private final List<Handshake> pending = new ArrayList<>();
	private final Queue<Handshake> completed = new LinkedList<>();
	private final Mailbox mailbox;
}
