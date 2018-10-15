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
	
	public PayloadType getParcelType(){
		return PayloadType.UNKNOWN;
	}
	
	public JSONObject serialize() {
		JSONObject serialized = new JSONObject();

		serialized.put("parcel_type", Parcel.getParcelTypeCode(parcel.getParcelType()));
		serialized.put("unique_id", parcel.getUniqueID());
		serialized.put("content", serializeContent());

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
	
	public String getEncryptedPayload(){
		return "ENCRYPTEDPAYLOAD";
	}
	
	public Parcel getParcel(){
		return parcel;
	}
	
	public boolean consultWhenPathUnknown() {
		return true;
	}

	public boolean requiresOnlinePath() {
		return getRequireOnlinePath();
	}
	
	public boolean getRequireOnlinePath() {
		return true;
	}
	
	private Parcel parcel;
}
