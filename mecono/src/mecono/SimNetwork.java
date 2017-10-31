package mecono;

import java.util.ArrayList;

/**
 *
 * @author jak
 */
public class SimNetwork {
	
	public void begin(){
		SimSelfNode a = new SimSelfNode("Ann");
		SimSelfNode b = new SimSelfNode("Bob");
		SimSelfNode c = new SimSelfNode("Cal");
		
		a.generateNewAddress();
		b.generateNewAddress();
		c.generateNewAddress();
		
		a.receiveRawString("111-222-333,encrypteddata");
	}
	
	public static SimSelfNode getSelfNodeFromRemoteNode(RemoteNode target){
		for(SimSelfNode node : members){
			if(target.equals(node)){
				return node;
			}
		}
		
		return null;
	}
	
	public static void generateNeighborships(){
		for(SimSelfNode member : members){
			int n = ((int) (Math.random() * ((maximum_neighbor_count - minimum_neighbor_count) + 1))) + minimum_neighbor_count;
		}
	}
	
	public static boolean simulatedEventChance(double rate){
		return (rate >= 1 || Math.random() < rate);
	}
	
	// Simulation Preferences
	public static final boolean simulate_latency = true;
	public static final double parcel_lost_rate = 0.02; // Chance that a parcel just gets thrown out, to simulate a sudden connection glitch.
	public static final double adversarial_node_rate = 0.05; // Percent of nodes that don't follow the network protocol.
	public static final short minimum_neighbor_count = 1; // Minimum neighbors a node must have.
	public static final short maximum_neighbor_count = 8; // Maximum neighbors a node may have.
	public static final double chance_neighbor_count_outlier = 0.25; // Chance for a node to have a non-standard neighbor count. If true, finds a random value between min/max neighbor count.
	private static ArrayList<SimSelfNode> members = new ArrayList<>();
}