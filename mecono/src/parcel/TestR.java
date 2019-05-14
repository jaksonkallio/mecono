package parcel;

import mecono.Self;

public class TestR extends Response {
	public TestR(Self self){
		super(self);
	}
	
	@Override
	public boolean requireOnlineChain(){
		return false;
	}
	
	@Override
	public ParcelType getParcelType(){
		return ParcelType.TESTR;
	}

	@Override
	public ParcelType getTriggerType() {
		return ParcelType.TEST;
	}
}
