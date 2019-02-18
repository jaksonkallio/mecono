package node;

import java.util.HashSet;
import java.util.Set;

public class Connection {
	public Connection(Node n1, Node n2){
		nodes = new HashSet<Node>();
		nodes.add(n1);
		nodes.add(n2);
	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof Connection){
			Connection other = (Connection) o;
			
			for(Node other_node : other.getNodes()){
				if(!getNodes().contains(other_node)){
					return false;
				}
			}
			
			return true;
		}
		
		return false;
	}
	
	public Node getOther(Node source){
		for(Node other : getNodes()){
			if(!other.equals(source)){
				return other;
			}
		}
		
		return null;
	}
	
	public double reliability(){
		return successes / Math.max(1, total);
	}
	
	public Set<Node> getNodes(){
		return nodes;
	}
	
	private final Set<Node> nodes;
	private int successes;
	private int total;
}
