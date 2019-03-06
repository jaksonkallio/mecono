package node;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import javax.xml.bind.DatatypeConverter;
import mecono.MeconoSerializable;
import mecono.Self;
import org.json.JSONObject;
import parcel.Find;

public class Node implements MeconoSerializable {
	public Node(Self self){
        this.self = self;
		this.connections = new ArrayList<>();
	}
	
	public String getAddressString(){
		return getAddress() + '!' + coords.x + ',' + coords.y;
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
	
	public String getPublicKey(){
		return public_key;
	}
	
	public void setPublicKey(String public_key){
		this.public_key = public_key;
	}
	
	public static boolean validAddress(String address){
		// TODO: valid address check
		return true;
	}
	
	public String getAddress() {
		if((address == null || address.length() == 0) && public_key != null){
			// If the address is null or the length is zero, perform hash on the public key
			try {
				MessageDigest digest = MessageDigest.getInstance("SHA-256");
	            byte[] hash = digest.digest(public_key.getBytes("UTF-8"));
	            address = DatatypeConverter.printHexBinary(hash);
			}catch(NoSuchAlgorithmException | UnsupportedEncodingException ex){
				// TODO: node log
			}
		}
		
		return address;
	}
	
	public void setAddress(String address){
		if(getAddress() == null){
			this.address = address;
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
	
	public void consult(Node target){
		Find find = new Find(self);
		find.setTarget(target);
		find.setDestination(this);
		
		self.enqueueSend(find);
	}
	
	public Chain find(Node target) throws BadProtocolException {
		Chain best_chain = null;
		int squeeze = 10;

		Set<SearchNode> checked = new HashSet<>();
		Queue<SearchNode> check = new PriorityQueue<>();
		
		// Initial node is this node
		check.offer(new SearchNode(null, this, target));
		
		// While there are still SearchNodes to check AND the squeeze is greater than zero
		while(!check.isEmpty() && squeeze > 0){
			SearchNode curr_node = check.poll();
			
			if(curr_node.node.equals(target)){
				Chain potential_chain = curr_node.getChain();
				
				if(best_chain == null || potential_chain.reliability() > best_chain.reliability()){
					best_chain = potential_chain;
				}
			}else{
				// For each neighboring node to the current one
				for(Connection curr_node_connection : curr_node.node.getConnections()){
					// Create a new search node with the current as the parent
					check.offer(new SearchNode(curr_node, curr_node_connection.getOther(curr_node.node), target));
				}
			}
			
			if(best_chain != null){
				squeeze--;
			}
		}
		
		return best_chain;
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
		if(json.has("public_key")){
			setPublicKey(json.getString("public_key"));
		}
		
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
			Chain chain = getChain();
			double fail_rate = 1.0 - chain.reliability();
			double distance = node.getCoords().dist(target.getCoords());
			
			return (int)(distance * fail_rate);
			
		}
		
		public final SearchNode parent;
		public final Node node;
		public final Node target;
	}
	
	public static final int MAX_BLURB_LENGTH = 100;
	
	private String blurb;
	private String public_key;
	private String address;
	private final List<Connection> connections;
	private int send_count;
	private int receive_count;
	private long last_test;
	private GeoCoord coords;
    private Self self;
}
