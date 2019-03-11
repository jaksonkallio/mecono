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
	
	@Override
	public boolean isDuplicate(Parcel o){
		if(o instanceof Data){
			Data other = (Data) o;
			
			if(this.getMessage().equals(other.getMessage())){
				return super.isDuplicate(o);
			}
		}
		
		return false;
	}
	
	public void setMessage(String message){
		this.message = message;
	}
	
	public String getMessage(){
		return message;
	}
	
	private String message;
}
