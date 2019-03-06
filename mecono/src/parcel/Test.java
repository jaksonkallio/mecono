package parcel;

import mecono.Self;
import node.BadSerializationException;
import node.Node;
import org.json.JSONObject;

public class Test extends Trigger {
	public Test(Self self){
		super(self);
	}
	
	public void setTarget(Node target){
		this.target = target;
	}
	
	public Node getTarget(){
		return target;
	}
	
	@Override
	public boolean isDuplicate(Parcel o){
		if(o instanceof Test){
			Test other = (Test) o;
			
			if(getTarget().equals(other.getTarget())){
				return super.isDuplicate(o);
			}
		}
		
		return false;
	}
	
    @Override
	public void deserialize(JSONObject parcel_json) throws BadSerializationException {
		super.deserialize(parcel_json);
	}
	
	@Override
	public ParcelType getParcelType(){
		return ParcelType.TEST;
	}
	
	private Node target;
}
