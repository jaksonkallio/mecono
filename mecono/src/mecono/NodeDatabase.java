package mecono;

// Jakson Kallio, 2019

import java.util.HashMap;
import node.Node;


// A node database is a specialized data container for node information
public class NodeDatabase {
	public NodeDatabase(Self self){
		this.self = self;
		node_memory = new HashMap<>();
	}
	
	public Node getNode(String address){
		if(node_memory.containsKey(address)){
            return node_memory.get(address).node;
        }
        
		NodeRecord new_node_record = new NodeRecord();
		new_node_record.node = new Node(self);
        new_node_record.node.setAddress(address);
        node_memory.put(new_node_record.node.getAddress(), new_node_record);
        
        return new_node_record.node;
	}
	
	// Reference to the self node
	private final Self self;
	
	// Nodes organized by address string
	private final HashMap<String, NodeRecord> node_memory;
	
	private class NodeRecord {
		public Node node;
		public boolean deleted;
	}
}
