package mecono.parceling.types;

import mecono.node.Mailbox;
import mecono.parceling.DestinationParcel;
import mecono.parceling.ResponseParcel;

/**
 *
 * @author jak
 */
public class DataReceiptParcel extends ResponseParcel {

	public DataReceiptParcel(Mailbox mailbox, TransferDirection direction) {
		super(mailbox, direction);
	}

}
