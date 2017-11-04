package mecono;

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
	public void start(Stage stage){
		Pane root = new Pane();
		
		System.out.println("Mecono started.");
		if(sandbox){
			System.out.println("Running sandbox");
			sandbox();
		}else{
			if(simulated_network){
				System.out.println("Running simulation");
				sim = new SimNetwork();
				sim.begin();
				sim_gui = new SimGUI(sim);
				root = sim_gui.getMainContainer();
			}
        }
		
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {	
		launch();
    }

	private void sandbox(){
		
	}
	
	public static SimNetwork sim;
	public static SimGUI sim_gui;
	public static boolean simulated_network = true;
	public static boolean sandbox = false;
}
