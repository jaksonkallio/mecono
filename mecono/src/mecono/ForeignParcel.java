package mecono;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Foreign parcels are parcels where the SelfNode is neither the originator or
 * destination.
 *
 * @author jak
 */
public class ForeignParcel extends Parcel {

	public ForeignParcel(Path path_history, String payload) {
		this.path = path;
		this.payload = payload;
	}

	/**
	 * Gets the next node.
	 *
	 * @return RemoteNode The next node
	 */
	public RemoteNode getNextDestination() {
		return (RemoteNode) path_history.getStop(path_history.getPathLength() - 1);
	}
	
	@Override
	public String toString(){
		return "Foreign Parcel - Next: "+getNextNode().getAddress();
	}

	@Override
	public void setPath(Path path){
		this.path_history = path;
	}
	
	@Override
	public Path getPath(){
		return path_history;
	}
	
	/**
	 * Gets how many hops this parcel has traveled so far.
	 *
	 * @return Integer Hop count
	 */
	public int getHopCount() {
		return (path_history.getPathLength() - 2);
	}

	@Override
	public RemoteNode getNextNode() {
		// For foreign parcels, the next node is the last item in the path.
		return (RemoteNode) path.getStop(path.getPathLength() - 1);
	}
	
	@Override
	public JSONObject serialize(){
		JSONObject serialized_parcel = new JSONObject();
		JSONArray serialized_path_history = new JSONArray(getPath());
		
		serialized_parcel.put("path_history", serialized_path_history);
		serialized_parcel.put("payload", payload);
		
		return serialized_parcel;
	}

	@Override
	public Node getOriginator() {
		return path_history.getStop(0);
	}
	
	private String payload;
	private Path path_history;
}
