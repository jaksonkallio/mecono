package mecono;

// Jakson Kallio, 2019

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import static mecono.Self.MAX_RESPONSE_WAIT;
import node.MNode;
import parcel.Trigger;


// A node database is a specialized data container for node information
public class NodeDatabase {
	public NodeDatabase(Self self){
		this.self = self;
		node_memory = new HashMap<>();
	}
	
	public MNode getNode(String address){
		if(node_memory.containsKey(address)){
            return node_memory.get(address);
        }
        
		MNode new_node= new MNode(self);
        new_node.setAddress(address);
        node_memory.put(new_node.getAddress(), new_node);
        
        return new_node;
	}
	
	public MNode[] getTopNodes(int k){
		k = Math.max(1, k);
		MNode[] top_nodes = new MNode[k+1];
		
		for(Map.Entry<String, MNode> entry : node_memory.entrySet()) {
            String key = entry.getKey();
            MNode node = entry.getValue();
			
			top_nodes[k] = node;
			
			Arrays.sort(top_nodes, new SortByUseCount());
        }

		return top_nodes;
	}
	
	public int getNodeKnowledgeCount(){
		return node_memory.size();
	}
	
	// Reference to the self node
	private final Self self;
	
	// Nodes organized by address string
	private final HashMap<String, MNode> node_memory;
	
	public class SortByUseCount implements Comparator<MNode> {
		@Override
		public int compare(MNode a, MNode b) {
			if(a == null){
				return 1;
			}
			
			if(b == null){
				return -1;
			}
			
			return Integer.compare(a.getSendCount(), b.getSendCount());
		}
	}
}
