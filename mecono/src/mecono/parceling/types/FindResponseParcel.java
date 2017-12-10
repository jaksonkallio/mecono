package mecono.parceling.types;

import java.util.ArrayList;
import mecono.node.Mailbox;
import mecono.node.Path;
import mecono.parceling.DestinationParcel;

/**
 *
 * @author jak
 */
public class FindResponseParcel extends DestinationParcel {

	public FindResponseParcel(Mailbox mailbox, TransferDirection direction) {
		super(mailbox, direction);
	}

	public void unserializeContent() {

	}

	ArrayList<Path> path_results = new ArrayList<>();
}
