package parcel;

import mecono.Self;
import node.Node;
import org.json.JSONObject;

public abstract class Terminus extends Parcel {
    public Terminus(Self self){
        super(self);
    }
    
    @Override
    public String toString(){
        StringBuilder str = new StringBuilder();
        
        str.append("#");
        str.append(getID());
        str.append(" is ");
        str.append(getParcelType().name());
        str.append(" to ");
        str.append(getDestination().getTrimmedAddress());
        
        if(getChain() != null){
            str.append(" via ");
            str.append(getChain().toString());
        }
        
        return str.toString();
    }
    
    public abstract ParcelType getParcelType();
    
    public String getID(){
		return id;
	}
    
    public boolean isSent(){
        return getTimeSent() != 0;
    }
    
    public long getTimeSent(){
        return time_sent;
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
    
    public void logSend(){
        time_sent = Self.time();
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
    
    public boolean requireOnlineChain(){
        return true;
    }
    
    private long time_sent;
    private long time_queued;
	private Node destination;
	private String id;
}
