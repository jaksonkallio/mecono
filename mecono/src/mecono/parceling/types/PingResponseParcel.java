package mecono.parceling.types;

import mecono.node.Mailbox;
import mecono.parceling.DestinationParcel;
import mecono.parceling.ResponseParcel;

/**
 *
 * @author jak
 */
public class PingResponseParcel extends ResponseParcel {

	public PingResponseParcel(Mailbox mailbox, TransferDirection direction) {
		super(mailbox, direction);
	}

}
