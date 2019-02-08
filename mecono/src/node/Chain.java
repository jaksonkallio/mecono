package node;

import java.util.ArrayList;
import java.util.List;

public class Chain {
	public Chain(){
		nodes = new ArrayList<>();
	}
	
	public void addNode(Node node){
		if(!nodes.contains(node)){
			nodes.add(node);
		}
	}
	
	public void addNode(int i, Node node){
		nodes.add(i, node);
	}
	
	public List<Node> getNodes(){
		return nodes;
	}
	
	public double reliability(){
		double reliability = 1.0;
		
		for(int i = 0; i < nodes.size() - 1; i++){
			Node curr = nodes.get(i);
			Node next = nodes.get(i + 1);
			
			reliability = reliability * curr.getConnection(next).reliability();
		}
		
		return reliability;
	}
	
	private final List<Node> nodes;
}
