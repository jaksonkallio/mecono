package mecono.parceling;

import mecono.node.Node;
import mecono.node.RemoteNode;
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
	
	public PayloadType getPayloadType(){
		return PayloadType.UNKNOWN;
	}
	
	public JSONObject serialize() {
		JSONObject serialized = new JSONObject();

		serialized.put("parcel_type", Parcel.getParcelTypeCode(parcel.getPayloadType()));
		serialized.put("unique_id", parcel.getUniqueID());
		serialized.put("content", serializeContent());

		return serialized;
	}
	
	public JSONObject serializeContent(){
		return new JSONObject();
	}
	
	public void onReceiveAction() throws BadProtocolException, MissingParcelDetailsException {
		
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
