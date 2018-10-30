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
	
	public boolean getRequireOnlinePath() {
		return true;
	}
	
	public boolean getResolveUnknownPath(){
		return true;
	}
	
	// Wait time for a response before the trigger parcel is considered stale
	public long getStaleTime(){
		return 30000;
	}
	
	// Number of times to retry sending the parcel of this payload type after stale
	public int getMaxRetryCount(){
		return 10;
	}
	
	// Whether there is a maximum number of retries
	public boolean getRetryIndefinitely(){
		return false;
	}
	
	private Parcel parcel;
}
