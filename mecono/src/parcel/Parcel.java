package parcel;

import mecono.MeconoSerializable;
import mecono.Self;
import node.Chain;
import org.json.JSONObject;

public abstract class Parcel implements MeconoSerializable {
	public Parcel(Self self){
		this.self = self;
	}
	
	public abstract ParcelType getParcelType();
	
	@Override
	public void deserialize(JSONObject parcel_json){
		if(parcel_json.has("chain")){
			Chain chain = new Chain();
			chain.deserialize(parcel_json.getJSONObject("chain"));
			setChain(chain);
		}
	}
	
	public String getID(){
		return "ABCD";
	}
	
	public boolean isResponse(Response response){
		return getID().equals(response.getTriggerID());
	}
	
	public void setChain(Chain chain){
		this.chain = chain;
	}
	
	public Chain getChain(){
		return chain;
	}
	
	public void process(){
		
	}
	
	private final Self self;
	private Chain chain;
}
