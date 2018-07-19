package mecono.node;

import mecono.protocol.Protocol;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import mecono.parceling.BadPathException;

/**
 *
 * @author jak
 */
public class RemoteNode implements Node {

	public RemoteNode(String address, SelfNode indexer) {
		this.address = address;
		this.indexer = indexer;
	}

	@Override
	public String getAddress() {
		return address;
	}

	@Override
	public String getLabel() {
		return label;
	}

	public boolean isAdversarial() {
		return adversarial;
	}

	public void logResult(boolean endpoint) {
		if (endpoint) {
			endpoint_uses++;
		} else {
			instrumental_uses++;
		}
	}

	public void learnPath(Path path, RemoteNode learned_from) throws BadPathException {
		if (indexer.isNeighbor((RemoteNode) path.getStop(1)) && path.getStop(path.getPathLength() - 1).equals(this)) {
			// If the first stop is the self node, and the last stop is this node, then store
			if (!isPathKnown(path)) {
				// If this path isn't already known
				PathStats path_stats = new PathStats(path, indexer, learned_from);
				paths_to.add(path_stats);
			}
		}
	}

	public ArrayList<RemoteNode> getNeighbors() {
		return neighbors;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Node) {
			Node other = (Node) o;

			return other.getAddress().equals(this.getAddress());
		}

		return false;
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
		last_ping_time = Protocol.getEpochMinute();
	}
	
	public long getLastPinged(){
		if(getIdealPath() == null){
			return 0;
		}
		
		return getIdealPath().getLastUse();
	}

	public boolean isOnline() {
		return Protocol.elapsedMillis(getLastPinged()) < ONLINE_THRESHOLD;
	}

	public boolean isReady() {
		// Node is ready when the ideal path exists, and the node is online/offline as per user settings.
		return !((!indexer.ready_when_offline && !isOnline()) || getIdealPath() == null);
	}

	public int countPathsTo() {
		return paths_to.size();
	}

	public ArrayList<PathStats> getPathsTo() {
		return paths_to;
	}

	public int getLastOnline() {
		return last_ping_time;
	}

	public int getPing() {
		return ping;
	}

	public PathStats getIdealPath() {
		if (indexer.isNeighbor(this)) {
			ArrayList<Node> stops = new ArrayList<>();
			stops.add(indexer);
			stops.add(this);
			Path direct_path = new Path(stops);
			try {
				learnPath(direct_path, null);
			} catch (BadPathException ex) {
				indexer.nodeLog(2, "Could not learn neighborship", ex.getMessage());
			}
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
		for (PathStats path : paths_to) {
			if (path.getPath().equals(target)) {
				//indexer.nodeLog(2, "Path already known", target.toString());
				return true;
			}
		}

		return false;
	}

	private void sortPaths() {
		Collections.sort(paths_to, new Comparator<PathStats>() {
			@Override
			public int compare(PathStats path2, PathStats path1) {

				return (int) (1000 * (path2.reliability() - path1.reliability()));
			}
		});
	}
	
	public final static long ONLINE_THRESHOLD = 30000;

	private final String address;
	private String label;
	private int instrumental_uses = 0; // Successful signals sent across this node
	private int endpoint_uses = 0; // Successful signals sent to this node
	private boolean adversarial; // Flagged as an adversarial node.
	private int ping;
	private int last_ping_time; // Time of the last ping, in minutes.
	private ArrayList<PathStats> paths_to = new ArrayList<>();
	private ArrayList<RemoteNode> neighbors; // This node's neighbors, used only for community members.
	private SelfNode indexer;
	private int max_paths = 100; // Only keep the best x paths.
}
