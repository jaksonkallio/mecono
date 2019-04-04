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
	public void receive(){
		super.receive();
		
		FindR new_response = new FindR(getSelf());
		new_response.setKnowledge(getSelf().getGroup(getTarget(), KNOWLEDGE_GROUP_SIZE));
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
	
	public static final int KNOWLEDGE_GROUP_SIZE = 100;
	
	private Node target;
}
