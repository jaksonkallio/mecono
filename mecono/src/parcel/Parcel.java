package parcel;

import mecono.ErrorLevel;
import mecono.MeconoSerializable;
import mecono.Self;
import node.BadProtocolException;
import node.BadSerializationException;
import node.Chain;
import org.json.JSONObject;

public abstract class Parcel implements MeconoSerializable {
	public Parcel(Self self){
		this.self = self;
	}
	
	@Override
	public void deserialize(JSONObject parcel_json) throws BadSerializationException {
		if(parcel_json.has("chain")){
			Chain chain = new Chain(getSelf());
			chain.deserialize(parcel_json.getJSONObject("chain"));
			setChain(chain);
		}
	}
	
	@Override
	public JSONObject serialize(){
		JSONObject parcel_json = new JSONObject();
		JSONObject chain_json = getChain().serialize();
		
		parcel_json.put("chain", chain_json);
		
		return parcel_json;
	}
	
	public void setChain(Chain chain){
		this.chain = chain;
	}
	
	public Chain getChain(){
		return chain;
	}
	
	public void receive() throws BadProtocolException {
		getSelf().log(ErrorLevel.OK, "Received", toString());
		getSelf().learn(getChain());
	}
    
    public Self getSelf(){
        return self;
    }
	
	public boolean isDuplicate(Parcel other){
		return true;
	}

    public abstract void enqueueSend();
    
    public static Parcel constructParcelType(String type_str, Self self) throws BadSerializationException {
        switch(type_str){
            case "DATA":
                return new Data(self);
            case "DATAR":
                return new DataR(self);
            case "FIND":
                return new Find(self);
            case "FINDR":
                return new FindR(self);
            case "TEST":
                return new Test(self);
            case "TESTR":
                return new TestR(self);
        }
        
        throw new BadSerializationException("Unrecognized type string \"" + type_str + "\"");
    }
	
    public static Parcel constructParcelType(JSONObject json, Self self) throws BadSerializationException {
        if(!json.has("chain")){
			throw new BadSerializationException("Lacking a chain");
		}
        
        Chain chain = new Chain(self);
		chain.deserialize(json.getJSONObject("chain"));
        
        if(chain.getDestinationNode().equals(self.getSelfNode())){
            if(!json.has("content")){
                throw new BadSerializationException("Terminus parcel missing content");
            }
			
            // TODO: decryption routine here
            
            JSONObject content = json.getJSONObject("content");
            
            if(!content.has("type")){
                throw new BadSerializationException("Terminus parcel missing type");
            }
            
            return constructParcelType(content.getString("type"), self);
        }else{
            return new Foreign(self);
        }
    }
    
	private final Self self;
	private Chain chain;
}
