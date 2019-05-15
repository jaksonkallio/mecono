package node;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import mecono.MeconoSerializable;
import node.AdjacencyList.AdjacencyItem;
import org.json.JSONArray;
import org.json.JSONObject;

public class AdjacencyList implements MeconoSerializable, Iterable<AdjacencyItem> {
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
			
			for(MNode target : item.targets){
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
		
		for(AdjacencyItem item : this){
			str.append(item.source.getTrimmedAddress());
			str.append("->[");
			for(MNode target : item.targets){
				str.append(target.getTrimmedAddress());
				str.append(",");
			}
			str.append("]\n");
		}
		
		return str.toString();
	}
	
	public boolean hasConnection(MNode n1, MNode n2){
		return hasConnectionDirected(n1, n2) || hasConnectionDirected(n2, n1); 
	}
	
	public boolean hasConnectionDirected(MNode source, MNode target){
		for(AdjacencyItem item : adjacency_items){
			for(MNode curr_target : item.targets){
				if(item.source.equals(source) && curr_target.equals(target)){
					return true;
				}
			}
		}
		
		return false;
	}
	
	public void addConnection(MNode n1, MNode n2){
		boolean added = false;
		
		if(hasConnection(n1, n2)){
			return;
		}
		
		for(AdjacencyItem item : adjacency_items){
			if(item.source.equals(n1)){
				item.targets.add(n2);
				added = true;
				return;
			}
			
			if(item.source.equals(n2)){
				item.targets.add(n1);
				added = true;
				return;
			}
		}
		
		if(!added){
			AdjacencyItem new_adj_item = new AdjacencyItem();
			new_adj_item.source = n1;
			new_adj_item.targets = new ArrayList<>();
			new_adj_item.targets.add(n2);
			
			adjacency_items.add(new_adj_item);
		}
	}

	@Override
	public Iterator<AdjacencyItem> iterator() {
		Iterator<AdjacencyItem> it = new Iterator<AdjacencyItem>() {

            private int i = 0;

            @Override
            public boolean hasNext() {
                return i < adjacency_items.size() && adjacency_items.get(i) != null;
            }

            @Override
            public AdjacencyItem next() {
				i++;
                return adjacency_items.get(i - 1);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
		
        return it;
	}
	
	public class AdjacencyItem {
		public MNode source;
		public List<MNode> targets = new ArrayList<>();
	}
	
	public List<AdjacencyItem> adjacency_items;
}
