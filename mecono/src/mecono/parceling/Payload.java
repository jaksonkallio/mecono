package mecono.parceling;

import mecono.parceling.types.DataPayload;
import mecono.parceling.types.DataResponsePayload;
import mecono.parceling.types.FindPayload;
import mecono.parceling.types.FindResponsePayload;
import mecono.parceling.types.PingPayload;
import mecono.parceling.types.PingResponsePayload;
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
		serialized.put("seq", parcel.getSeqNum());
		serialized.put("unique_id", parcel.getUniqueID());
		serialized.put("content", serializeContent());

		return serialized;
	}
	
	public final Payload deserialize(JSONObject payload_json) throws MissingParcelDetailsException {
		if(!payload_json.has("type")){
			throw new MissingParcelDetailsException("Missing payload type");
		}
		
		if(!payload_json.has("content")){
			throw new MissingParcelDetailsException("Missing payload content");
		}
		
		PayloadType payload_type = deserializePayloadType(payload_json.getString("type"));
		Payload payload = createPayload(payload_type);
		payload.deserializeContent(payload_json.getJSONObject("content"));
		
		return payload;
	}
	
	public static Payload createPayload(PayloadType payload_type){
		switch(payload_type){
			case PING:
				return new PingPayload();
			case PING_RESPONSE:
				return new PingResponsePayload();
			case FIND:
				return new FindPayload();
			case FIND_RESPONSE:
				return new FindResponsePayload();
			case DATA:
				return new DataPayload();
			case DATA_RESPONSE:
				return new DataResponsePayload();
		}
		
		return null;
	}
	
	public static PayloadType deserializePayloadType(String parcel_type) {
		switch (parcel_type) {
			case "PING":
				return PayloadType.PING;
			case "PING_RESPONSE":
				return PayloadType.PING_RESPONSE;
			case "FIND":
				return PayloadType.FIND;
			case "FIND_RESPONSE":
				return PayloadType.FIND_RESPONSE;
			case "DATA":
				return PayloadType.DATA;
			case "DATA_RESPONSE":
				return PayloadType.DATA_RESPONSE;
			case "ANNC":
				return PayloadType.ANNC;
			default:
				return PayloadType.UNKNOWN;
		}
	}
	
	public JSONObject serializeContent(){
		return new JSONObject();
	}
	
	public void deserializeContent(JSONObject content){};
	
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
