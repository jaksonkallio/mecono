package mecono;

import java.util.ArrayList;
import java.util.Set;

/**
 *
 * @author jak
 */
public class RemoteNode implements Node {

	public RemoteNode(String address, SelfNode indexer) {
		this.address = address;
		this.indexer = indexer;
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
	
	public boolean isPinned() {
		// All trusted nodes are inherently pinned as well.
		return (isTrusted() || pinned);
	}

	public boolean isBlacklisted() {
		return blacklisted;
	}
	
	public void learnPath(Path path){
		if(path.getStop(0).equals(indexer) && path.getStop(path.getPathLength() - 1).equals(this)){
			// If the first stop is the self node, and the last stop is this node, then store
			if(!isPathKnown(path)){
				// If this path isn't already known
				paths_to.add(path);
			}
		}
	}

	public Set<RemoteNode> getNeighbors() {
		return neighbors;
	}

	@Override
	public boolean equals(Object o) {
		Node other = (Node) o;
		return other.getAddress().equals(this.getAddress());
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
		return ((Protocol.getEpochMinute() - last_ping_time) < indexer.offline_successful_ping_threshold);
	}
	
	public boolean isReady(){
		if(!indexer.ready_when_offline && !isOnline()){
			return false;
		}
		
		if(indexer.ready_when_offline){
			if(getPathTo() != null){
				
			}
		}
		
		return true;
	}
	
	public int countPathsTo(){
		return paths_to.size();
	}
	
	public int getLastUse(){
		return last_use;
	}
	
	public int getPing(){
		return ping;
	}
	
	private Path getPathTo(){
		if(countPathsTo() > 0){
			// Return top path
			return paths_to.get(0);
		}else{
			return null;
		}
	}
	
	private boolean isPathKnown(Path target){
		for(Path path : paths_to){
			if(path.equals(target)){
				return true;
			}
		}
		
		return false;
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
	private int ping;
	private int last_ping_time; // Time of the last ping, in minutes.
	private int last_use; // Gets the last time this node was used, despite if the signal succeeded or failed.
	private ArrayList<Path> paths_to;
	private Set<RemoteNode> neighbors; // This node's neighbors.
	private SelfNode indexer;
	private int max_paths = 100; // Only keep the best x paths.
}
