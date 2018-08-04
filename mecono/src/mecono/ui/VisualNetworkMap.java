package mecono.ui;


import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mecono.protocol.cse.SimNetwork;

/**
 *
 * @author Jakson
 */
public class VisualNetworkMap extends Stage {
	public VisualNetworkMap(SimNetwork sim){
		this.sim = sim;
		setTitle("Visual Network Map");
		setScene(new Scene(genMainContainer(), 800, 800));
		show();
	}
	
	private VBox genMainContainer(){
		VBox main_container = new VBox();
		main_container.getChildren().addAll(genMap());
		return main_container;
	}
	
	private Canvas genMap(){
		Canvas map_canvas = new Canvas(800,800);
		
		return map_canvas;
	}
	
	private final SimNetwork sim;
}
