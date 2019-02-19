package parcel;

import mecono.Self;

public class Find extends Trigger {
	public Find(Self self){
		super(self);
	}
	
	@Override
	public ParcelType getParcelType(){
		return ParcelType.FIND;
	}
}
