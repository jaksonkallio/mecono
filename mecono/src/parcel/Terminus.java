package parcel;

import mecono.Self;
import node.BadSerializationException;
import node.MNode;
import org.json.JSONObject;
import static parcel.Trigger.PARCEL_ID_LEN;

public abstract class Terminus extends Parcel {
    public Terminus(Self self){
        super(self);
		genID();
    }
	
	public final void genID() {
		setID("" + getSelf().genRandomString(PARCEL_ID_LEN));
	}
    
    @Override
    public String toString(){
        StringBuilder str = new StringBuilder();
        
		str.append("#");
		str.append(getID());
			
		if(this instanceof Response){
			str.append(" (Trigger #");
			str.append(((Response) this).getTriggerID());
			str.append(")");
		}
        
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
		getDestination().incSendCount();
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
	
	public MNode getDestination(){
		if(getChain() == null || getChain().empty()){
			return destination;
		}
		
		return getChain().getDestinationNode();
	}
	
	public void setDestination(MNode destination){
		this.destination = destination;
	}
	
	@Override
	public JSONObject serialize(){
		JSONObject parcel_json = super.serialize();
		JSONObject content_json = new JSONObject();
	
		content_json.put("type", getParcelType().name());
		content_json.put("id", getID());
		
		parcel_json.put("content", content_json);
		
		return parcel_json;
	}
	
	@Override
	public void deserialize(JSONObject parcel_json) throws BadSerializationException {
		super.deserialize(parcel_json);
		
		JSONObject content_json = parcel_json.getJSONObject("content");
		setID(content_json.getString("id"));
	}
    
    public boolean requireOnlineChain(){
        return true;
    }
    
    private long time_sent;
    private long time_queued;
	private MNode destination;
	private String id;
}
