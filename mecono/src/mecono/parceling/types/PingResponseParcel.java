package mecono.parceling.types;

import mecono.node.Mailbox;
import mecono.node.PathStats;
import mecono.parceling.MissingParcelDetailsException;
import mecono.parceling.ParcelType;
import mecono.parceling.ResponseParcel;
import mecono.parceling.Handshake;
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
		
		Handshake sent_parcel = getHandshake();
		
		if(sent_parcel.hasResponse()){
			long ping = sent_parcel.getPing();
			PingParcel original_parcel = (PingParcel) sent_parcel.getTriggerParcel();
			PathStats used_path = original_parcel.getOutboundActualPath();
			
			used_path.setPing(ping);
		}
	}
	
	@Override
	public ParcelType getParcelType() {
		return ParcelType.PING_RESPONSE;
	}
	
	@Override
	public boolean requiresOnlinePath(){
		return false;
	}
	
	@Override
	public boolean getRequireOnlinePath(){
		return false;
	}
}
