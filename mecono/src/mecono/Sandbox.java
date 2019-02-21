package mecono;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import node.Node;

public class Sandbox {
	public void start(){
		System.out.println("Starting sandbox");
		
		try {
			System.out.println("Starting self node");
			
			self_nodes = new ArrayList<>();
			
			for(int i = 0; i < SIMULATED_NODE_COUNT; i++){
				self_nodes.add(Self.generate());
			}
		
		}catch(NoSuchAlgorithmException ex){
			System.out.println("Could not generate self: " + ex.getMessage());
		}
	}

	public HardwareController lookupHardware(Node node){
		for(Self self : self_nodes){
			if(self.getSelfNode().equals(node)){
				return self.getHardwareController();
			}
		}
		
		return null;
	}
	
	public static final int SIMULATED_NODE_COUNT = 20;
	
	private List<Self> self_nodes;
}
