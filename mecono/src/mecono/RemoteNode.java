package mecono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
	
	public boolean isAdversarial() {
		return adversarial;
	}

	public void learnPath(Path path) {
		if (indexer.getNeighbors().contains((RemoteNode) path.getStop(0)) && path.getStop(path.getPathLength() - 1).equals(this)) {
			// If the first stop is the self node, and the last stop is this node, then store
			if (!isPathKnown(path)) {
				// If this path isn't already known
				paths_to.add(path);
			}
		}
	}

	public ArrayList<RemoteNode> getNeighbors() {
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
		if (ping > 60000) {
			// If ping is over 60 seconds, ping is shown as "60+ seconds"
			ping = 60000;
		}

		this.ping = ping;
		successful_signal_dest[0]++;
		last_ping_time = Protocol.getEpochMinute();
	}

	public boolean isOnline() {
		return ((Protocol.getEpochMinute() - last_ping_time) < indexer.offline_successful_ping_threshold);
	}

	public boolean isReady() {
		// Node is ready when the ideal path exists, and the node is online/offline as per user settings.
		return !((!indexer.ready_when_offline && !isOnline()) || getIdealPath() == null);
	}

	public int countPathsTo() {
		return paths_to.size();
	}

	public int getLastUse() {
		return last_use;
	}

	public int getPing() {
		return ping;
	}

	public int getTotalUses() {
		return Math.abs(successes + strikes);
	}

	/**
	 * Mark this node as being online at the current time.
	 */
	public void markOnline() {
		last_online = Protocol.getEpochMinute();
	}

	public Path getIdealPath() {
		if(indexer.isNeighbor(this)){
			ArrayList<RemoteNode> stops = new ArrayList<>();
			stops.add(this);
			Path direct_path = new Path(stops, indexer);
			learnPath(direct_path);
		}
		
		sortPaths();

		if (countPathsTo() > 0) {
			// Return top path
			return paths_to.get(0);
		} else {
			return null;
		}
	}

	private boolean isPathKnown(Path target) {
		for (Path path : paths_to) {
			if (path.equals(target)) {
				return true;
			}
		}

		return false;
	}

	private void sortPaths() {
		Collections.sort(paths_to, new Comparator<Path>() {
			@Override
			public int compare(Path path2, Path path1) {

				return (int) (1000 * (path2.getReliability() - path1.getReliability()));
			}
		});
	}

	private String address;
	private String label;
	private int successes = 0; // Successful signals sent across this node.
	private int strikes = 0; // Unsuccessful signals sent across this node.
	private int[] successful_signal_dest = {0, 0, 0}; // Signals where valid response received.
	private boolean adversarial; // Flagged as an adversarial node.
	private int ping;
	private int last_ping_time; // Time of the last ping, in minutes.
	private int last_online; // Last time this node has successfully been used to send a signal.
	private int last_use; // Gets the last time this node was used, despite if the signal succeeded or failed.
	private ArrayList<Path> paths_to = new ArrayList<>();
	private ArrayList<RemoteNode> neighbors; // This node's neighbors, used only for community members.
	private SelfNode indexer;
	private int max_paths = 100; // Only keep the best x paths.
}
