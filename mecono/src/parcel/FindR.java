package parcel;

import mecono.Self;

public class FindR extends Response {
	public FindR(Self self){
		super(self);
	}
	
	@Override
	public ParcelType getParcelType(){
		return ParcelType.FINDR;
	}

	@Override
	public ParcelType getTriggerType() {
		return ParcelType.FIND;
	}
}
