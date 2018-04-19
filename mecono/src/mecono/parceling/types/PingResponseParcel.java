package mecono.parceling.types;

import mecono.node.Mailbox;
import mecono.node.Path;
import mecono.node.PathStats;
import mecono.parceling.MissingParcelDetailsException;
import mecono.parceling.ParcelType;
import mecono.parceling.ResponseParcel;
import mecono.parceling.SentParcel;
import mecono.protocol.BadProtocolException;

/**
 *
 * @author jak
 */
public class PingResponseParcel extends ResponseParcel {

	public PingResponseParcel(Mailbox mailbox, TransferDirection direction) {
		super(mailbox, direction);
	}

	@Override
	public void onReceiveAction() throws BadProtocolException, MissingParcelDetailsException {
		super.onReceiveAction();
		
		SentParcel sent_parcel = getSentParcel();
		
		if(sent_parcel.isSuccessful()){
			long ping = sent_parcel.getPing();
			PingParcel original_parcel = (PingParcel) sent_parcel.getOriginalParcel();
			PathStats used_path = original_parcel.getOutboundActualPath();
			
			used_path.setPing(ping);
		}
	}
	
	@Override
	public ParcelType getParcelType() {
		return ParcelType.PING_RESPONSE;
	}
}
