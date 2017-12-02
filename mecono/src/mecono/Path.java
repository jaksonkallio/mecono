package mecono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author jak
 */
public class Path {

	public Path() {

	}

	public Path(ArrayList<RemoteNode> stops, SelfNode relative_origin) {
		this.stops = stops;
		this.relative_origin = relative_origin;
		
		if(isTrimmed()){
			stops.remove(0);
		}
	}

	public Path(String[] addresses) {

	}

	@Override
	public boolean equals(Object o) {
		Path other = (Path) o;
		boolean is_equal = true;

		for (int i = 0; i < getPathLength(); i++) {
			// If we find just one node out of place, paths are different
			if (!this.getStop(i).equals(other.getStop(i))) {
				is_equal = false;
				break;
			}
		}

		return is_equal;
	}

	/**
	 * Gets a specific stop in the path.
	 *
	 * @param i
	 * @return
	 */
	public Node getStop(int i) {
		if(i <= 0){
                    return getRelativeOrigin();
		}
		
                System.out.println("Getting stop");
		return stops.get(i - 1);
	}
	
	public SelfNode getRelativeOrigin(){
		return relative_origin;
	}

	/**
	 * Returns a list of the stops.
	 *
	 * @return
	 */
	public ArrayList<RemoteNode> getStops() {
		return stops;
	}

	/**
	 * Gets the number of stops in a path.
	 *
	 * @return
	 */
	public int getPathLength() {
		return stops.size();
	}

	/**
	 * Gets a subpath between two stops, inclusive.
	 *
	 * @param start
	 * @param end
	 * @return Path Resulting subpath.
	 */
	public Path getSubpath(int start, int end) {
		ArrayList<RemoteNode> subpath_stops = new ArrayList<>();

		while (start <= end) {
			subpath_stops.add(stops.get(start));
			start++;
		}

		return new Path(subpath_stops, relative_origin);
	}

	/**
	 * More specific use of getSubpath to only get the start of the path up to
	 * the end value.
	 *
	 * @param end
	 * @return
	 */
	public Path getSubpath(int end) {
		return getSubpath(0, end);
	}

	/**
	 * Finds the intermediate path. Path excluding origin and destination.
	 *
	 * @return
	 */
	public Path getIntermediatePath() {
		return this.getSubpath(1, this.getPathLength() - 2);
	}

	public static Path unserialize(String ser_path, SelfNode owner) {
		ArrayList<RemoteNode> path_nodes = new ArrayList<>();
		for (String remote_node_address : ser_path.split("-")) {
			path_nodes.add(owner.getMemoryController().loadRemoteNode(remote_node_address));
		}
		return new Path(path_nodes, owner);
	}

	public int getTotalUses() {
		return getTotalFailures() + getTotalSuccesses();
	}
	
	public int getTotalSuccesses(){
		return successes;
	}
	
	public int getTotalFailures(){
		return failures;
	}

	public boolean isTested(){
		return getTotalSuccesses() > 0;
	}
	
	public double getReliability() {
		double cooperativity = 0;

		if (getTotalUses() > 0) {
			if (successes > 0) {
				// Cooperativity bonus favors nodes that have had a lot of signals sent over them. This gives frequently used paths some slack, and also allows them to improve their reliability over time (up to 100%).
				cooperativity = (successes + (getTotalUses() * relative_origin.path_reliability_rating_bonus)) / getTotalUses();
			} else {
				// Only nodes that have had at least one successful signal sent over them get a cooperativity bonus.
				cooperativity = 0;
			}
		} else {
			// Until we get a good sample size, the cooperativity is constant.
			cooperativity = 0.25;
		}

		// Cooperativity may never be greater than 100%.
		cooperativity = Math.min(cooperativity, 1.00);

		return cooperativity;
	}
	
	private boolean isTrimmed(){
		return stops.get(0).equals(relative_origin);
	}
	
	public int getLastUse() {
		return last_use;
	}

	/**
	 * Regenerates the serialized identifier.
	 */
	public String getIdentifier() {
		// TODO: Use a proper hash of the address items instead.

		String identifier = "";
		int count = 0;

		for (RemoteNode stop : stops) {
			if (count > 0) {
				identifier += ";";
			}
			identifier += count + "-" + stop.getAddress().substring(0, 4);
			count++;
		}

		return identifier;
	}

	private ArrayList<RemoteNode> stops;
	private SelfNode relative_origin;
	private int successes = 0;
	private int failures = 0;
	private int last_use = 0;

	// TODO: These values should probably be in the self node preferences list
	private final double ideality_cooperativity_component = 0.50; // The cooperativity weight for finding ideality rating of paths.
	private final double ideality_online_count_component = 0.40; // The online count weight for finding ideality rating of paths.
	private final double ideality_trusted_count_component = 0.10; // The trusted node count weight for finding ideality rating of paths.
}
