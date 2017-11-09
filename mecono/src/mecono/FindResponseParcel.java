package mecono;

import java.util.ArrayList;

/**
 *
 * @author jak
 */
public class FindResponseParcel extends DestinationParcel {

	public FindResponseParcel(Mailbox mailbox) {
		super(mailbox);
	}

	public void unserializeContent() {

	}

	ArrayList<Path> path_results = new ArrayList<>();
}
