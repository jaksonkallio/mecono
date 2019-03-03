package mecono;

public class Sandbox {
	public void start(){
		System.out.println("Starting sandbox");
		VirtualEnvironment ve = new VirtualEnvironment();
		ve.runSim(25);
		ve.printSelfList();
	}
}
