package mecono;

/**
 * A neighbor has a remote node.
 * @author jak
 */
public class Neighbor {
	public Neighbor(RemoteNode node, int network_port){
		this.node = node;
		this.network_port = network_port;
	}
	
	public int getPort(){
		return network_port;
	}
	
	public RemoteNode getNode(){
		return node;
	}
	
	private final RemoteNode node;
	private final int network_port;
}
