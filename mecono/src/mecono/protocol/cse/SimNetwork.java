package mecono.protocol.cse;

import java.util.ArrayList;
import java.util.Arrays;
import mecono.node.HandshakeHistory;
import mecono.node.Neighbor;
import mecono.node.RemoteNode;
import mecono.node.SimSelfNode;
import mecono.parceling.PayloadType;
import mecono.ui.SimGUI;

/**
 *
 * @author jak
 */
public abstract class SimNetwork {

	public SimNetwork() {
	}

	protected final void startGUI() {
		this.sim_gui = new SimGUI(this);
	}

	public SimGUI getSimGUI() {
		return sim_gui;
	}

	public String getVersionLabel() {
		return this.getClass().getSimpleName();
	}

	public void startMailboxWorkers() {
		if (!isStarted()) {
			is_started = true;
			for (SimSelfNode node : node_set) {
				node.getMailbox().getWorker().startWorking();
				node.nodeLog(0, "Started sim node mailbox worker");
			}
		}
	}

	private void stopMailboxWorkers() {
		for (SimSelfNode node : node_set) {
			node.getMailbox().getWorker().stopWorking();
			node.nodeLog(0, "Stopped sim node mailbox worker");
		}
	}

	public boolean isStarted() {
		return is_started;
	}

	protected abstract void initEnvironment();

	public void refillSampleParcels() {
	}

	;
	
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

	public int parcelsInOutbox(PayloadType parcel_type) {
		int sum = 0;

		for (SimSelfNode node : getNodeSet()) {
			sum += node.getMailbox().getHandshakeHistory().count(false, true, parcel_type);
		}
		
		return sum;
	}

	public double averageSuccessRate(PayloadType parcel_type) {
		int count_success = 0;
		int count_fail = 0;

		for (SimSelfNode node : getNodeSet()) {
			HandshakeHistory handshake_history = node.getMailbox().getHandshakeHistory();
			count_success += handshake_history.count(true, parcel_type);
			count_fail += handshake_history.count(false, parcel_type);
		}

		int denominator = Math.max(1, (count_fail + count_success));
		double rate = ((double) count_success) / ((double) denominator);

		return rate;
	}

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

	public ArrayList<SimSelfNode> getNodeSet() {
		return node_set;
	}

	public int getNodeCount() {
		return getNodeSet().size();
	}

	public ArrayList<ArrayList<Integer>> getParcelSet() {
		return parcel_set;
	}

	public void stop() {
		stopMailboxWorkers();
		sim_gui.stop();
	}

	protected void addSampleParcel(int n1, int n2) {
		Integer[] pair = {n1, n2};
		parcel_set.add(new ArrayList<>(Arrays.asList(pair)));
	}

	protected void distributeSampleParcels() {
		for (int i = 0; i < parcel_set.size(); i++) {
			SimSelfNode sender = node_set.get(parcel_set.get(i).get(0));
			RemoteNode receiver = sender.getMemoryController().loadRemoteNode(node_set.get(parcel_set.get(i).get(1)).getAddress());
			sender.sendDataParcel(receiver, "test_message_" + i);
		}
	}

	// Simulation Preferences
	protected final ArrayList<SimSelfNode> node_set = new ArrayList<>();
	protected final ArrayList<ArrayList<Integer>> parcel_set = new ArrayList<>();
	private SimGUI sim_gui;
	private boolean is_started = false;
}
