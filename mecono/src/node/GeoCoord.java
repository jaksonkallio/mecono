package node;

import mecono.MeconoSerializable;
import org.json.JSONObject;

public class GeoCoord implements MeconoSerializable {
	public GeoCoord(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public double dist(GeoCoord o){
		return Math.sqrt(Math.pow(this.x - o.x, 2) + Math.pow(this.y - o.y, 2));
	}
	
	@Override
	public JSONObject serialize() {
		JSONObject coords_json = new JSONObject();
		coords_json.put("x", x);
		coords_json.put("y", y);
		return coords_json;
	}

	public static MeconoSerializable deserialize(JSONObject json) throws BadSerializationException {
		if(json.has("x") && json.has("y")){
			return new GeoCoord(json.getInt("x"), json.getInt("y"));
		}else{
			throw new BadSerializationException("Missing x or y coordinates");
		}
	}
	
	public final int x;
	public final int y;
}
