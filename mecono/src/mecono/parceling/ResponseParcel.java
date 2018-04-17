package mecono.parceling;

import mecono.node.Mailbox;
import mecono.node.Path;
import mecono.node.PathStats;
import mecono.protocol.BadProtocolException;

/**
 *
 * @author jak
 */
public class ResponseParcel extends DestinationParcel {

	public ResponseParcel(Mailbox mailbox, TransferDirection direction) {
		super(mailbox, direction);
	}

	/**
	 * A "responded ID" is the ID of the parcel that triggered a response from
	 * the remote. For example, a ping parcel with ID 1234 is sent to a remote
	 * node. The response will have parameter "responded_id" set to 1234 so the
	 * self node knows what original parcel the response parcel is referencing.
	 *
	 * @return
	 */
	public String getRespondedID() {
		return respond_to_id;
	}
	
	public SentParcel getSentParcel(){
		return mailbox.getSentParcel(getRespondedID());
	}
	
	@Override
	public void onReceiveAction() throws BadProtocolException, MissingParcelDetailsException {
		super.onReceiveAction();
		
		// Update the sent parcel
		SentParcel responding_to = getSentParcel();
		
		if(responding_to != null){
			responding_to.giveResponse(this);

			// If successful send, we can 
			if(responding_to.isSuccessful()){
				PathStats path_used = responding_to.getOriginalParcel().getOutboundActualPath();
				path_used.success();
			}
		}else{
			throw new MissingParcelDetailsException("Unwarranted response (or original parcel timeout)");
		}
	}

	public void setRespondedID(String respond_to_id) {
		if (Parcel.validUniqueID(respond_to_id)) {
			this.respond_to_id = respond_to_id;
		}
	}

	private String respond_to_id;
}
