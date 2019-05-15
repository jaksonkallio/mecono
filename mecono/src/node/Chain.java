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
	
	public MNode getNext(){
		boolean found = false;
		
		for(MNode node : getNodes()){
			if(found){
				return node;
			}
			
			if(node.equals(self.getSelfNode())){
				found = true;
			}
		}
		
		return null;
	}
	
	@Override
	public String toString(){
		StringBuilder str = new StringBuilder();
		boolean first = false;
		
		str.append("[");
		for(MNode node : getNodes()){
			if(!first){
				first = true;
			}else{
				str.append(",");
			}
			
			str.append(node.getTrimmedAddress());
		}
		
		str.append("]");
		
		return str.toString();
	}
	
	public void addNode(MNode node){
		if(!getNodes().contains(node)){
			getNodes().add(node);
		}
	}
	
	public void addNode(int i, MNode node){
		getNodes().add(i, node);
	}
	
	public List<MNode> getNodes(){
		return nodes;
	}
    
    public MNode getNode(int i){
        return getNodes().get(i);
    }
    
    public MNode getOriginNode(){
        if(getNodes().size() > 0){
            return getNodes().get(0);
        }
        
        return null;
    }
    
	public boolean empty(){
		return getNodes().isEmpty();
	}
	
    public MNode getDestinationNode(){
        if(getNodes().size() > 0){
            return getNodes().get(getNodes().size() - 1);
        }
        
        return null;
    }
	
	public double getGeoLength(){
		double sum = 0;
		
		for(int i = 1; i < getNodes().size(); i++){
			sum += getNode(i - 1).getCoords().dist(getNode(i).getCoords());
		}
		
		return sum;
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
            MNode curr = nodes.get(i);
			MNode next = nodes.get(i + 1);
			
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
		JSONObject nodes_json = new JSONObject();
		JSONArray nodes_array = new JSONArray();
		
		for(MNode node : getNodes()){
			nodes_array.put(node.getAddress());
		}
		
		nodes_json.put("nodes", nodes_array);
		
		return nodes_json;
	}

	@Override
	public void deserialize(JSONObject chain_json) throws BadSerializationException {
		if(chain_json.has("nodes")){
			JSONArray nodes = chain_json.getJSONArray("nodes");
			
			for(int i = 0; i < nodes.length(); i++){
				String pubkey = nodes.getString(i);
				MNode node = self.getNodeDatabase().getNode(pubkey);
				
				if(node != null){
					addNode(node);
				}
			}
		}else{
			throw new BadSerializationException("No nodes to deserialize");
		}
	}
	
	private final List<MNode> nodes;
	private final Self self;
}
