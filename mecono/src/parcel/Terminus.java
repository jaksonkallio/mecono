package parcel;

import mecono.Self;
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
    public JSONObject serialize(){
        return null;
    }
}
