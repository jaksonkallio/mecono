package mecono;

import node.BadSerializationException;
import org.json.JSONObject;

public interface MeconoSerializable {
	public JSONObject serialize();
	public static MeconoSerializable deserialize(JSONObject json) throws BadSerializationException {return null;};
}
