package mecono.node;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import mecono.parceling.BadPathException;
import mecono.parceling.DestinationParcel;
import mecono.parceling.MissingParcelDetailsException;
import mecono.parceling.SentParcel;
import mecono.protocol.BadProtocolException;

/**
 * The Sent Parcel History is a complete archive of recently sent, or queued to be sent, parcels into the Mecono network.
 * @author Jakson
 */
public class SentParcelHistory {
	public SentParcelHistory(Mailbox mailbox){
		this.mailbox = mailbox;
	}
	
	public void prune(){
	
	}
	
	public void enqueueSend(SentParcel parcel){
		pending.add(parcel);
	}
	
	public void enqueueSend(DestinationParcel parcel){
		if(parcel.getTransferDirection() == DestinationParcel.TransferDirection.OUTBOUND){
			SentParcel sent_parcel = new SentParcel(parcel);
			enqueueSend(sent_parcel);
		}	
	}
	
	public void attemptSend(){
		if(send_cursor < pending.size()){
			SentParcel send = pending.get(send_cursor);
			
			// First check consists of readiness based on outbound information
			// Must be unsent, must not be stale, and must be ready to resend
			if(!send.isSent()
					&& !send.isStale()
					&& send.readyResend()){
				DestinationParcel original_parcel = send.getOriginalParcel();
				
				try {
					// Second check consists of readiness based on the actual parcel metadata
					if(original_parcel.pathKnown()){
						if(original_parcel.pathOnline()){
							
						}
					}else{
						
					}
				} catch(MissingParcelDetailsException ex){
				
				}
			}
		}
		
		// Increment the cursor
		send_cursor = (send_cursor + 1) % pending.size();
	}
	
	private int send_cursor = 0;
	private final List<SentParcel> pending = new ArrayList<>();
	private final Queue<SentParcel> completed = new LinkedList<>();
	private final Mailbox mailbox;
}
