package mecono.parceling;

import mecono.protocol.BadProtocolException;
import org.json.JSONObject;

/**
 *
 * @author jak
 */
public abstract class Payload {
	public Payload(){
		
	}
	
	public void setParcel(Parcel parcel){
		this.parcel = parcel;
	}
	
	public ParcelType getParcelType(){
		return ParcelType.UNKNOWN;
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
	
	public boolean consultWhenPathUnknown() {
		return true;
	}

	public boolean requiresOnlinePath() {
		return true;
	}
	
	private Parcel parcel;
}
