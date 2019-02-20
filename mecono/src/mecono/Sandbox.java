package mecono;

import java.security.NoSuchAlgorithmException;

public class Sandbox {
	public void start(){
		System.out.println("Starting sandbox");
		
		try {
			System.out.println("Starting self node");
			
			Self self = Self.generate();
			
			
		}catch(NoSuchAlgorithmException ex){
			System.out.println("Could not generate self: " + ex.getMessage());
		}
	}
}
