package mecono.parceling.types;

import mecono.node.Mailbox;
import mecono.parceling.DestinationParcel;

/**
 *
 * @author jak
 */
public class DataReceiptParcel extends DestinationParcel {

	public DataReceiptParcel(Mailbox mailbox, TransferDirection direction) {
		super(mailbox, direction);
	}

}
