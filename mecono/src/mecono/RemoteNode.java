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
		successful_signal_dest[0]++;
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
	
	public Path getIdealPath(){
		return paths_to.get(0);
	}
	
	public int getTotalUses(){
		return Math.abs(successes + strikes);
	}
	
	/**
	 * Cooperativity is a measure of cooperation a node shows with the self node. It only applies to non-destination nodes in the path.
	 * @return 
	 */
	public double getCooperativity(){
		double cooperativity = 0;
		
		if(getTotalUses() > 0 && getTotalUses() >= indexer.cooperativity_minimum_sample_size){
			if(successes > 0){
				// Cooperativity bonus favors nodes that have had a lot of signals sent over them. This gives frequently used nodes some slack, and also allows them to improve their cooperativity raiting over time (up to 100%).
				cooperativity = (successes+(getTotalUses()*indexer.cooperativity_rating_bonus)) / getTotalUses();
			}else{
				// Only nodes that have had at least one successful signal sent over them get a cooperativity bonus.
				cooperativity = 0;
			}
		}else {
			// Until we get a good sample size, the cooperativity is constant.
			cooperativity = 0.25;
		}
		
		// Cooperativity may never be greater than 100%.
		cooperativity = Math.min(cooperativity, 1.00);
		
		return cooperativity;
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
	private int successes = 0; // Successful signals sent across this node.
	private int strikes = 0; // Unsuccessful signals sent across this node.
	private int[] successful_signal_dest = {0, 0, 0}; // Signals where valid response received.
	private boolean trusted; // This node can usually be trusted to adhere to good practices in the network.
	private boolean pinned; // We should make sure we always have an online path to this node.
	private boolean blacklisted; // This node shouldn't be used, unless as a last resort.
	private int ping;
	private int last_ping_time; // Time of the last ping, in minutes.
	private int last_use; // Gets the last time this node was used, despite if the signal succeeded or failed.
	private ArrayList<Path> paths_to;
	private Set<RemoteNode> neighbors; // This node's neighbors.
	private SelfNode indexer;
	private int max_paths = 100; // Only keep the best x paths.
}
