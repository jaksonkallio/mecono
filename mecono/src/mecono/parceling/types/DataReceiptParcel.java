package mecono.parceling.types;

import mecono.parceling.Parcel;
import mecono.parceling.ParcelType;
import mecono.parceling.ResponsePayload;

/**
 *
 * @author jak
 */
public class DataReceiptParcel extends ResponsePayload {
	
	@Override
	public ParcelType getParcelType() {
		return ParcelType.DATA_RECEIPT;
	}
}
