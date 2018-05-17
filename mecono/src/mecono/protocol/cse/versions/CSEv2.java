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
		int[][] parcel_set_raw = {
			{11, 34},
			{90, 41},
			{73, 60},
			{23, 10},
			{45, 9},
			{54, 80},
			{13, 57},
			{95, 28},
			{80, 55},
			{56, 3},
			{10, 98},
			{19, 45},
			{60, 90},
			{84, 63},
			{77, 71},
			{25, 95},
			{40, 73},
			{87, 80},
			{56, 77},
			{48, 6},
			{29, 4},
			{98, 67},
			{57, 64},
			{35, 0},
			{26, 43},
			{11, 10},
			{69, 48},
			{87, 20},
			{73, 59},
			{43, 76},
			{61, 99},
			{66, 88},
			{13, 51},
			{61, 93},
			{73, 31},
			{11, 26},
			{87, 2},
			{47, 17},
			{69, 76},
			{88, 11},
			{95, 3},
			{3, 13},
			{4, 15},
			{73, 96},
			{73, 45},
			{4, 45},
			{85, 16},
			{28, 95},
			{36, 21},
			{3, 31}
		};
		
		for(int i = 0; i < parcel_set_raw.length; i++){
			addSampleParcel(parcel_set_raw[i][0],parcel_set_raw[i][1]);
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
	
	private final int node_count = 100;
	
	// https://www.random.org/integers/?num=50&min=0&max=4&col=1&base=10&format=plain&rnd=new
	// https://delim.co/
	
	// https://www.random.org/integer-sets/?sets=50&num=2&min=0&max=99&commas=on&order=index&format=html&rnd=new
	// http://textmechanic.com/text-tools/basic-text-tools/find-and-replace-text/
}
