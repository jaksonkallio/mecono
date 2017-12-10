package mecono.node;

import mecono.protocol.SimNetwork;
import org.json.JSONObject;

/**
 *
 * @author jak
 */
public class SimSelfNode extends SelfNode {

	public SimSelfNode(String label, SimNetwork sim_network) {
		super(label);
		this.sim_network = sim_network;
	}

	public void receiveRawString(String raw_parcel) {
		mailbox.getNetworkController().receiveData(raw_parcel);
	}
	
	@Override
	public synchronized String nodeLog(int importance, String message){
		String construct = super.nodeLog(importance, message);
		
		if(sim_network != null && sim_network.getSimGUI() != null){
			sim_network.getSimGUI().appendGlobalConsole(construct);
		}
		
		return construct;
	}

	private boolean adversarial_node = false;
	private SimNetwork sim_network;
}
