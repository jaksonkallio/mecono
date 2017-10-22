package mecono;

import java.util.ArrayList;
import java.util.Set;

/**
 *
 * @author jak
 */
public class RemoteNode implements Node {

	public RemoteNode(String address) {
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

	public boolean isTrusted() {
		return trusted;
	}

	public boolean isBlacklisted() {
		return blacklisted;
	}

	public Set<RemoteNode> getNeighbors() {
		return neighbors;
	}

	public boolean equals(Object o) {
		Node other = (Node) o;
		return other.getAddress() == this.getAddress();
	}

	public int getNeighborCount() {
		return neighbors.size();
	}
	
	public void updateSuccessfulPing(int ping) {
		if(ping > 60000){
			// If ping is over 60 seconds, ping is shown as "60+ seconds"
			ping = 60000;
		}
		
		this.ping = ping;
		successful_pings++;
		last_ping_time = Protocol.getEpochMinute();
	}

	public boolean isOnline(){
		return ((Protocol.getEpochMinute() - last_ping_time) < offline_successful_ping_threshold);
	}
	
	private String address;
	private String label;
	private int successful_pings;
	private int successful_chunks; // Number of successful chunks sent to this node.
	private int failed_chunks; // Number of chunks that were sent to this node, but didn't receive a receipt within the time allotted.
	private int received_chunks; // Number of chunks received from this node.
	private boolean trusted; // This node can usually be trusted to adhere to good practices in the network.
	private boolean pinned; // We should make sure we always have an online path to this ndoe.
	private boolean blacklisted; // This node shouldn't be used, unless as a last resort.
	private boolean discovered; // If we know any successful paths to this node.
	private int ping;
	private int last_ping_time; // Time of the last ping, in minutes.
	private ArrayList<Path> paths_to;
	private Set<RemoteNode> neighbors; // This node's neighbors.
	
	private static final int offline_successful_ping_threshold = 8; // A successful ping within the last x minutes means the node is online.
	private static final int pinned_ping_interval = 4; // How many minutes between each ping to pinned nodes.
}
