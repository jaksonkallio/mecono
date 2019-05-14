package node;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import mecono.ErrorLevel;
import mecono.MeconoSerializable;
import mecono.Self;
import mecono.Util;
import org.json.JSONObject;
import parcel.Find;
import parcel.Test;

public class Node implements MeconoSerializable {
	public Node(Self self){
        this.self = self;
		this.connections = new ArrayList<>();
	}
	
	public String getAddressString(){
		return getAddress() + '!' + coords.x + ',' + coords.y;
	}
	
	@Override
	public String toString(){
		return getTrimmedAddress() + " Send Count: " + getSendCount();
	}
	
	public boolean equals(Object o){
		if(o instanceof Node){
			Node other = (Node) o;
			
			if(other.getAddress().equals(getAddress())){
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public int hashCode(){
		return getAddress().hashCode();
	}
	
	public PublicKey getPublicKey(){
		return public_key;
	}
	
	public void setPublicKey(PublicKey public_key){
		this.public_key = public_key;
	}
	
	public static boolean validAddress(String address){
		// TODO: valid address check
		return true;
	}
	
	public String getAddress() {
        return Util.bytesToHex(getPublicKey().getEncoded());
	}
    
    public String getTrimmedAddress(){
        return getAddress().substring(58, 61)+"*"+getAddress().substring(getAddress().length() - 13, getAddress().length() - 10);
    }
	
	public void setAddress(String address){
        try {
            PublicKey new_public_key = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Util.hexStringToByteArray(address)));
            setPublicKey(new_public_key);
        }catch(InvalidKeySpecException | NoSuchAlgorithmException ex){
            self.log(ErrorLevel.ERROR, "Could not set address of node", ex.getMessage());
        }
	}
	
	public void setBlurb(String blurb){
		if(blurb.length() > MAX_BLURB_LENGTH){
			blurb = blurb.substring(0, MAX_BLURB_LENGTH);
		}
		
		this.blurb = blurb;
	}
	
	public String getBlurb(){
		return blurb;
	}
	
	public GeoCoord getCoords(){
		if(coords == null){
			setCoords(new GeoCoord(0, 0));
		}
		
		return coords;
	}
	
	public void setCoords(GeoCoord coords){
		this.coords = coords;
	}
	
	public List<Connection> getConnections(){
		return connections;
	}
    
    public List<Node> getNeighbors(){
        List<Node> results = new ArrayList<>();
        
        for(Connection conn : getConnections()){
            results.add(conn.getOther(this));
        }
        
        return results;
    }
	
	public Connection getConnection(Node other) {
		for(Connection c : getConnections()){
			if(c.getOther(this).equals(other)){
				return c;
			}
		}
		
		return null;
	}
	
	public void addConnection(Node other){
		Connection conn = new Connection(this, other);
		
		if(!getConnections().contains(conn)){
			getConnections().add(conn);
		}
		
		if(!other.getConnections().contains(conn)){
			other.getConnections().add(conn);
		}
	}
	
	public void findMe(){
		for(Node friend : self.getFriends()){
			friend.consult(this);
		}
	}
    
    public void test(){
        Test test = new Test(self);
		test.setDestination(this);
		test.enqueueSend();
    }
	
	public void consult(Node target){
		Find find = new Find(self);
		find.setTarget(target);
		find.setDestination(this);
		find.enqueueSend();
	}
	
	public Chain find(Node target) throws BadProtocolException {
		Set<SearchNode> checked = new HashSet<>();
		Queue<SearchNode> check = new PriorityQueue<>();
		
		// Initial node is this node
		check.offer(new SearchNode(null, this, target));
		
		// While there are still SearchNodes to check AND the squeeze is greater than zero
		while(!check.isEmpty()){
			SearchNode curr_node = check.poll();
			checked.add(curr_node);
			
			
			if(curr_node.node.equals(target)){
				return curr_node.getChain();
			}else{
				// For each neighboring node to the current one
				for(Connection curr_node_connection : curr_node.node.getConnections()){
					SearchNode new_search_node = new SearchNode(curr_node, curr_node_connection.getOther(curr_node.node), target);
					
					// Don't get stuck in cyclic graph
					if(!checked.contains(new_search_node)){
						// Create a new search node with the current as the parent
						check.offer(new_search_node);
					}
				}
			}
		}
		
		return null;
	}
	
	public void incSendCount(){
		send_count++;
	}
	
	public int getSendCount(){
		return send_count;
	}

	@Override
	public JSONObject serialize() {
		// By default, we use bootstrap mode
		return serialize(true);
	}
	
	public boolean emptyAddress(){
		return getAddress() == null || getAddress().length() == 0;
	}
	
	// In bootstrap mode several bits of "personalized" metadata are left out
	public JSONObject serialize(boolean bootstrap) {
		JSONObject node_json = new JSONObject();
		node_json.put("address", getAddress());
		node_json.put("coords", getCoords().serialize());
		node_json.put("blurb", getBlurb());
		
		if(!bootstrap){
			node_json.put("send_count", send_count);
			node_json.put("receive_count", receive_count);
			node_json.put("last_test", last_test);
		}
	
		return node_json;
	}
    
    @Override
	public void deserialize(JSONObject json) throws BadSerializationException {
		if(json.has("address")){
			setAddress(json.getString("address"));
		}
		
		if(emptyAddress()){
			throw new BadSerializationException("Unable to deserialize an address or public key");
		}
		
		if(json.has("coords")){
            GeoCoord coords = new GeoCoord();
            coords.deserialize(json.getJSONObject("coords"));
			setCoords(coords);
		}
		
		if(json.has("blurb")){
			setBlurb(json.getString("blurb"));
        }
	}
	
	private class SearchNode implements Comparable {
		public SearchNode(SearchNode parent, Node node, Node target){
			this.parent = parent;
			this.node = node;
			this.target = target;
		}
		
		@Override
		public int hashCode(){
			return node.hashCode();
		}
		
		@Override
		public boolean equals(Object o){
			if(o instanceof SearchNode){
				SearchNode other = (SearchNode) o;
				
				return other.hashCode() == this.hashCode();
			}
		
			return false;
		}
		
		@Override
		public int compareTo(Object o){
			if(o instanceof SearchNode){
				SearchNode other = (SearchNode) o;
				return other.getCost() - this.getCost();
			}
			
			return Integer.MAX_VALUE;
		}
		
		public Chain getChain(){
			Chain chain = new Chain(self);
			SearchNode curr = this;

			while(curr != null){
				chain.addNode(0, curr.node);
				curr = curr.parent;
			}

			return chain;
		}
		
		private int getCost(){
			if(node == null || target == null){
				return Integer.MAX_VALUE;
			}
			
			Chain chain = getChain();
			double current_distance = getChain().getGeoLength();
			double fail_rate = 1.0 - chain.reliability();
			double next_distance = node.getCoords().dist(target.getCoords());
			double total_distance = current_distance + next_distance;
			
			// cost = distance + distance * fail_rate
			
			return (int)(total_distance * (1 + fail_rate));
			
		}
		
		public final SearchNode parent;
		public final Node node;
		public final Node target;
	}
	
	public static final int MAX_BLURB_LENGTH = 100;
	
	private String blurb;
	private PublicKey public_key;
	private String address;
	private final List<Connection> connections;
	private int send_count;
	private int receive_count;
	private long last_test;
	private GeoCoord coords;
    private Self self;
}
