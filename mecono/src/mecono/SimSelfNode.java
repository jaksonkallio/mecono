package mecono;

/**
 *
 * @author jak
 */
public class SimSelfNode extends SelfNode {
	
	public SimSelfNode(String label) {
		super(label);
	}
	
	public void receiveRawString(String raw_string) {
		mailbox.getNetworkController().receiveData(raw_string);
	}
	
	private boolean adversarial_node = false;
}