package parcel;

import mecono.Self;
import org.json.JSONObject;

public class TestR extends Response {
	public TestR(Self self){
		super(self);
	}
	
	@Override
	public boolean requireOnlineChain(){
		return false;
	}
	
	@Override
	public JSONObject serialize(){
		JSONObject parcel_json = super.serialize();
		JSONObject content_json = new JSONObject();
		
		content_json.put("type", getParcelType().name());
		
		parcel_json.put("content", content_json);
		
		return parcel_json;
	}
	
	@Override
	public ParcelType getParcelType(){
		return ParcelType.TESTR;
	}

	@Override
	public ParcelType getTriggerType() {
		return ParcelType.TEST;
	}
}
