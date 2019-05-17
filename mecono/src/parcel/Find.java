package parcel;

import mecono.Self;
import node.BadProtocolException;
import node.BadSerializationException;
import node.MNode;
import org.json.JSONObject;

public class Find extends Trigger {
	public Find(Self self){
		super(self);
	}
	
	@Override
	public ParcelType getParcelType(){
		return ParcelType.FIND;
	}
	
	public void setTarget(MNode target){
		this.target = target;
	}
	
	public MNode getTarget(){
		return target;
	}
	
	@Override
	public void deserialize(JSONObject parcel_json) throws BadSerializationException {
		super.deserialize(parcel_json);
		
		JSONObject content_json = parcel_json.getJSONObject("content");
		String target_address = content_json.getString("target");
		
		if(target_address != null){
			setTarget(getSelf().getNodeDatabase().getNode(target_address));
		}
	}
	
	@Override
	public JSONObject serialize(){
		JSONObject parcel_json = super.serialize();
		JSONObject content_json = parcel_json.getJSONObject("content");
		
		content_json.put("target", getTarget().getAddress());
		
		return parcel_json;
	}
	
	@Override
	public void receive() throws BadProtocolException {
		super.receive();
		
		if(getTarget() == null){
			throw new BadProtocolException("Unspecified target");
		}
		
		FindR new_response = new FindR(getSelf());
		new_response.setTriggerID(getID());
        new_response.setDestination(getChain().getOriginNode());
		new_response.setKnowledge(getSelf().getSelfNode().getGroup(KNOWLEDGE_GROUP_SIZE));
		setResponse(new_response);
		new_response.enqueueSend();
	}
	
	@Override
	public boolean isDuplicate(Parcel o){
		if(o instanceof Find){
			Find other = (Find) o;
			
			if(getTarget().equals(other.getTarget())){
				return super.isDuplicate(o);
			}
		}
		
		return false;
	}
	
	public static final int KNOWLEDGE_GROUP_SIZE = 30;
	
	private MNode target;
}
