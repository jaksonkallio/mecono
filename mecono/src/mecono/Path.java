package mecono;

import java.util.ArrayList;

/**
 *
 * @author jak
 */
public class Path {
	
	public Path(ArrayList<RemoteNode> stops) {
		this.stops = stops;
	}
	
	public Path(String[] addresses) {
		for (String address : addresses) {
			stops.add(SelfNode.getRemoteNode(address));
		}
	}
	
	public double getAssuranceLevel() {
		// TODO: measure all nodes in path, and multiple their chances of success, return final assurance level.
		return 0.0;
	}
	
    private ArrayList<RemoteNode> stops;
}