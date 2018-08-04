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
import mecono.node.Neighbor;
import mecono.node.Node;
import mecono.node.RemoteNode;
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
		
		for(MapEdge map_edge : map_edges){
			map_canvas.getChildren().add(map_edge.getEdge());
		}
		
		return map_canvas;
	}
	
	private MapNode lookupMapNode(Node node){
		for(MapNode map_node : map_nodes){
			if(map_node.getSimSelfNode().equals(node)){
				return map_node;
			}
		}
		
		return null;
	}
	
	private class MapNode {
		public MapNode(SimSelfNode node){
			this.node = node;
		}
		
		@Override
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
		
		public int getX(){
			return (getCellX() + 1) * node_hor_spacing;
		}
		
		public int getCellY(){
			int list_index = map_nodes.indexOf(this);
			return (int) (list_index / cells_row);
		}
		
		public int getY(){
			return (getCellY() + 1) * node_ver_spacing;
		}
		
		public Pane getVisualNode(){
			Pane stack = new Pane();
			Circle vis_node_circle = new Circle();
			Label vis_node_label = new Label(node.getAddress());
			vis_node_label.setFont(UtilGUI.MICRO_LABEL);
			vis_node_circle.setRadius(node_radius);
			stack.relocate(getX(), getY());
			vis_node_circle.setStroke(Color.BLACK);
			vis_node_circle.setFill(Color.WHITE);
			
			for(Neighbor neighbor : node.getNeighbors()){
				Node neighbor_node = neighbor.getNode();
				MapEdge map_edge = new MapEdge(this, lookupMapNode(neighbor_node));
				
				if(!map_edges.contains(map_edge)){
					map_edges.add(map_edge);
				}
			}
			
			stack.getChildren().addAll(vis_node_circle, vis_node_label);
			
			return stack;
		}
		
		private final SimSelfNode node;
	}
	
	private class MapEdge {
		public MapEdge(MapNode node1, MapNode node2){
			this.node1 = node1;
			this.node2 = node2;
		}
		
		@Override
		public boolean equals(Object o){
			if(o instanceof MapEdge){
				MapEdge other = (MapEdge) o;
				
				if(other.getNode(1).equals(this.getNode(1)) && other.getNode(2).equals(this.getNode(2))){
					return true;
				}
			}
			
			return false;
		}
		
		public Line getEdge(){
			Line vis_edge = new Line();
			
			vis_edge.setStartX(node1.getX());
			vis_edge.setStartY(node1.getY());
			vis_edge.setEndX(node2.getX());
			vis_edge.setEndY(node2.getY());
			
			return vis_edge;
		}
		
		public MapNode getNode(int i){
			if(i == 1){
				return node1;
			}
			
			return node2;
		}
		
		private final MapNode node1;
		private final MapNode node2;
	}
	
	private final ArrayList<MapNode> map_nodes = new ArrayList<>();
	private final ArrayList<MapEdge> map_edges = new ArrayList<>();
	private final SimNetwork sim;
	private final int cells_row = 8;
	private final int win_width = 800;
	private final int win_height = 800;
	private final int node_hor_spacing = win_width / (cells_row + 1);
	private final int node_ver_spacing = 40;
	private final int node_radius = 10;
}
