package mecono;

import mecono.protocol.SimNetwork;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 *
 * @author Jakson Kallio
 */
public class Mecono extends Application {

	@Override
	public void start(Stage stage) {
		Pane root = new Pane();

		System.out.println("Mecono started");
		if (sandbox) {
			System.out.println("Running sandbox...");
			sandbox();
			System.out.println("Sandbox done");
		} else {
			if (simulated_network) {
				System.out.println("Running simulation...");
				stage.setTitle("Simulated Network - Mecono " + Mecono.getVersion());
				root.getChildren().add(sim.getSimGUI().getMainContainer());
				sim.startMailboxWorkers();
			}
		}

		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}

	@Override
	public void stop() {
		System.out.println("Main GUI closing...");

		if (simulated_network) {
			System.out.println("Stopping threads...");
			sim.stopMailboxWorkers();
		}

		System.out.println("Mecono ended successully");

		// Save file
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		launch();
	}

	public static String getVersion() {
		return "v" + protocol_version + "." + version;
	}

	private void sandbox() {

	}

	public static final SimNetwork sim = new SimNetwork();
	public static boolean simulated_network = true;
	public static boolean sandbox = false;
	public static int version = 1;
	public static int protocol_version = 1;
}
