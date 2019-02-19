package parcel;

public class FindR extends Response {
	@Override
	public ParcelType getParcelType(){
		return ParcelType.FINDR;
	}

	@Override
	public ParcelType getTriggerType() {
		return ParcelType.FIND;
	}
}
