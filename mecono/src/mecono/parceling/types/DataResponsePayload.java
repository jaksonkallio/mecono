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
	public boolean equals(Object o) {
		if (o instanceof DataResponsePayload) {
			// All DataResponsePayloads are identical
			return super.equals(o);
		}

		return false;
	}
	
	@Override
	public PayloadType getPayloadType() {
		return PayloadType.DATA_RESPONSE;
	}
}
