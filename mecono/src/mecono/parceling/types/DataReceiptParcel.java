package mecono.parceling.types;

import mecono.parceling.Parcel;
import mecono.parceling.ParcelType;
import mecono.parceling.ResponsePayload;

/**
 *
 * @author jak
 */
public class DataReceiptParcel extends ResponsePayload {

	public DataReceiptParcel(Parcel parcel) {
		super(parcel);
	}

	@Override
	public ParcelType getParcelType() {
		return ParcelType.DATA_RECEIPT;
	}
}
