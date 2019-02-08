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
	
	public double reliability(){
		return successes / Math.max(1, total);
	}
	
	private final Node source;
	private final Node other;
	private int successes;
	private int total;
}
