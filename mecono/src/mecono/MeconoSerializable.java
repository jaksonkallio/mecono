package mecono;

import org.json.JSONObject;

public interface MeconoSerializable {
	public JSONObject serialize();
	public static MeconoSerializable deserialize(JSONObject json){return null;};
}
