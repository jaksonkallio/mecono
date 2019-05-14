package parcel;

import mecono.ErrorLevel;
import mecono.Self;
import node.BadProtocolException;
import node.BadSerializationException;
import node.InsufficientKnowledgeException;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class Response extends Terminus {
	public Response(Self self){
		super(self);
	}
	
	public String getTriggerID(){
		return trigger_id;
	}
	
	public void setTriggerID(String trigger_id){
		this.trigger_id = trigger_id;
	}
	
	@Override
	public JSONObject serialize(){
		JSONObject parcel_json = super.serialize();
		JSONObject content_json = parcel_json.getJSONObject("content");
		
		content_json.put("trigger_id", getTriggerID());
		
		return parcel_json;
	}
	
	@Override
	public void deserialize(JSONObject parcel_json) throws BadSerializationException {
		super.deserialize(parcel_json);
		
		JSONObject content_json = parcel_json.getJSONObject("content");
		
		String trigger_id = content_json.getString("trigger_id");
		setTriggerID(trigger_id);
	}
	
    @Override
    public void receive() throws BadProtocolException {
		super.receive();
		
        try {
            // Lookup the trigger, give it this parcel
            Trigger trigger = getSelf().lookupTrigger(getTriggerID());
            trigger.setResponse(this);
            trigger.logResponse();
        }catch(InsufficientKnowledgeException ex){
            getSelf().log(ErrorLevel.ERROR, "Unable to process response: " + ex.getMessage());
        }
    }
	
	public abstract ParcelType getTriggerType();
	
	private String trigger_id;
}
