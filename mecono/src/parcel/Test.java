package parcel;

import mecono.Self;
import node.BadSerializationException;
import node.Node;
import org.json.JSONObject;

public class Test extends Trigger {
	public Test(Self self){
		super(self);
	}
	
    @Override
	public void deserialize(JSONObject parcel_json) throws BadSerializationException {
		super.deserialize(parcel_json);
	}
	
	@Override
    public JSONObject serialize(){
        JSONObject parcel_json = super.serialize();
		JSONObject content_json = new JSONObject();
		
		parcel_json.put("content", content_json);
		
		System.out.println(parcel_json.toString());
		
		return parcel_json;
    }
	
	@Override
	public ParcelType getParcelType(){
		return ParcelType.TEST;
	}
    
    @Override
	public void receive(){
		super.receive();
        
        TestR new_response = new TestR(getSelf());
        new_response.setDestination(getChain().getOriginNode());
		setResponse(new_response);
		new_response.enqueueSend();
    }
    
    @Override
    public boolean requireOnlineChain(){
        return false;
    }
	
	@Override
	public boolean isDuplicate(Parcel o){
		if(o instanceof Test){
			Test other = (Test) o;
			
			return super.isDuplicate(o) && this.getDestination().equals(other.getDestination());
		}
		
		return false;
	}
	
	private Node target;
}
