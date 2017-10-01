package mecono;

import java.util.Set;

/**
 *
 * @author jak
 */
public class RemoteNode implements Node {
    
	public RemoteNode(String address){
		this.address = address;
	}
	
	public String getAddress() {
		return address;
	}
	
	public String getLabel() {
		return label;
	}
	
	public int countSuccessfulChunks() {
		return successful_chunks;
	}
	
	public int countFailedChunks() {
		return failed_chunks;
	}
	
	public int countReceivedChunks() {
		return received_chunks;
	}
	
	public boolean isCooperative() {
		return cooperative;
	}
	
	public boolean isBlacklisted() {
		return blacklisted;
	}
	
	public Set<RemoteNode> getNeighbors() {
		return neighbors;
	}
	
	public int getNeighborCount() {
		return neighbors.size();
	}
	
    private String address;
	private String label;
	private int successful_chunks; // Number of successful chunks sent to this node.
	private int failed_chunks; // Number of chunks that were sent to this node, but didn't receive a receipt within the time allotted.
	private int received_chunks; // Number of chunks received from this node.
	private boolean cooperative; // This node can usually be trusted to adhere to good practices in the network.
	private boolean blacklisted; // This node shouldn't be used, unless as a last resort.
	private boolean discovered; // If we know any successful paths to this node.
	private Set<RemoteNode> neighbors; // This node's neighbors.
	
}