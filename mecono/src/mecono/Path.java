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
	
	public double getAssuranceLevel() {
		// TODO: measure all nodes in path, and multiple their chances of success, return final assurance level.
		return 0.0;
	}
	
	public Node getStop(int i) {
		return stops.get(i);
	}
	
	public ArrayList<RemoteNode> getStops(){
		return stops;
	}
	
	public int getPathLength() {
		return stops.size();
	}
	
	public String getIdentifier(){
		regenerateIdentifier();
		return identifier;
	}
	
	public Path getSubpath(int start, int end){
		ArrayList<RemoteNode> subpath_stops = new ArrayList<>();
		
		while(start <= end){
			subpath_stops.add(stops.get(start));
			start++;
		}
		
		return new Path(subpath_stops);
	}
	
	public Path getSubpath(int end){
		return getSubpath(0, end);
	}
	
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
}