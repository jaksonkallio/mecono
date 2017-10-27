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
	
	protected Path path_history;
	protected Path path;
}
