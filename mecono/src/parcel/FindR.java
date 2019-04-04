package parcel;

import mecono.Self;
import node.AdjacencyList;

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
	public void receive(){
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
