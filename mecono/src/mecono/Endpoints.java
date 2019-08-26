package mecono;

import static spark.Spark.*;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import parcel.Terminus;

public class Endpoints {
	private Self self;
	
	public Endpoints(Self self) {
		this.self = self;
	}
	
	public void initialize() {
		get("/hello", (req, res) -> "Hello World");
		
		get("/outbox", (req, res) -> {
			List<Terminus> send_queue = self.getSendQueue();
			
			JSONArray outbox_json = new JSONArray();
			
			for(Terminus parcel : send_queue) {
				JSONObject outbox_item_json = new JSONObject();
				
				if(parcel.getDestination() != null) {
					outbox_item_json.put("destination", parcel.getDestination().getTrimmedAddress());
				}
				
				if(parcel.getChain() != null) {
					outbox_item_json.put("chain", parcel.getChain().toString());
				}
				
				outbox_item_json.put("online", parcel.getChain().online());
				outbox_item_json.put("online_required", parcel.requireOnlineChain());
				
				outbox_json.put(outbox_item_json);
			}
			
			return response(null);
		});
	}
	
	public void stopServer() {
		stop();
	}
	
	public String response(JSONObject payload) {
		JSONObject json = new JSONObject();
		
		json.put("status", 200);
		json.put("payload", payload);
		
		return json.toString();
	}
}
