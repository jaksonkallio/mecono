package parcel;

import mecono.Self;
import node.BadSerializationException;
import org.json.JSONObject;

public class Test extends Trigger {
	public Test(Self self){
		super(self);
	}
	
    @Override
	public void deserialize(JSONObject parcel_json) throws BadSerializationException {
		super.deserialize(parcel_json);
	}
	
	@Override
	public ParcelType getParcelType(){
		return ParcelType.TEST;
	}
}
