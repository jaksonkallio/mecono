package mecono.parceling;

import mecono.node.Mailbox;
import mecono.node.ParcelHistoryArchive;
import mecono.node.Path;
import mecono.node.PathStats;
import mecono.node.SelfNode;
import mecono.protocol.BadProtocolException;
import org.json.JSONObject;

/**
 *
 * @author jak
 */
public class ResponseParcel extends DestinationParcel {

	public ResponseParcel(Mailbox mailbox, TransferDirection direction) {
		super(mailbox, direction);
	}
	
	@Override
	public String toString() {
		return super.toString()+"[RespondingTo: "+getRespondedID()+"]";
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
	
	public Handshake getSentParcel(){
		return mailbox.getSentParcel(getRespondedID());
	}
	
	@Override
	public void onReceiveAction() throws BadProtocolException, MissingParcelDetailsException {
		super.onReceiveAction();
		
		// Update the sent parcel
		Handshake responding_to = getSentParcel();
		
		if(responding_to != null){
			responding_to.giveResponse(this);

			// There are a few things we'd like to do upon a successful send with a good response
			// - Mark the path as successful
			// - Update the response value in the parcel history archive
			if(responding_to.hasResponse()){
				DestinationParcel original_parcel = responding_to.getOriginalParcel();
				PathStats path_used = original_parcel.getOutboundActualPath();
				path_used.success();
				ParcelHistoryArchive parcel_history_archive = getMailbox().getParcelHistoryArchive();
				parcel_history_archive.markParcelResponded(original_parcel.getUniqueID(), this);
				getMailbox().getOwner().nodeLog(SelfNode.ErrorStatus.GOOD, SelfNode.LogLevel.VERBOSE, "Marked parcel history archive item as responded to.");
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
	
	@Override
	protected JSONObject encryptAsPayload() throws MissingParcelDetailsException {
		JSONObject payload = super.encryptAsPayload();
		payload.put("responding_to", getRespondedID());
		return payload;
	}
	
	private String respond_to_id;
}
