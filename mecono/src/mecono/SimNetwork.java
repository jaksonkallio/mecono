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
		// Maximum members for the simulation network
		member_count = Math.min(member_count, 1000);
		this.member_count = member_count;
		
		initializeMembers();
	}
	
	/**
	 * Creates all member nodes.
	 */
	public void initializeMembers() {
	
	}
	
	private final int member_count;
	private ArrayList<SimNode> members;
}