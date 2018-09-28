package mecono.protocol.cse.versions;

import java.util.Random;
import mecono.node.NodeAddress;
import mecono.node.RemoteNode;
import mecono.node.SimSelfNode;
import mecono.parceling.DestinationParcel.TransferDirection;
import mecono.parceling.ParcelType;
import mecono.parceling.types.DataParcel;
import mecono.protocol.cse.SimNetwork;

/**
 *
 * @author sabreok
 */
public class CSEv2 extends SimNetwork {

	public CSEv2() {
		super();
		initEnvironment();
		startGUI();
	}

	@Override
	protected final void initEnvironment() {
		createNodes();
		createNeighborships();
		refillSampleParcels();
		distributeSampleParcels();
	}

	private void createNodes() {
		for (int i = 0; i < node_count; i++) {
			node_set.add(new SimSelfNode("n" + i, new NodeAddress("n" + i), this));
		}
	}

	public void refillSampleParcels() {
		while (parcelsInOutbox(ParcelType.DATA) < constant_parcel_count) {
			int origin_index = sample_parcel_rand.nextInt(node_set.size());
			int destination_index = sample_parcel_rand.nextInt(node_set.size());

			if (origin_index != destination_index) {
				SimSelfNode origin = node_set.get(origin_index);
				RemoteNode destination = origin.getMemoryController().loadRemoteNode(node_set.get(destination_index).getAddress());
				DataParcel data = new DataParcel(origin.getMailbox(), TransferDirection.OUTBOUND);
				data.setDestination(destination);
				data.setMessage("sample_parcel_" + parcel_counter);
				origin.getMailbox().getHandshakeHistory().enqueueSend(data);
				parcel_counter++;
			}
		}
	}

	private void createNeighborships() {
		for (int i = 0; i < node_set.size(); i++) {
			int neighbor_count = Math.max(min_neighbors, ((Math.abs(neighborship_rand.nextInt())) % max_neighbors));

			for (int j = 0; j < neighbor_count; j++) {
				int neighbor_index = (i + (Math.abs(neighborship_rand.nextInt()) % max_neighbor_distance)) % node_set.size();
				createNeighborship(node_set.get(i), node_set.get(neighbor_index));
			}
		}
	}

	private int parcel_counter = 0;

	private final int node_count = 100;
	private final int max_neighbors = 6;
	private final int min_neighbors = 2;
	private final int max_neighbor_distance = 8;
	private final int constant_parcel_count = 20; // If there are fewer than this many parcels in outboxes, create more
	private final Random neighborship_rand = new Random(158848213);
	private final Random sample_parcel_rand = new Random(181209178);
}
