package node;

import java.util.ArrayList;
import java.util.List;
import mecono.MeconoSerializable;
import org.json.JSONArray;
import org.json.JSONObject;

public class AdjacencyList implements MeconoSerializable {
	public AdjacencyList(){
		adjacency_items = new ArrayList<>();
	}
	
	@Override
	public void deserialize(JSONObject adj_list_json){
		
	}
	
	@Override
	public JSONObject serialize(){
		JSONArray adj_list_array_json = new JSONArray();
		
		for(AdjacencyItem item : adjacency_items){
			JSONObject adj_list_item = new JSONObject();
			JSONArray targets_json = new JSONArray();
			
			for(Node target : item.targets){
				targets_json.put(target.getAddress());
			}
			
			adj_list_item.put("source", item.source.getAddress());
			adj_list_item.put("targets", targets_json);
			adj_list_array_json.put(adj_list_item);
		}
		
		JSONObject adj_list_json = new JSONObject();
		adj_list_json.put("adj_list", adj_list_array_json);
		
		return adj_list_json;
	}
	
	@Override
	public String toString(){
		StringBuilder str = new StringBuilder();
		
		for(AdjacencyItem item : adjacency_items){
			str.append(item.source.getTrimmedAddress());
			str.append("[");
			for(Node target : item.targets){
				str.append(target.getTrimmedAddress());
				str.append(",");
			}
			str.append("]");
		}
		
		return str.toString();
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
