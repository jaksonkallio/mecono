package parcel;

public abstract class Response extends Parcel {
	public String getTriggerID(){
		return trigger_id;
	}
	
	public void setTriggerID(String trigger_id){
		this.trigger_id = trigger_id;
	}
	
	public abstract ParcelType getTriggerType();
	
	private String trigger_id;
}
