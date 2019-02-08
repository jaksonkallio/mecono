package node;

public class Connection {
	public Connection(Node source, Node other){
		this.source = source;
		this.other = other;
	}
	
	public Node getOther(){
		return other;
	}
	
	public Node getSource(){
		return source;
	}
	
	private final Node source;
	private final Node other;
}
