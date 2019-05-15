package mecono;

// Author: Jakson Kallio, 2019

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import ui.VirtualEnvironmentUI;

public class Mecono extends Application {
	
	@Override
	public void start(Stage stage) {	
		ve = new VirtualEnvironment();
		ve.setNodeCount(100);
		ve.runSim();
		ve.printSelfList();
		
		ve_ui = new VirtualEnvironmentUI(ve);
		ve_ui.show();
		
		ve_ui.setOnHidden(e -> Platform.exit());
	}
	
	@Override
	public void stop(){
		System.out.println("Mecono stopping");
		ve.stopSim();
	}

	public static void main(String[] args) {
		launch(args);
	}
	
	public static String getVersionString() {
		return "v" + PROTOCOL_VERSION + "." + IMPLEMENTATION_VERSION;
	}
	
	// The implementation version is the current state of the software that interacts with the underlying protocol
	// This includes GUI, simulations, data organization techniques, etc.
	public static final int IMPLEMENTATION_VERSION = 1;

	// The protocol version is the current state of the underlying protocol that connects with other nodes
	// If this is different from another node, they will not be able to communicate at all
	public static final int PROTOCOL_VERSION = 2;
	
	private VirtualEnvironmentUI ve_ui;
	private VirtualEnvironment ve;
}
