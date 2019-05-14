package parcel;

import mecono.Self;
import node.AdjacencyList;
import node.BadProtocolException;
import org.json.JSONObject;

public class FindR extends Response {
	public FindR(Self self){
		super(self);
	}
	
	@Override
	public ParcelType getParcelType(){
		return ParcelType.FINDR;
	}

	@Override
	public ParcelType getTriggerType() {
		return ParcelType.FIND;
	}
	
	@Override
	public JSONObject serialize(){
		JSONObject parcel_json = super.serialize();
		JSONObject content_json = parcel_json.getJSONObject("content");
		
		content_json.put("target_group", getKnowledge().serialize());
				
		return parcel_json;
	}
	
	@Override
	public void receive() throws BadProtocolException {
		super.receive();
	}
	
	public AdjacencyList getKnowledge(){
		return knowledge;
	}
	
	public void setKnowledge(AdjacencyList knowledge){
		this.knowledge = knowledge;
	}
	
	private AdjacencyList knowledge;
}
