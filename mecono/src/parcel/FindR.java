package parcel;

import mecono.Self;
import node.AdjacencyList;
import node.BadProtocolException;
import node.BadSerializationException;
import org.json.JSONObject;

public class FindR extends Response {
	public FindR(Self self){
		super(self);
	}
	
	@Override
	public void deserialize(JSONObject parcel_json) throws BadSerializationException {
		super.deserialize(parcel_json);
		
		JSONObject content_json = parcel_json.getJSONObject("content");
		AdjacencyList des_knowledge = new AdjacencyList(getSelf());
		des_knowledge.deserialize(content_json.getJSONObject("knowledge"));
		setKnowledge(des_knowledge);
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
		
		content_json.put("knowledge", getKnowledge().serialize());
				
		return parcel_json;
	}
	
	@Override
	public void receive() throws BadProtocolException {
		super.receive();
		getSelf().learn(getKnowledge());
	}
	
	public AdjacencyList getKnowledge(){
		return knowledge;
	}
	
	public void setKnowledge(AdjacencyList knowledge){
		this.knowledge = knowledge;
	}
	
	private AdjacencyList knowledge;
}
