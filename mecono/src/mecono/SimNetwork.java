package mecono;

import java.util.ArrayList;

/**
 *
 * @author jak
 */
public class SimNetwork {
	
	public void begin(){
		generateSimSelfNodes(mesh_size);
		generateNeighborships();
	}
	
	public void generateSimSelfNodes(int count){
		for(int i = 0; i < count; i++){
			members.add(new SimSelfNode("n"+i));
		}
	}
	
	public static SimSelfNode getSelfNodeFromRemoteNode(RemoteNode target){
		for(SimSelfNode node : members){
			if(target.equals(node)){
				return node;
			}
		}
		
		return null;
	}
	
	public ArrayList<SimSelfNode> getMembers(){
		return members;
	}
	
	private void generateNeighborships(){
		// For each member, generate neighbors
		for(SimSelfNode member_self : members){
			int n = ((int) (Math.random() * ((maximum_neighbor_count - minimum_neighbor_count) + 1))) + minimum_neighbor_count;
			
			// Repeat neighbor adding with random node N times
			for(int i = 0; i < n; i++){
				// The other node is the RemoteNode, built from their memory controller and a String address.
				SimSelfNode other_self = members.get((int) (Math.random() * members.size()));
				RemoteNode other = member_self.getMemoryController().loadRemoteNode(other_self.getAddress());
				// Get the remote version of this member, to reciprocate the neighborship.
				RemoteNode member_remote = other_self.getMemoryController().loadRemoteNode(member_self.getAddress());
				other_self.addNeighbor(member_remote);
				member_self.addNeighbor(other);
			}
		}
	}
	
	public static boolean simulatedEventChance(double rate){
		return (rate >= 1 || Math.random() < rate);
	}
	
	public String getStats(){
		return "";
	}
	
	// Simulation Preferences
	private static final int mesh_size = 20;
	public static final boolean simulate_latency = true;
	public static final double parcel_lost_rate = 0.02; // Chance that a parcel just gets thrown out, to simulate a sudden connection glitch.
	public static final double adversarial_node_rate = 0.05; // Percent of nodes that don't follow the network protocol.
	public static final short minimum_neighbor_count = 1; // Minimum neighbors for random generation.
	public static final short maximum_neighbor_count = 3; // Maximum neighbors for random generation. 
	public static final double chance_neighbor_count_outlier = 0.25; // Chance for a node to have a non-standard neighbor count. If true, finds a random value between min/max neighbor count.
	private static ArrayList<SimSelfNode> members = new ArrayList<>();
}