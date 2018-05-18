package mecono.protocol.cse;

import mecono.ui.SimGUI;
import mecono.node.SimSelfNode;
import mecono.node.Neighbor;
import mecono.node.RemoteNode;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author jak
 */
public abstract class SimNetwork {

	public SimNetwork() {
		init();
		this.sim_gui = new SimGUI(this);
	}
	
	private void init(){
		initEnvironment();
	}

	public SimGUI getSimGUI() {
		return sim_gui;
	}

	public String getVersionLabel(){
		return this.getClass().getSimpleName();
	}
	
	public void startMailboxWorkers() {
		for (SimSelfNode node : node_set) {
			node.getMailbox().getWorker().startWorking();
			node.nodeLog(0, "Started sim node mailbox worker");
		}
	}

	public void stopMailboxWorkers() {
		for (SimSelfNode node : node_set) {
			node.getMailbox().getWorker().stopWorking();
			node.nodeLog(0, "Stopped sim node mailbox worker");
		}
	}

	protected abstract void initEnvironment();

	/*public final void initializeRandomEnvironment() {
		if (!initialized) {
			generateSimSelfNodes(mesh_size);
			generateRandomNeighborships();

			for (int i = 0; i < 3; i++) {
				SimSelfNode originator = members.get((int) (Math.random() * members.size()));
				RemoteNode destination = originator.getMemoryController().loadRemoteNode(members.get((int) (Math.random() * members.size())).getAddress());
				originator.sendDataParcel(destination, "test_message_" + i);
			}

			initialized = true;
		}
	}

	public void generateSimSelfNodes(int count) {
		for (int i = 0; i < count; i++) {
			members.add(new SimSelfNode("n" + i, this));
		}
	}*/

	public SimSelfNode getSelfNodeFromRemoteNode(RemoteNode target) {
		for (SimSelfNode node : node_set) {
			if (target.equals(node)) {
				return node;
			}
		}

		return null;
	}

	public ArrayList<SimSelfNode> getMembers() {
		return node_set;
	}
	
	public int parcelsInOutbox(){
		int sum = 0;
		
		for(SimSelfNode node : getNodeSet()){
			sum += node.getMailbox().getOutboxCount();
		}
		
		return sum;
	}
	

	/*private void memberOutboxProcess() {
		for (SimSelfNode node : members) {
			for (int i = 0; i < node.getMailbox().getOutboxCount(); i++) {
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
	}*/

	protected void createNeighborship(SimSelfNode node1, SimSelfNode node2) {
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
	
	public ArrayList<SimSelfNode> getNodeSet(){
		return node_set;
	}
	
	public ArrayList<ArrayList<Integer>> getParcelSet(){
		return parcel_set;
	}
	
	protected void addSampleParcel(int n1, int n2){
		Integer[] pair = {n1, n2};
		parcel_set.add(new ArrayList<>(Arrays.asList(pair)));
	}
	
	protected void distributeSampleParcels(){
		for(int i = 0; i < parcel_set.size(); i++){
			SimSelfNode sender = node_set.get(parcel_set.get(i).get(0));
			RemoteNode receiver = sender.getMemoryController().loadRemoteNode(node_set.get(parcel_set.get(i).get(1)).getAddress());
			sender.sendDataParcel(receiver, "test_message_" + i);
		}
	}

	// Simulation Preferences
	protected final ArrayList<SimSelfNode> node_set = new ArrayList<>();
	protected final ArrayList<ArrayList<Integer>> parcel_set = new ArrayList<>();
	private final SimGUI sim_gui;
}
