package parcel;

import mecono.Self;

public class Data extends Trigger {
	public Data(Self self){
		super(self);
	}
	
	@Override
	public ParcelType getParcelType(){
		return ParcelType.DATA;
	}
	
	public void setMessage(String message){
		this.message = message;
	}
	
	private String message;
}
