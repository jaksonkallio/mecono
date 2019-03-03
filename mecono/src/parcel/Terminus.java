package parcel;

import mecono.Self;
import mecono.Util;
import org.json.JSONObject;

public abstract class Terminus extends Parcel {
    public Terminus(Self self){
        super(self);
    }
    
    public abstract ParcelType getParcelType();
    
    public String getID(){
		return "ABCD";
	}
    
    public boolean ready(){
        return true;
    }
    
    public void logSend(){};
    
    @Override
    public JSONObject serialize(){
        return null;
    }
    
    @Override
    public void enqueueSend(){
        getSelf().enqueueSend(this);
        logQueue();
    }
    
    public boolean isQueued(){
        return time_queued != 0;
    }
    
    public void logQueue(){
        time_queued = Util.time();
    }
    
    private long time_queued;
}
