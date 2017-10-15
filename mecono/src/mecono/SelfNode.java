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

	public void nodeLog(int importance, String message){
		String[] importance_levels = {"INFO", "NOTE", "WARN", "CRIT"};
		
		if(importance <= (importance_levels.length - 1)){
			System.out.println("["+importance_levels[importance]+"] "+message);
		}
	}
	
	private String address;
	private String label;
	private Mailbox mailbox;
	private boolean request_no_foreign_optimization = false; // We can ask nodes that receive our nugget streams to not optimize our streams.
	private int nstream_build_expiry = 30; // Time, in minutes, where an incomplete nstream will be deleted along with contained nuggets.

	private static Set<RemoteNode> nodes_memory;
}
