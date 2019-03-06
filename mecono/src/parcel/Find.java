package parcel;

import mecono.Self;
import node.Node;

public class Find extends Trigger {
	public Find(Self self){
		super(self);
	}
	
	@Override
	public ParcelType getParcelType(){
		return ParcelType.FIND;
	}
	
	public void setTarget(Node target){
		this.target = target;
	}
	
	public Node getTarget(){
		return target;
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
	
	private Node target;
}
