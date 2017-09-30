package mecono;

/**
 *
 * @author jak
 */
public class PathSegment {
    
	public PathSegment(String address, Path parent_path) {
		this.parent_path = parent_path;
		// Look up node by address string
		this.node = SelfNode.getRemoteNode(address);
	}
	
	private final Path parent_path;
	private final Node node;
}