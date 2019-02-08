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
	
	public Chain find(Node target) throws BadProtocolException {
		Chain best_chain = null;
		Set<SearchNode> checked = new HashSet<>();
		Queue<SearchNode> check = new PriorityQueue<>();
		
		while(!check.isEmpty()){
			
		}
		
		return best_chain;
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
