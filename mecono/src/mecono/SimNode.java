package mecono;

/**
 *
 * @author jak
 */
public class SimNode extends RemoteNode {
	
	public SimNode() {
		super(Protocol.generateAddress());
	}
	
	public SimNode(String address) {
		super(address);
	}
}