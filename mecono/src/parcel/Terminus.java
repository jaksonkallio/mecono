package parcel;

import mecono.Self;
import mecono.Util;
import node.Node;
import org.json.JSONObject;

public abstract class Terminus extends Parcel {
    public Terminus(Self self){
        super(self);
    }
    
    public abstract ParcelType getParcelType();
    
    public String getID(){
		return "ABCD";
	}
	
	@Override
	public boolean isDuplicate(Parcel o){
		if(o instanceof Terminus){
			Terminus other = (Terminus) o;
			
			if(getParcelType() == other.getParcelType()){
				return true;
			}
		}
		
		return false;
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
	
	public Node getDestination(){
		if(getChain() == null || getChain().empty()){
			return destination;
		}
		
		return getChain().getDestinationNode();
	}
    
    private long time_queued;
	private Node destination;
}
