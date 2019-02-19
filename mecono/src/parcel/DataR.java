package parcel;

import mecono.Self;

public class DataR extends Response {
	public DataR(Self self){
		super(self);
	}
	
	@Override
	public ParcelType getParcelType(){
		return ParcelType.DATAR;
	}

	@Override
	public ParcelType getTriggerType() {
		return ParcelType.DATA;
	}
}
