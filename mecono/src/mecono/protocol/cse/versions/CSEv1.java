package mecono.protocol.cse.versions;

import java.util.ArrayList;
import mecono.node.NodeAddress;
import mecono.node.RemoteNode;
import mecono.node.SimSelfNode;
import mecono.protocol.cse.SimNetwork;

/**
 *
 * @author jak
 */
public class CSEv1 extends SimNetwork {
	@Override
	public void initEnvironment() {
		ArrayList<SimSelfNode> members = getMembers();
		members.clear();
		String test_addr_suffix = "eee";

		// CSE CSEv1 https://github.com/jaksonkallio/mecono/issues/13
		members.add(new SimSelfNode("Andreas", new NodeAddress("A" + test_addr_suffix), this));
		members.add(new SimSelfNode("Brandon", new NodeAddress("B" + test_addr_suffix), this));
		members.add(new SimSelfNode("Carolyn", new NodeAddress("C" + test_addr_suffix), this));
		members.add(new SimSelfNode("Dominic", new NodeAddress("D" + test_addr_suffix), this));
		members.add(new SimSelfNode("Evelyn", new NodeAddress("E" + test_addr_suffix), this));
		members.add(new SimSelfNode("Finn", new NodeAddress("F" + test_addr_suffix), this));
		members.add(new SimSelfNode("Gerald", new NodeAddress("G" + test_addr_suffix), this));
		members.add(new SimSelfNode("Xavier", new NodeAddress("X" + test_addr_suffix), this));

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

		for (int i = 0; i < test_parcels.length; i++) {
			SimSelfNode sender = members.get(test_parcels[i][0]);
			RemoteNode receiver = sender.getMemoryController().loadRemoteNode(members.get(test_parcels[i][1]).getAddress());
			sender.sendDataParcel(receiver, "test_message_" + i);
		}
	}
}
