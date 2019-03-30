package node;

import java.util.ArrayList;
import java.util.List;

public class AdjacencyList {
	public AdjacencyList(){
		adjacency_items = new ArrayList<>();
	}
	
	public class AdjacencyItem {
		public String source_address;
		public List<String> target_addresses = new ArrayList<>();
	}
	
	public List<AdjacencyItem> adjacency_items;
}
