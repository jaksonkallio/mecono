package mecono;

import java.util.ArrayList;

/**
 *
 * @author jak
 */
public class SimNetwork {
	
	public SimNetwork() {
		this.member_count = 10;
		
		initializeMembers();
	}
	
	public SimNetwork(int member_count) {
		// Maximum/minimum members for the simulation network
		if (member_count < 2 || member_count > 1000) {
			member_count = 10;
		}
		
		this.member_count = member_count;
		
		initializeMembers();
	}
	
	/**
	 * Creates all member nodes.
	 */
	public void initializeMembers() {
		// Only initialize if there are no members yet
		if (members.isEmpty()) {
			members.add(new SimNode());
		}
	}
	
	private final int member_count;
	private ArrayList<SimNode> members;
}