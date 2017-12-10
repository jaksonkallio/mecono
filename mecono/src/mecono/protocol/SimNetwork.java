package mecono.protocol;

import mecono.ui.SimGUI;
import mecono.node.SimSelfNode;
import mecono.node.Neighbor;
import mecono.node.RemoteNode;
import java.util.ArrayList;
import mecono.node.NodeAddress;

/**
 *
 * @author jak
 */
public class SimNetwork {
	
	public SimNetwork(int mesh_size){
		this.mesh_size = mesh_size;
		initializeRandomEnvironment();
		this.sim_gui = new SimGUI(this);
	}
	
	public SimNetwork(){
		this.mesh_size = 0;
		initializeControlledEnvironment();
		this.sim_gui = new SimGUI(this);
	}
	
	public SimGUI getSimGUI(){
		return sim_gui;
	}
	
	public void startMailboxWorkers(){
		for (SimSelfNode node : members) {
			node.getMailbox().getWorker().startWorking();
			node.nodeLog(0, "Started sim node mailbox worker");
		}
	}
	
	public void stopMailboxWorkers(){
		for (SimSelfNode node : members) {
			node.getMailbox().getWorker().stopWorking();
			node.nodeLog(0, "Stopped sim node mailbox worker");
		}
	}
	
	public final void initializeControlledEnvironment(){
		members.clear();
		String test_addr_suffix = "eee";
		
		// CSE v1 https://github.com/jaksonkallio/mecono/issues/13
		members.add(new SimSelfNode("Andreas", new NodeAddress("A"+test_addr_suffix), this));
		members.add(new SimSelfNode("Brandon", new NodeAddress("B"+test_addr_suffix), this));
		members.add(new SimSelfNode("Carolyn", new NodeAddress("C"+test_addr_suffix), this));
		members.add(new SimSelfNode("Dominic", new NodeAddress("D"+test_addr_suffix), this));
		members.add(new SimSelfNode("Evelyn", new NodeAddress("E"+test_addr_suffix), this));
		members.add(new SimSelfNode("Finn", new NodeAddress("F"+test_addr_suffix), this));
		members.add(new SimSelfNode("Gerald", new NodeAddress("G"+test_addr_suffix), this));
		members.add(new SimSelfNode("Xavier", new NodeAddress("X"+test_addr_suffix), this));
		
		createNeighborship(members.get(0), members.get(1));
		createNeighborship(members.get(0), members.get(3));
		createNeighborship(members.get(1), members.get(3));
		createNeighborship(members.get(2), members.get(4));
		createNeighborship(members.get(3), members.get(4));
		createNeighborship(members.get(4), members.get(5));
		createNeighborship(members.get(5), members.get(6));
		createNeighborship(members.get(5), members.get(7));
		createNeighborship(members.get(6), members.get(7));
		
		int test_parcels[][] = {
			{0, 1},
			{2, 3},
			{1, 5},
			{1, 6}
		};
		
		for(int i = 0; i < test_parcels.length; i++){
			SimSelfNode sender = members.get(test_parcels[i][0]);
			RemoteNode receiver = sender.getMemoryController().loadRemoteNode(members.get(test_parcels[i][1]).getAddress());
			sender.sendDataParcel(receiver, "test_message_" + i);
		}
	}
	
	public final void initializeRandomEnvironment() {
		if(!initialized){
			generateSimSelfNodes(mesh_size);
			generateRandomNeighborships();

			for(int i = 0; i < 3; i++){
				SimSelfNode originator = members.get((int) (Math.random()*members.size()));
				RemoteNode destination = originator.getMemoryController().loadRemoteNode(members.get((int) (Math.random()*members.size())).getAddress());
				originator.sendDataParcel(destination, "test_message_"+i);
			}
			
			initialized = true;
		}
	}

	public void generateSimSelfNodes(int count) {
		for (int i = 0; i < count; i++) {
			members.add(new SimSelfNode("n" + i, this));
		}
	}

	public static SimSelfNode getSelfNodeFromRemoteNode(RemoteNode target) {
		for (SimSelfNode node : members) {
			if (target.equals(node)) {
				return node;
			}
		}

		return null;
	}

	public ArrayList<SimSelfNode> getMembers() {
		return members;
	}
	
	private void memberOutboxProcess(){
		for (SimSelfNode node : members) {
			for(int i = 0; i < node.getMailbox().getOutboxCount(); i++){
				node.getMailbox().processOutboxItem(i);
			}
		}
	}
	
	private void generateRandomNeighborships() {
		// For each member, generate neighbors
		for (SimSelfNode node1 : members) {
			int n = ((int) (Math.random() * ((maximum_neighbor_count - minimum_neighbor_count) + 1))) + minimum_neighbor_count;

			// Repeat neighbor adding with random node N times
			for (int i = 0; i < n; i++) {
				// The other node is the RemoteNode, built from their memory controller and a String address.
				SimSelfNode node2 = members.get((int) (Math.random() * members.size()));
				createNeighborship(node1, node2);
			}
		}
	}
	
	private void createNeighborship(SimSelfNode node1, SimSelfNode node2){
		RemoteNode node2_remote = node1.getMemoryController().loadRemoteNode(node2.getAddress());
		RemoteNode node1_remote = node2.getMemoryController().loadRemoteNode(node1.getAddress());
		node1.addNeighbor(new Neighbor(node2_remote, 2));
		node2.addNeighbor(new Neighbor(node1_remote, 2));
	}

	public static boolean simulatedEventChance(double rate) {
		return (rate >= 1 || Math.random() < rate);
	}

	public String getStats() {
		return "";
	}

	// Simulation Preferences
	private final int mesh_size;
	private boolean initialized = false;
	private boolean in_loop = false;
	public static final boolean simulate_latency = true;
	public static final double parcel_lost_rate = 0.02; // Chance that a parcel just gets thrown out, to simulate a sudden connection glitch.
	public static final double adversarial_node_rate = 0.05; // Percent of nodes that don't follow the network protocol.
	public static final short minimum_neighbor_count = 1; // Minimum neighbors for random generation.
	public static final short maximum_neighbor_count = 3; // Maximum neighbors for random generation. 
	public static final double chance_neighbor_count_outlier = 0.25; // Chance for a node to have a non-standard neighbor count. If true, finds a random value between min/max neighbor count.
	private static ArrayList<SimSelfNode> members = new ArrayList<>();
	private SimGUI sim_gui;
}
