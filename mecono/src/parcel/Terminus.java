package parcel;

import mecono.Self;
import node.Node;
import org.json.JSONObject;

public abstract class Terminus extends Parcel {
    public Terminus(Self self){
        super(self);
    }
    
    public abstract ParcelType getParcelType();
    
    public String getID(){
		return id;
	}
	
	public void setID(String id){
		this.id = id;
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
        return getTimeQueued() != 0;
    }
    
    public long getTimeQueued(){
        return time_queued;
    }
    
    public void logQueue(){
        time_queued = Self.time();
    }
	
	public Node getDestination(){
		if(getChain() == null || getChain().empty()){
			return destination;
		}
		
		return getChain().getDestinationNode();
	}
	
	public void setDestination(Node destination){
		this.destination = destination;
	}
    
    private long time_queued;
	private Node destination;
	private String id;
}
