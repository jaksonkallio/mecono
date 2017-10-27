package mecono;

import java.util.ArrayList;

/**
 *
 * @author jak
 */
public class Path {
	
	public Path(){
		
	}
	
	public Path(ArrayList<RemoteNode> stops) {
		this.stops = stops;
	}
	
	public Path(String[] addresses) {
		
	}
	
	@Override
	public boolean equals(Object o){
		Path other = (Path) o;
		boolean is_equal = true;
		
		for(int i = 0; i < getPathLength(); i++){
			// If we find just one node out of place, paths are different
			if(!this.getStop(i).equals(other.getStop(i))){
				is_equal = false;
				break;
			}
		}
		
		return is_equal;
	}
	
	/**
	 * Gets a specific stop in the path.
	 * @param i
	 * @return 
	 */
	public Node getStop(int i) {
		return stops.get(i);
	}
	
	/**
	 * Returns a list of the stops.
	 * @return 
	 */
	public ArrayList<RemoteNode> getStops(){
		return stops;
	}
	
	/**
	 * Gets the number of stops in a path.
	 * @return 
	 */
	public int getPathLength() {
		return stops.size();
	}
	
	/**
	 * Gets the serialized identifier
	 * @return 
	 */
	public String getIdentifier(){
		regenerateIdentifier();
		return identifier;
	}
	
	/**
	 * Gets a subpath between two stops, inclusive.
	 * @param start
	 * @param end
	 * @return Path Resulting subpath.
	 */
	public Path getSubpath(int start, int end){
		ArrayList<RemoteNode> subpath_stops = new ArrayList<>();
		
		while(start <= end){
			subpath_stops.add(stops.get(start));
			start++;
		}
		
		return new Path(subpath_stops);
	}
	
	/**
	 * More specific use of getSubpath to only get the start of the path up to the end value.
	 * @param end
	 * @return 
	 */
	public Path getSubpath(int end){
		return getSubpath(0, end);
	}
	
	/**
	 * Finds the intermediate path. Path excluding origin and destination.
	 * @return 
	 */
	public Path getIntermediatePath(){
		return this.getSubpath(1, this.getPathLength() - 2);
	}
	
	/**
	 * Gets the ideality rating.
	 * @return 
	 */
	public double getIdealityRating(){
		double cooperativity = getAverageCooperativity();
		int trusted_nodes = 0;
		int online_nodes = 0;
		int path_length = 0;
		
		if((ideality_cooperativity_component + ideality_online_count_component + ideality_trusted_count_component) != 1){
			// These must add up to 1 to be proper weights.
			return 0;
		}
		
		for(RemoteNode stop : stops){
			path_length++;
			
			if(stop.isTrusted()){
				trusted_nodes++;
			}
			if(stop.isOnline()){
				online_nodes++;
			}
		}
		
		return ((ideality_cooperativity_component * cooperativity) + (ideality_online_count_component * (online_nodes / path_length)) + (ideality_trusted_count_component * (trusted_nodes / path_length)));
	}
	
	/**
	 * Gets the average cooperativity of the nodes in the path.
	 * @return 
	 */
	public double getAverageCooperativity(){
		double total_cooperativity = 0.0;
		int count = 0;
		
		for(RemoteNode node : getIntermediatePath().getStops()){
			total_cooperativity += node.getCooperativity();
			count++;
		}
		
		if(count == 0){
			return 0.0;
		}else{
			return total_cooperativity / count;
		}
	}
	
	/**
	 * Regenerates the serialized identifier.
	 */
	private void regenerateIdentifier(){
		// TODO: Use a proper hash of the address items instead.
		
		String new_identifier = "";
		int count = 0;
		
		for(RemoteNode stop : stops){
			if(count > 0){
				new_identifier += ";";
			}
			new_identifier += count+"-"+stop.getAddress().substring(0, 4);
			count++;
		}
		
		identifier = new_identifier;
	}
	
    private ArrayList<RemoteNode> stops;
	private String identifier;
	
	// TODO: These values should probably be in the self node preferences list
	private final double ideality_cooperativity_component = 0.50; // The cooperativity weight for finding ideality rating of paths.
	private final double ideality_online_count_component = 0.40; // The online count weight for finding ideality rating of paths.
	private final double ideality_trusted_count_component = 0.10; // The trusted node count weight for finding ideality rating of paths.
}