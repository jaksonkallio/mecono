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
    
    public void logResponse(){
        getChain().logSuccess(getTimeResponded() - getTimeSent());
    }

    @Override
    public void logSend(){
        getChain().logUse();
        time_sent = Util.time();
    }
    
    public boolean isSent(){
        return getTimeSent() != 0;
    }
    
    public long getTimeSent(){
        return time_sent;
    }
	
	public long getTimeResponded(){
		return time_responded;
	}
    
    public boolean isResponded(){
        return time_responded != 0;
    }
    
    public boolean isResponse(Response response){
		return getID().equals(response.getTriggerID());
	}
    
    public Response response;
    
    private long time_sent;
    private long time_responded;
}
