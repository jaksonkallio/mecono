package node;

import java.util.ArrayList;
import java.util.List;

public class AdjacencyList {
	public AdjacencyList(){
		adjacency_items = new ArrayList<>();
	}
	
	public class AdjacencyItem {
		public Node source;
		public List<Node> targets = new ArrayList<>();
	}
	
	public List<AdjacencyItem> adjacency_items;
}
