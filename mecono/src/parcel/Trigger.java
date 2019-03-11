package parcel;

import mecono.Self;

public abstract class Trigger extends Terminus {
	public Trigger(Self self){
		super(self);
		genID();
	}
    
	public final void genID(){
		setID(""+getSelf().genRandomString(PARCEL_ID_LEN));
	}
	
    public void setResponse(Response response){
        this.response = response;
        time_responded = Self.time();
    }
    
    public void logResponse(){
        getChain().logSuccess(getTimeResponded() - getTimeSent());
    }

    @Override
    public void logSend(){
        getChain().logUse();
        time_sent = Self.time();
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
	
	public static final short PARCEL_ID_LEN = 5;
	
    public Response response;
    
    private long time_sent;
    private long time_responded;
}
