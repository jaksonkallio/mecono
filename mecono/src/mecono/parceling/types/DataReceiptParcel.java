package mecono.parceling.types;

import mecono.node.Mailbox;
import mecono.parceling.ParcelType;
import mecono.parceling.ResponseParcel;

/**
 *
 * @author jak
 */
public class DataReceiptParcel extends ResponseParcel {

	public DataReceiptParcel(Mailbox mailbox) {
		super(mailbox);
	}

	@Override
	public ParcelType getParcelType() {
		return ParcelType.DATA_RECEIPT;
	}
}
