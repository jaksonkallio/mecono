package mecono.parceling;

import mecono.protocol.BadProtocolException;
import org.json.JSONObject;

/**
 *
 * @author jak
 */
public abstract class Payload {
	public Payload(Parcel parcel){
		this.parcel = parcel;
	}
	
	public JSONObject serialize() {
		JSONObject serialized = new JSONObject();

		serialized.put("parcel_type", Parcel.getParcelTypeCode(parcel.getParcelType()));
		serialized.put("unique_id", parcel.getUniqueID());
		serialized.put("content", serializeContent());
		serialized.put("signature", parcel.getSignature());

		return serialized;
	}
	
	public JSONObject serializeContent(){
		return new JSONObject();
	}
	
	public void onReceiveAction() throws BadProtocolException, MissingParcelDetailsException {
		if (parcel.getTransferDirection() != Parcel.TransferDirection.INBOUND) {
			throw new BadProtocolException("The parcel isn't inbound");
		}
	}
	
	public Parcel getParcel(){
		return parcel;
	}
	
	private final Parcel parcel;
}
