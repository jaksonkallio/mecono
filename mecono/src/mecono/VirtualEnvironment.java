package mecono;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.PriorityBlockingQueue;
import node.GeoCoord;
import node.Node;

public class VirtualEnvironment {
	public VirtualEnvironment(){
		self_list = new ArrayList<>();
		rng = new Random(RNG_SEED);
	}
	
	public void runSim(int n){
		int side_count = (int) Math.ceil(Math.sqrt(n));
		int spacing_variance = base_spacing / 2;
		
		try {
			for(int x = 0; x < side_count; x++){
				for(int y = 0; y < side_count; y++){
					if(self_list.size() < n){
						Self new_self = Self.generate();
						int new_x = (x + 1) * base_spacing;
						int new_y = (y + 1) * base_spacing;
						new_x += spacing_variance * rng.nextDouble() - (spacing_variance / 2);
						new_y += spacing_variance * rng.nextDouble() - (spacing_variance / 2);
						GeoCoord new_coords = new GeoCoord(new_x, new_y);
						new_self.getSelfNode().setCoords(new_coords);
						self_list.add(new_self);
					}else{
						break;
					}
				}
			}
			
			for(Self self : self_list){
				Queue<ProximityNode> prox_nodes = getProximityNodes(self, neighbor_count);
				
				while(!prox_nodes.isEmpty()){
					ProximityNode prox = prox_nodes.poll();
					self.getSelfNode().addConnection(prox.self.getSelfNode());
					System.out.println("added, dist: "+prox.dist);
				}
			}
		} catch(NoSuchAlgorithmException ex) {
			System.out.println("Cannot run simulation: " + ex.getMessage());
		}
	}
	
	public void printSelfList(){
		for(int i = 0; i < self_list.size(); i++){
			System.out.println(i + " @ " + self_list.get(i).getSelfNode().getCoords().toString());
		}
	}
	
	public HardwareController lookupHardware(Node node){
		for(Self self : self_list){
			if(self.getSelfNode().equals(node)){
				return self.getHardwareController();
			}
		}
		
		return null;
	}
	
	private Queue<ProximityNode> getProximityNodes(Self center, int k){
		Queue<ProximityNode> results = new PriorityBlockingQueue<>(k, Collections.reverseOrder());
		
		for(Self self : self_list){
			if(center == self || center.getSelfNode().equals(self.getSelfNode())){
				continue;
			}
			
			ProximityNode prox = new ProximityNode();
			prox.self = self;
			prox.dist = center.getSelfNode().getCoords().dist(self.getSelfNode().getCoords());
			results.offer(prox);
		
			while(results.size() > k){
				results.remove();
			}
		}
		
		return results;
	}
	
	private class ProximityNode implements Comparable {

		@Override
		public int compareTo(Object o) {
			if(o instanceof ProximityNode){
				ProximityNode other = (ProximityNode) o;
				return (int) (this.dist - other.dist);
			}
			
			return Integer.MAX_VALUE;
		}
		
		public Self self;
		public double dist;
	}
	
	private final static long RNG_SEED = 444555666;
	
	private final List<Self> self_list;
	private final int base_spacing = 20;
	private final int neighbor_count = 3;
	private final Random rng;
}
