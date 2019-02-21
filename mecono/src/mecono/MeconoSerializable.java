package mecono;

import node.BadSerializationException;
import org.json.JSONObject;

public interface MeconoSerializable {
	public JSONObject serialize();
	public void deserialize(JSONObject json) throws BadSerializationException {return null;};
}
