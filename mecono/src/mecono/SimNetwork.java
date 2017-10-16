package mecono;

import java.util.ArrayList;

/**
 *
 * @author jak
 */
public class SimNetwork {
	
	public void begin(){
		SimSelfNode a = new SimSelfNode("Annie");
		SimSelfNode b = new SimSelfNode("Bob");
		SimSelfNode c = new SimSelfNode("Charlie");
		
		a.getMailbox().listPartialStreams();
		b.getMailbox().listPartialStreams();
		c.getMailbox().listPartialStreams();
		
		a.receiveRawString("111-222-333,encrypteddata");
		
		a.getMailbox().listPartialStreams();
	}
	
	private ArrayList<SimSelfNode> members;
}