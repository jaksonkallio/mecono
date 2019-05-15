package parcel;

import mecono.Self;
import node.BadProtocolException;
import node.BadSerializationException;
import org.json.JSONObject;

public class Foreign extends Parcel {
    public Foreign(Self self){
        super(self);
    }
	
	@Override
	public void deserialize(JSONObject parcel_json) throws BadSerializationException {
		super.deserialize(parcel_json);
		
		// TODO: Encrypted content string
		setEncryptedContent(parcel_json.getJSONObject("content"));
	}
	
	@Override
	public void receive() throws BadProtocolException {
		super.receive();
		enqueueSend();
	}

    @Override
    public JSONObject serialize() {
		JSONObject parcel_json = super.serialize();
		
		parcel_json.put("content", getEncryptedContent());
    
		return parcel_json;
	}
    
    @Override
    public void enqueueSend(){
        getSelf().enqueueSend(this);
    }
	
	public void setEncryptedContent(JSONObject encrypted_content){
		this.encrypted_content = encrypted_content;
	}
	
	public JSONObject getEncryptedContent(){
		return encrypted_content;
	}
	
	private JSONObject encrypted_content;
}
