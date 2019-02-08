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
	
	private final List<Node> nodes;
}
