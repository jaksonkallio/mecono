package mecono;

/**
 * Foreign parcels are parcels where the SelfNode is neither the originator or
 * destination.
 *
 * @author jak
 */
public class ForeignParcel extends Parcel {

	public ForeignParcel(Path path_history, String payload) {
		this.path_history = path_history;
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
	
	public String toString(){
		return "Foreign Parcel - Next: "+getNextNode().getAddress();
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

	private String payload;
}
