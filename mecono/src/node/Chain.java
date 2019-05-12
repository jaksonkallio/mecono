package node;

import java.util.ArrayList;
import java.util.List;
import mecono.MeconoSerializable;
import mecono.Self;
import org.json.JSONArray;
import org.json.JSONObject;

public class Chain implements MeconoSerializable {
	public Chain(Self self){
		nodes = new ArrayList<>();
		this.self = self;
	}
	
	public void addNode(Node node){
		if(!getNodes().contains(node)){
			getNodes().add(node);
		}
	}
	
	public void addNode(int i, Node node){
		getNodes().add(i, node);
	}
	
	public List<Node> getNodes(){
		return nodes;
	}
    
    public Node getNode(int i){
        return getNodes().get(i);
    }
    
    public Node getOriginNode(){
        if(getNodes().size() > 0){
            return getNodes().get(0);
        }
        
        return null;
    }
    
	public boolean empty(){
		return getNodes().isEmpty();
	}
	
    public Node getDestinationNode(){
        if(getNodes().size() > 0){
            return getNodes().get(getNodes().size() - 1);
        }
        
        return null;
    }
	
	public double reliability(){
		double reliability = 1.0;
		
		for(Connection conn : getConnections()){
			reliability *= conn.reliability();
		}
		
		return reliability;
	}
    
    public List<Connection> getConnections(){
        List<Connection> conns = new ArrayList<>();
        
        for(int i = 0; i < nodes.size() - 1; i++){
            Node curr = nodes.get(i);
			Node next = nodes.get(i + 1);
			
			conns.add(curr.getConnection(next));
        }
        
        return conns;
    }
	
	public void test(){
		getDestinationNode().test();
	}
    
    public void logSuccess(long ping){
		long avg_ping_per_connection = ping / getConnections().size();
        
		for(Connection conn : getConnections()){
            conn.logSuccess(avg_ping_per_connection);
        }
    }
    
    public void logUse(){
        for(Connection conn : getConnections()){
            conn.logUse();
        }
    }
	
	public boolean online(){
		for(Connection conn : getConnections()){
			if(!conn.online()){
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public JSONObject serialize() {
		return null;
	}

	@Override
	public void deserialize(JSONObject chain_json) throws BadSerializationException {
		if(chain_json.has("nodes")){
			JSONArray nodes = chain_json.getJSONArray("nodes");
			
			for(int i = 0; i < nodes.length(); i++){
				String pubkey = nodes.getString(i);
				Node node = self.getNodeDatabase().getNode(pubkey);
				
				if(node != null){
					addNode(node);
				}
			}
		}else{
			throw new BadSerializationException("No nodes to deserialize");
		}
	}
	
	private final List<Node> nodes;
	private final Self self;
}
