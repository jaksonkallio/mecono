package mecono;

import java.util.ArrayList;
import java.util.Set;

/**
 *
 * @author jak
 */
public class SelfNode implements Node {

	public SelfNode(String label) {
		this.label = label;
		mailbox = new Mailbox(this);
		nodeLog(0, "SelfNode \""+getLabel()+"\" started.");
	}
	
	public SelfNode() {
		this("Unnamed");
	}
	
	public void generateNewAddress(){
		address = Protocol.generateAddress();
		nodeLog(0, "SelfNode \""+getLabel()+"\" now uses address \""+getAddress()+"\".");
	}

	@Override
	public String getAddress() {
		return address;
	}

	@Override
	public String getLabel() {
		return label;
	}

	public static RemoteNode getRemoteNode(String address) {
		// Check if node is loaded into memory
		for (RemoteNode node : nodes_memory) {
			if (node.getAddress().equals(address)) {
				return node;
			}
		}

		// TODO: Check if node is in saved in the database
		// Return a new blank node with address if none found anywhere else
		RemoteNode new_node = new RemoteNode(address);
		nodes_memory.add(new_node);

		return new_node;
	}

	public void receiveCompleteNStream(NuggetStream nstream) {
		nodeLog(1, "Data received via mecono network: "+nstream.buildMessage());
	}
	
	public void nodeLog(int importance, String message){
		String[] importance_levels = {"INFO", "NOTE", "WARN", "CRIT"};
		
		if(importance <= (importance_levels.length - 1)){
			System.out.println("["+label.substring(0,3)+"]["+importance_levels[importance]+"] "+message);
		}
	}
	
	public Mailbox getMailbox(){
		return mailbox;
	}
	
	private boolean sendNuggetStream(NuggetStream stream) {
		return true;
	}
	
	private String address;
	private String label;
	protected final Mailbox mailbox;
	private boolean request_no_foreign_optimization = false; // We can ask nodes that receive our nugget streams to not optimize our streams.
	private int nstream_build_expiry = 30; // Time, in minutes, where an incomplete nstream will be deleted along with contained nuggets.

	private static ArrayList<RemoteNode> nodes_memory = new ArrayList<>();
}
