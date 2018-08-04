package mecono.protocol.cse.versions;

import mecono.node.NodeAddress;
import mecono.node.SimSelfNode;
import mecono.protocol.cse.SimNetwork;

/**
 *
 * @author sabreok
 */
public class CSEv2 extends SimNetwork {

	@Override
	protected void initEnvironment() {
		createNodes();
		createNeighborships();
		createParcels();
		distributeSampleParcels();
	}
	
	private void createNodes(){
		for(int i = 0; i < node_count; i++){
			node_set.add(new SimSelfNode("n"+i, new NodeAddress("n"+i), this));
		}
	}
	
	private void createParcels(){
		int standard_dest_offset = 7;
		
		if(standard_dest_offset % 2 == 1){
			for(int i = 0; i < PARCEL_COUNT; i++){
				if(i % 2 == 0){
					addSampleParcel(i, ((i + standard_dest_offset) % node_count));
				}
			}
		}
	}
	
	private void createNeighborships(){
		int[][] neighborships = {
			{0,3,1,4,3,1,3,4,1,3,0,3,0,3,3,2,3,1,3,2,1,4,0,3,3,2,0,1,1,3,2,1,0,2,1,2,4,1,4,2,4,1,3,3,0,0,4,4,4,4},
			{3,0,0,0,0,4,0,3,0,1,1,1,0,4,1,1,0,2,0,3,2,0,3,0,4,0,1,3,0,4,4,4,4,1,3,0,0,0,0,3,0,3,0,2,3,1,3,0,3,1},
			{0,4,2,0,0,0,2,0,0,2,3,0,3,0,0,3,1,0,0,4,0,3,1,0,0,3,0,0,0,1,1,3,3,0,2,0,0,0,0,0,2,4,0,0,4,4,4,1,0,3},
			{1,0,0,1,1,0,0,0,3,0,0,0,0,0,0,0,4,0,2,0,0,1,2,4,2,0,3,0,4,0,0,2,0,2,0,4,0,0,1,0,0,0,0,0,1,0,0,2,0,2}
		};
		
		for(int i = 0; i < neighborships.length; i++){
			for(int j = 0; j < neighborships[i].length; j++){
				if(neighborships[i][j] != 0){
					int neighbor_id = (j + neighborships[i][j]) % neighborships[i].length;
					createNeighborship(node_set.get(j), node_set.get(neighbor_id));
				}
			}
		}
	}
	
	private final int node_count = 50;
	
	// https://www.random.org/integers/?num=50&min=0&max=4&col=1&base=10&format=plain&rnd=new
	// https://delim.co/
	
	// https://www.random.org/integer-sets/?sets=50&num=2&min=0&max=99&commas=on&order=index&format=html&rnd=new
	// http://textmechanic.com/text-tools/basic-text-tools/find-and-replace-text/
	
	public final int PARCEL_COUNT = 50;
}
