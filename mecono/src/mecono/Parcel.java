package mecono;

/**
 *
 * @author jak
 */
public abstract class Parcel {
	
	/**
	 * Which node originated this parcel, supposedly.
	 * @return RemoteNode Originator node object.
	 */
	public RemoteNode getOriginator(){
		return (RemoteNode) path.getStop(0);
	}
	
	public void setPath(Path path){
		this.path = path;
	}
	
	/**
	 * Gets the next node in the path.
	 * @return 
	 */
	public RemoteNode getNextNode(){
		return null;
	}
	
	public String serialize(){
		return null;
	}
	
	protected Path path_history;
	protected Path path;
}
