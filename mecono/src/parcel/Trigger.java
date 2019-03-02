package parcel;

import mecono.Self;
import mecono.Util;

public abstract class Trigger extends Terminus {
	public Trigger(Self self){
		super(self);
	}
    
    public void setResponse(Response response){
        this.response = response;
        time_responded = Util.time();
    }
    
    public void responseAction(){
        
    }
    
    public boolean isResponse(Response response){
		return getID().equals(response.getTriggerID());
	}
    
    public Response response;
    public long time_queued;
    public long time_sent;
    public long time_responded;
}
