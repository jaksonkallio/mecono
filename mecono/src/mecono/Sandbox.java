package mecono;

import java.security.NoSuchAlgorithmException;
import node.Node;

public class Sandbox {
	public void start(){
		System.out.println("Starting sandbox");
		
		try {
			System.out.println("Starting self node");
			
			Self self = Self.generate();
			Node[] others = new Node[5];
			
			others[0].setLabel("Andy");
			others[1].setLabel("Bob");
			others[2].setLabel("Carla");
			others[3].setLabel("Devin");
			others[4].setLabel("Eddie");
			
			others[0].addConnection(others[1]);
			others[0].addConnection(others[2]);
					
		}catch(NoSuchAlgorithmException ex){
			System.out.println("Could not generate self: " + ex.getMessage());
		}
	}
}
