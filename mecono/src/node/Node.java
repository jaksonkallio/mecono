package node;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

public class Node {
	public Node(Address address){
		this.address = address;
		this.connections = new ArrayList<>();
	}
	
	public String getAddressString(){
		return address.getString() + '!' + coords.x + ',' + coords.y;
	}
	
	public void setLabel(String label){
		this.label = label;
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
	
	public Connection getConnection(Node other) {
		for(Connection c : getConnections()){
			if(c.getOther().equals(other)){
				return c;
			}
		}
		
		return null;
	}
	
	public void addConnection(Node other){
		getConnections().add(new Connection(this, other));
	}
	
	public Chain find(Node target) throws BadProtocolException {
		Chain best_chain = null;
		int squeeze = 10;

		Set<SearchNode> checked = new HashSet<>();
		Queue<SearchNode> check = new PriorityQueue<>();
		
		// Initial node is this node
		check.offer(new SearchNode(null, this, (int)(getCoords().dist(target.getCoords()))));
		
		// While there are still SearchNodes to check AND the squeeze is greater than zero
		while(!check.isEmpty() && squeeze > 0){
			SearchNode curr_node = check.poll();
			
			if(curr_node.node.equals(target)){
				Chain potential_chain = createChainFromSearchNode(curr_node);
				
				if(best_chain == null || potential_chain.reliability() > best_chain.reliability()){
					best_chain = potential_chain;
				}
			}else{
				// For each neighboring node to the current one
				for(Connection curr_node_connection : curr_node.node.getConnections()){
					// Create a new search node with the current as the parent
					check.offer(new SearchNode(curr_node, curr_node_connection.getOther(), (int)(getCoords().dist(target.getCoords()))));
				}
			}
			
			if(best_chain != null){
				squeeze--;
			}
		}
		
		return best_chain;
	}
	
	private Chain createChainFromSearchNode(SearchNode last){
		Chain chain = new Chain();
		SearchNode curr = last;
		
		while(curr != null){
			chain.addNode(0, curr.node);
			curr = last.parent;
		}
		
		return chain;
	}
	
	private class SearchNode implements Comparable {
		public SearchNode(SearchNode parent, Node node, int cost){
			this.parent = parent;
			this.node = node;
			this.cost = cost;
		}
		
		@Override
		public int hashCode(){
			return node.hashCode();
		}
		
		@Override
		public int compareTo(Object o){
			if(o instanceof SearchNode){
				SearchNode other = (SearchNode) o;
				return other.cost - this.cost;
			}
			
			return Integer.MAX_VALUE;
		}
		
		public final SearchNode parent;
		public final Node node;
		public final int cost;
	}
	
	private String label;
	private final Address address;
	private final List<Connection> connections;
	private GeoCoord coords;
}
