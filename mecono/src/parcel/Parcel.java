package parcel;

public abstract class Parcel {
	public abstract ParcelType getParcelType();
	public String getID(){
		return "ABCD";
	}
	
	public boolean isResponse(Response response){
		return getID().equals(response.getTriggerID());
	}
}
