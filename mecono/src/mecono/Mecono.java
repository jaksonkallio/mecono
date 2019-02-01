package mecono;

// Author: Jakson Kallio, 2019

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Mecono extends Application {
	
	@Override
	public void start(Stage stage) {
		VBox root = new VBox();
		Scene scene = new Scene(root, 500, 500);
		stage.setTitle("Mecono " + getVersionString());
		stage.setScene(scene);
		stage.show();
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
}
