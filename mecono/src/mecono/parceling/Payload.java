package mecono.parceling;

import org.json.JSONObject;

/**
 *
 * @author jak
 */
public class Payload {
	public Payload(Parcel parcel){
		this.parcel = parcel;
	}
	
	public JSONObject serialize() {
		JSONObject serialized = new JSONObject();

		try {
			serialized.put("path_history", parcel.getPath());
			serialized.put("destination", parcel.getDestination().getAddress());
			serialized.put("parcel_type", Parcel.getParcelTypeCode(parcel.getParcelType()));
			serialized.put("unique_id", parcel.getUniqueID());
			serialized.put("actual_path", parcel.getActualPath());
			serialized.put("content", parcel.getSerializedContent());
			serialized.put("signature", parcel.getSignature());
		} catch (MissingParcelDetailsException ex) {
			parcel.getMailbox().getOwner().nodeLog(2, "Could not serialized payload: " + ex.getMessage());
		}

		return serialized;
	}
	
	private final Parcel parcel;
}
