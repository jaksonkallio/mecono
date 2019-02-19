package parcel;

public class TestR extends Response {
	@Override
	public ParcelType getParcelType(){
		return ParcelType.TESTR;
	}

	@Override
	public ParcelType getTriggerType() {
		return ParcelType.TEST;
	}
}
