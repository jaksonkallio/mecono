package parcel;

import mecono.Self;

public class Test extends Trigger {
	public Test(Self self){
		super(self);
	}
	
	@Override
	public ParcelType getParcelType(){
		return ParcelType.TEST;
	}
}
