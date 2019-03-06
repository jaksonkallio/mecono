package mecono;

public class Sandbox {
	public void start(){
		System.out.println("Starting sandbox");
		VirtualEnvironment ve = new VirtualEnvironment();
		ve.setNodeCount(100);
		ve.runSim();
		ve.printSelfList();
        ve.printBFS();
	}
}
