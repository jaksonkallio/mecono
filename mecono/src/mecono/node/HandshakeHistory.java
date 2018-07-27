package mecono.node;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import mecono.parceling.DestinationParcel;
import mecono.parceling.MissingParcelDetailsException;
import mecono.parceling.Handshake;
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
		pending.add(handshake);
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
		send_cursor = (send_cursor + 1) % pending.size();
	}
	
	private void pingPath(Path path){
		PingParcel ping = new PingParcel(mailbox, DestinationParcel.TransferDirection.OUTBOUND);
		ping.setDestination((RemoteNode) path.getLastStop());
		
		if(!alreadyPending(ping)){
			enqueueSend(ping);
		}
	}
	
	private void consultPath(RemoteNode target){
		
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
