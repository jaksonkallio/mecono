package parcel;

import mecono.Self;

public abstract class Parcel {
	public Parcel(Self self){
		this.self = self;
	}
	
	public abstract ParcelType getParcelType();
	public String getID(){
		return "ABCD";
	}
	
	public boolean isResponse(Response response){
		return getID().equals(response.getTriggerID());
	}
	
	public void process(){
		
	}
	
	private final Self self;
}
