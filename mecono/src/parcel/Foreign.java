package parcel;

import mecono.Self;
import org.json.JSONObject;

public class Foreign extends Parcel {
    public Foreign(Self self){
        super(self);
    }

    @Override
    public JSONObject serialize() {
        return null;
    }
    
    @Override
    public void enqueueSend(){
        getSelf().enqueueSend(this);
    }
}
