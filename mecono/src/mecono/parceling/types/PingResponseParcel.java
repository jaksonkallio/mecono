package mecono.parceling.types;

import mecono.node.Mailbox;
import mecono.parceling.DestinationParcel;

/**
 *
 * @author jak
 */
public class PingResponseParcel extends DestinationParcel {

	public PingResponseParcel(Mailbox mailbox, TransferDirection direction) {
		super(mailbox, direction);
	}

}
