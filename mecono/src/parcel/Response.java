package parcel;

import mecono.Self;

public abstract class Response extends Parcel {
	public Response(Self self){
		super(self);
	}
	
	public String getTriggerID(){
		return trigger_id;
	}
	
	public void setTriggerID(String trigger_id){
		this.trigger_id = trigger_id;
	}
	
	public abstract ParcelType getTriggerType();
	
	private String trigger_id;
}
