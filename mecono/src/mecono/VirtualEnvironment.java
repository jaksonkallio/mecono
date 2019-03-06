package mecono;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import node.GeoCoord;
import node.Node;

public class VirtualEnvironment {
	public VirtualEnvironment(){
		self_list = new ArrayList<>();
		rng = new Random(RNG_SEED);
	}
	
	public void runSim(){
		int side_count = (int) Math.ceil(Math.sqrt(getNodeCount()));
		int spacing_variance = base_spacing / 2;
		
		try {
			for(int x = 0; x < side_count; x++){
				for(int y = 0; y < side_count; y++){
					if(self_list.size() < getNodeCount()){
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
					self.getSelfNode().addConnection(self.lookupNode(prox.self.getSelfNode().getAddress()));
				}
			}
		} catch(NoSuchAlgorithmException ex) {
			System.out.println("Cannot run simulation: " + ex.getMessage());
		}
	}
    
    public void printBFS(){
        if(self_list.size() > 0){
            printBFS(self_list.get(self_list.size() / 2));
        }
    }
    
    public void printBFS(Self self){
        Queue<Self> check = new LinkedBlockingQueue<>();
        Set<Self> checked = new HashSet<>();
        check.offer(self);
        int n = 0;
        
        while(!check.isEmpty()){
            Self curr = check.poll();
            
            if(checked.contains(curr)){
               continue; 
            }
            
            for(Node neighbor : curr.getSelfNode().getNeighbors()){
                check.offer(lookupSelf(neighbor.getAddress()));
            }
            
            checked.add(curr);
            n++;
        }
        
        System.out.println("Networked nodes: " + n);
        System.out.println("Orphaned nodes: " + (getNodeCount() - n));
    }
	
	public void printSelfList(){
		for(int i = 0; i < self_list.size(); i++){
			System.out.println(i + " @ " + self_list.get(i).getSelfNode().getCoords().toString());
		}
    }
    
    public Self lookupSelf(String address){
        for(Self self : self_list){
            if(self.getSelfNode().getAddress().equals(address)){
                return self;
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
    
    public int getNodeCount(){
        return node_count;
    }
	
    public void setNodeCount(int node_count){
		this.node_count = node_count;
	}
    
	private final static long RNG_SEED = 444555666;
	
	private int node_count;
	private final List<Self> self_list;
	private final int base_spacing = 20;
	private final int neighbor_count = 3;
	private final Random rng;
}
