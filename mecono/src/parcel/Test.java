package parcel;

import mecono.Self;
import org.json.JSONObject;

public class Test extends Trigger {
	public Test(Self self){
		super(self);
	}
	
	public Parcel deserialize(JSONObject parcel_json){
		super.deserialize(parcel_json);
	}
	
	@Override
	public ParcelType getParcelType(){
		return ParcelType.TEST;
	}
}
