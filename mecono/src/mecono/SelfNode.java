package mecono;

import java.util.Set;

/**
 *
 * @author jak
 */
public class SelfNode implements Node {

	public SelfNode() {
		mailbox = new Mailbox(this);
	}

	public String getAddress() {
		return address;
	}

	public String getLabel() {
		return label;
	}

	public static RemoteNode getRemoteNode(String address) {
		// Check if node is loaded into memory
		for (RemoteNode node : nodes_memory) {
			if (node.getAddress() == address) {
				return node;
			}
		}

		// TODO: Check if node is in saved in the database
		// Return a new blank node with address if none found anywhere else
		RemoteNode new_node = new RemoteNode(address);
		nodes_memory.add(new_node);

		return new_node;
	}

	private boolean sendNuggetStream(NuggetStream stream) {
		return true;
	}

	private String address;
	private String label;
	private Mailbox mailbox;
	private boolean optimize_foreign_nuggetstream_paths = true; // If we receive a NuggetStream and know a better path than what is given, use our own path.
	private double optimize_foreign_nuggetstream_threshold = 0.95; // The threshold before we optimize their path.
	private boolean request_no_foreign_optimization = false; // We can ask nodes that receive our nugget streams to not optimize our streams.

	private static Set<RemoteNode> nodes_memory;
}
