package mecono;

import mecono.protocol.cse.SimNetwork;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import mecono.protocol.cse.versions.*;

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
			sim.stop();
		}

		System.out.println("Mecono ended successully");

		// Save file
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		for (int i = 0; i < (args.length - 1); i++) {
			System.out.println("Startup parameter: " + args[i] + "=" + args[i + 1]);
			switch (args[i]) {
				case "--cse":
					simulated_network = true;
					cse_version = 1;

					try {
						cse_version = Integer.parseInt(args[i + 1]);
					} catch (NumberFormatException ex) {
						System.out.println("Bad startup parameter");
					}

					break;
			}
		}

		getSimNetwork();

		launch();
	}

	private static void getSimNetwork() {
		switch (cse_version) {
			case 1:
				sim = new CSEv1();
				break;
			case 2:
				sim = new CSEv2();
				break;
			default:
				sim = new CSEv1();
				break;
		}
	}

	public static String getVersion() {
		String v_str = "v" + PROTOCOL_VERSION + "." + IMPLEMENTATION_VERSION;

		if (simulated_network) {
			v_str += " (CSE v" + cse_version + ")";
		}

		return v_str;
	}

	private void sandbox() {

	}

	public static SimNetwork sim;
	public static boolean simulated_network = false;
	public static boolean sandbox = false;
	public static int cse_version;

	// The implementation version is the current state of the software that interacts with the underlying protocol
	// This includes GUI, simulations, data organization techniques, etc.
	public static final int IMPLEMENTATION_VERSION = 1;

	// The protocol version is the current state of the underlying protocol that connects with other nodes
	// If this is different from another node, they will not be able to communicate at all
	public static final int PROTOCOL_VERSION = 1;
}
