package mecono;

import org.json.JSONObject;

/**
 *
 * @author jak
 */
public class SimSelfNode extends SelfNode {
	
	public SimSelfNode(String label) {
		super(label);
	}
	
	public void receiveRawString(JSONObject raw_parcel) {
		mailbox.getNetworkController().receiveData(raw_parcel);
	}
	
	private boolean adversarial_node = false;
}