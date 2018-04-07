package mecono.node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import mecono.parceling.BadPathException;
import org.json.JSONArray;

/**
 *
 * @author jak
 */
public class Path {

    public Path() {

    }

    public Path(ArrayList<Node> stops) {
        this.stops = stops;
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
        return stops.get(i);
    }

    /**
     * Returns a list of the stops.
     *
     * @return
     */
    public ArrayList<Node> getStops() {
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
    public Path getSubpath(int start, int end) throws BadPathException {
        ArrayList<Node> subpath_stops = new ArrayList<>();

		if(start < 0){
			throw new BadPathException("Subpath starting below zero");
		}
		
		if(end >= getPathLength()){
			throw new BadPathException("Subpath ending after last node");
		}
		
        while (start <= end) {
            subpath_stops.add(stops.get(start));
            start++;
        }

        return new Path(subpath_stops);
    }

	public void reverse(){
		Collections.reverse(stops);
	}
	
    /**
     * More specific use of getSubpath to only get the start of the path up to
     * the end value.
     *
     * @param end
     * @return
     */
    public Path getSubpath(int end) {
		try {
			return getSubpath(0, end);
		} catch(BadPathException ex) {
			// TODO: Should never happen
		}
		
		return null;
    }

    /**
     * Finds the intermediate path. Path excluding origin and destination.
     *
     * @return
     */
    public Path getIntermediatePath() {
		try {
			return this.getSubpath(1, this.getPathLength() - 2);
		} catch(BadPathException ex) {
			// TODO: Should never happen
		}
		
		return null;
    }

    public static Path unserialize(String ser_path, SelfNode owner) {
        ArrayList<Node> path_nodes = new ArrayList<>();
        for (String remote_node_address : ser_path.split("-")) {
            path_nodes.add(owner.getMemoryController().loadRemoteNode(remote_node_address));
        }
        return new Path(path_nodes);
    }

    /**
     * Regenerates the serialized identifier.
     *
     * @return
     */
    public String getIdentifier() {
        // TODO: Use a proper hash of the address items instead.

        String identifier = "";
        int count = 0;

        for (Node stop : stops) {
            if (count > 0) {
                identifier += ";";
            }
            identifier += count + "-" + stop.getAddress().substring(0, 4);
            count++;
        }

        return identifier;
    }
	
	@Override
	public String toString(){
		StringBuilder construct = new StringBuilder();
		ArrayList<Node> stops = getStops();
		boolean first_added = false;
		
		construct.append("[");
		for(Node stop : stops){
			if(first_added){
				construct.append(" -> ");
			}
			
			construct.append(stop.getAddress());
			first_added = true;
		}
		construct.append("]");
		
		return construct.toString();
	}
	
	public static ArrayList<Path> convertToRawPaths(ArrayList<PathStats> outwards_paths){
		ArrayList<Path> paths_raw = new ArrayList<>();
		for(PathStats outward_path : outwards_paths){
			paths_raw.add(outward_path.getPath());
		}
		return paths_raw;
	}

    private ArrayList<Node> stops;
}
