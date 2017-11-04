package mecono;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;

/**
 *
 * @author jak
 */
public class SimGUI {
	public SimGUI(SimNetwork sim){
		this.sim = sim;
		buildMainContainer();
	}
	
	public BorderPane getMainContainer(){
		return main_container;
	}
	
	private void nodeSelected(){
		SimSelfNode selected_node = node_list.getSelectionModel().getSelectedItem();
		active_node_label.setText("Selected Node "+selected_node.getAddressLabel());
		node_console.setText("");
	}
	
	private void buildMainContainer(){
		main_container.setPadding(new Insets(10));
		
		buildNodeList();
		main_container.setLeft(node_list);
		buildActiveNodeArea();
		main_container.setCenter(active_node_area);
		buildInfoBar();
		main_container.setBottom(info_bar);
	}
	
	private void buildNodeList(){
		for(SimSelfNode node : sim.getMembers()){
			node_items.add(node);
		}
		
		node_list.setItems(node_items);
		node_list.setPrefWidth(100);
		node_list.setPrefHeight(400);
		
		node_list.getSelectionModel().selectedItemProperty().addListener(event -> {
			nodeSelected();
		});
	}
	
	private void buildActiveNodeArea(){
		node_console.setPrefSize(300, 400);
		node_console.setDisable(true);
		active_node_area.setPrefWidth(400);
		active_node_area.setPadding(left_inset);
		
		active_node_actions.getChildren().addAll(get_node_info, send_from_node, toggle_online);
		active_node_area.getChildren().addAll(active_node_label, node_console, active_node_actions);
	}
	
	private void buildInfoBar(){
		sim_stats.setText(sim.getStats());
		info_bar.getChildren().addAll(attribution, sim_stats);
		info_bar.setPadding(top_inset);
	}

	private SimNetwork sim;
	private BorderPane main_container = new BorderPane();
	private ListView<SimSelfNode> node_list = new ListView<>();
	private ObservableList<SimSelfNode> node_items = FXCollections.observableArrayList();
	private Label active_node_label = new Label("Selected Node");
	private TextArea node_console = new TextArea();
	private Button get_node_info = new Button("Node Info");
	private Button send_from_node = new Button("Send From");
	private Button toggle_online = new Button("Toggle Online");
	private VBox active_node_actions = new VBox(10);
	private VBox active_node_area = new VBox(10);
	private Label attribution = new Label("Made by Jakson Kallio");
	private Label sim_stats = new Label("Statistics loading...");
	private HBox info_bar = new HBox(20);
	private Insets top_inset = new Insets(10, 0, 0, 0);
	private Insets left_inset = new Insets(0, 0, 0, 10);
}
