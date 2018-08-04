package mecono.ui;


import java.util.ArrayList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import mecono.node.SimSelfNode;
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
	
	private Pane genMap(){
		Pane map_canvas = new Pane();
		
		map_nodes.clear();
		for(SimSelfNode node : sim.getNodeSet()){
			map_nodes.add(new MapNode(node));
		}
		
		for(MapNode map_node : map_nodes){
			map_canvas.getChildren().add(map_node.getVisualNode());
		}
		
		return map_canvas;
	}
	
	private class MapNode {
		public MapNode(SimSelfNode node){
			this.node = node;
		}
		
		public boolean equals(Object o){
			if(o instanceof MapNode){
				MapNode other = (MapNode) o;
				
				if(other.getSimSelfNode().equals(getSimSelfNode())){
					return true;
				}
			}
			
			return false;
		}
		
		public SimSelfNode getSimSelfNode(){
			return node;
		}
		
		public int getCellX(){
			int list_index = map_nodes.indexOf(this);
			return list_index % cells_row;
		}
		
		public int getCellY(){
			int list_index = map_nodes.indexOf(this);
			return (int) (list_index / cells_row);
		}
		
		public Pane getVisualNode(){
			Pane stack = new Pane();
			Circle vis_node_circle = new Circle();
			Label vis_node_label = new Label(node.getAddress());
			vis_node_label.setFont(UtilGUI.MICRO_LABEL);
			vis_node_circle.setRadius(node_radius);
			stack.relocate((getCellX() + 1) * node_spacing, (getCellY() + 1) * node_spacing);
			vis_node_circle.setStroke(Color.BLACK);
			vis_node_circle.setFill(Color.WHITE);
			
			stack.getChildren().addAll(vis_node_circle, vis_node_label);
			
			return stack;
		}
		
		private final SimSelfNode node;
	}
	
	private final ArrayList<MapNode> map_nodes = new ArrayList<>();
	private final SimNetwork sim;
	private final int cells_row = 8;
	private final int node_spacing = 40;
	private final int node_radius = 10;
}
