package mecono.node;

import mecono.protocol.cse.SimNetwork;
import org.json.JSONObject;

/**
 *
 * @author jak
 */
public class SimSelfNode extends SelfNode {

	public SimSelfNode(String label, NodeAddress node_address, SimNetwork sim_network) {
		super(label, node_address);
		this.sim_network = sim_network;
	}

	public SimSelfNode(String label, SimNetwork sim_network) {
		super(label, new NodeAddress());
	}

	@Override
	public String toString() {
		return getLabel();
	}

	public void receiveRawString(String raw_parcel) {
		mailbox.getNetworkController().receiveData(raw_parcel);
	}

	@Override
	public synchronized String nodeLog(int importance, String message) {
		String construct = super.nodeLog(importance, message);

		if (sim_network != null && sim_network.getSimGUI() != null && construct != null) {
			sim_network.getSimGUI().appendGlobalConsole(construct);
		}

		return construct;
	}

	public SimNetwork getSimNetwork(){
		return sim_network;
	}
	
	private boolean adversarial_node = false;
	private SimNetwork sim_network;
}
