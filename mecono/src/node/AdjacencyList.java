package node;

import java.util.ArrayList;
import java.util.List;

public class AdjacencyList {
	public AdjacencyList(){
		adjacency_items = new ArrayList<>();
	}
	
	public boolean hasConnection(Node n1, Node n2){
		return hasConnectionDirected(n1, n2) || hasConnectionDirected(n2, n1); 
	}
	
	public boolean hasConnectionDirected(Node source, Node target){
		for(AdjacencyItem item : adjacency_items){
			for(Node curr_target : item.targets){
				if(item.source.equals(source) && curr_target.equals(target)){
					return true;
				}
			}
		}
		
		return false;
	}
	
	public void addConnection(Node n1, Node n2){
		if(hasConnection(n1, n2)){
			return;
		}
		
		for(AdjacencyItem item : adjacency_items){
			if(item.equals(n1)){
				item.targets.add(n2);
				return;
			}
			
			if(item.equals(n2)){
				item.targets.add(n1);
				return;
			}
		}
	}
	
	public class AdjacencyItem {
		public Node source;
		public List<Node> targets = new ArrayList<>();
	}
	
	public List<AdjacencyItem> adjacency_items;
}
