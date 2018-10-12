package mecono.parceling.types;

import mecono.parceling.Parcel;
import mecono.parceling.PayloadType;
import mecono.parceling.ResponsePayload;

/**
 *
 * @author jak
 */
public class DataResponsePayload extends ResponsePayload {
	
	@Override
	public PayloadType getParcelType() {
		return PayloadType.DATA_RESPONSE;
	}
}
