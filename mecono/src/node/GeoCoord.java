package node;

import mecono.MeconoSerializable;
import org.json.JSONObject;

public class GeoCoord implements MeconoSerializable {
	public GeoCoord(int x, int y){
		this.x = x;
		this.y = y;
	}
    
    public GeoCoord(){
        this(0, 0);
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
    
    @Override
	public void deserialize(JSONObject json) throws BadSerializationException {
        if(!json.has("x") || !json.has("y")){
            throw new BadSerializationException("Missing x or y coordinates");
        }
        
        x = json.getInt("x");
        y = json.getInt("y");
	}
	
	public int x;
	public int y;
}
