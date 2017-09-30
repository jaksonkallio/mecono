package mecono;

import java.util.Set;

/**
 *
 * @author jak
 */
public class SelfNode implements Node {
	
	public String getAddress() {
		return address;
	}
	
	public String getLabel() {
		return label;
	}
	
	public static RemoteNode getRemoteNode(String address) {
		// Check if node is loaded into memory
		for (RemoteNode node : nodes_memory) {
			if(node.getAddress() == address){
				return node;
			}
		}
		
		// TODO: Check if node is in saved in the database
		
		// Return a new blank node with address if none found anywhere else
		RemoteNode new_node = new RemoteNode(address);
		nodes_memory.add(new_node);
		
		return new_node;
	}
	
    private String address;
	private String label;
	private static Set<RemoteNode> nodes_memory;
}