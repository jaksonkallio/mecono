package mecono.ui;

import mecono.node.SimSelfNode;
import java.util.ArrayList;
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
import javafx.scene.text.Font;
import mecono.node.Neighbor;
import mecono.node.Path;
import mecono.node.PathStats;
import mecono.node.RemoteNode;
import mecono.protocol.SimNetwork;

/**
 *
 * @author jak
 */
public class SimGUI {

	public SimGUI(SimNetwork sim_network) {
		this.sim_network = sim_network;
		buildMainContainer();
	}

	public BorderPane getMainContainer() {
		return main_container;
	}

	public void appendGlobalConsole(String new_line) {
		consoleAppend(global_console, new_line);
	}

	private void nodeSelected() {
		selected_node = node_list.getSelectionModel().getSelectedItem();
		active_node_label.setText("Selected Node " + selected_node.getAddressLabel());
		node_console.setText("");
		appendNodeConsole("Selected node " + selected_node.getAddressLabel() + ".");
	}

	private void appendNodeConsole(String new_line) {
		consoleAppend(node_console, new_line);
	}

	private synchronized void consoleAppend(TextArea console, String new_line) {
		javafx.application.Platform.runLater(() -> console.appendText(new_line + "\n"));
	}

	private void buildMainContainer() {
		main_container.getChildren().clear();
		main_container.setPadding(new Insets(10));

		buildNodeList();
		main_container.setLeft(node_list);
		buildActiveNodeArea();
		main_container.setCenter(active_node_area);
		buildInfoBar();
		main_container.setBottom(info_bar);
	}

	private void buildNodeList() {
		for (SimSelfNode node : sim_network.getMembers()) {
			node_items.add(node);
		}

		node_list.setItems(node_items);
		node_list.setPrefWidth(100);
		node_list.setPrefHeight(400);

		node_list.getSelectionModel().selectedItemProperty().addListener(event -> {
			nodeSelected();
		});
	}

	private void buildActiveNodeArea() {
		global_console.setPrefHeight(200);
		global_console.setEditable(false);
		global_console.setWrapText(true);
		global_console.setFont(console_font);
		node_console.setPrefHeight(200);
		node_console.setEditable(false);
		node_console.setWrapText(true);
		node_console.setFont(console_font);
		active_node_area.setPrefWidth(600);
		active_node_area.setPadding(left_inset);

		get_node_info.setOnAction(event -> {
			ArrayList<Neighbor> neighbors = selected_node.getNeighbors();
			String neighbors_str = "";
			for (Neighbor neighbor : neighbors) {
				if (!neighbors_str.equals("")) {
					neighbors_str += ", ";
				}

				neighbors_str += neighbor.getNode().getAddress();
			}

			String str = "";
			str += "Address: " + selected_node.getAddress();
			str += "\nNeighbors: " + selected_node.getNeighborCount() + " (" + neighbors_str + ")";
			str += "\nSuccessful Sends: " + selected_node.parcelHistoryCount(true);
			str += "\nFailed Sends: " + selected_node.parcelHistoryCount(false);

			appendNodeConsole(str);
		});

		discovered_nodes_button.setOnAction(event -> {
			ArrayList<RemoteNode> discovered_nodes = selected_node.getMemoryController().getNodeMemory();
			StringBuilder discovered_nodes_str = new StringBuilder();

			int discovered_nodes_count = 0;
			for (RemoteNode node : discovered_nodes) {
				int known_paths = 0;

				discovered_nodes_count++;

				discovered_nodes_str.append("-- ");
				discovered_nodes_str.append(node.getAddress());
				discovered_nodes_str.append("\n");

				for (PathStats path : node.getPathsTo()) {
					known_paths++;
					discovered_nodes_str.append("  -- ");
					discovered_nodes_str.append(path.toString());
					discovered_nodes_str.append("\n");
				}

				discovered_nodes_str.append("  -- ");
				discovered_nodes_str.append(known_paths);
				discovered_nodes_str.append(" known paths\n");
			}
			discovered_nodes_str.append("-- ");
			discovered_nodes_str.append(discovered_nodes_count);
			discovered_nodes_str.append(" discovered nodes\n");

			appendNodeConsole(discovered_nodes_str.toString());
		});

		view_outbox.setOnAction(event -> {
			appendNodeConsole(selected_node.getMailbox().listOutbox());
		});

		active_node_actions.getChildren().addAll(get_node_info, discovered_nodes_button, send_from_node, view_outbox, toggle_online);
		active_node_area.getChildren().addAll(global_console, active_node_label, node_console, active_node_actions);
	}

	private void buildInfoBar() {
		sim_stats.setText(sim_network.getStats());
		info_bar.getChildren().addAll(attribution, sim_stats);
		info_bar.setPadding(top_inset);
	}

	private SimNetwork sim_network;
	private BorderPane main_container = new BorderPane();
	private ListView<SimSelfNode> node_list = new ListView<>();
	private ObservableList<SimSelfNode> node_items = FXCollections.observableArrayList();
	private Label active_node_label = new Label("Selected Node");
	private TextArea node_console = new TextArea();
	private TextArea global_console = new TextArea();
	private Button get_node_info = new Button("Node Info");
	private Button discovered_nodes_button = new Button("Discovered Nodes");
	private Button send_from_node = new Button("Send From");
	private Button toggle_online = new Button("Toggle Online");
	private Button view_outbox = new Button("View Outbox");
	private VBox active_node_actions = new VBox(10);
	private VBox active_node_area = new VBox(10);
	private Label attribution = new Label("Made by Jakson Kallio");
	private Label sim_stats = new Label("Statistics loading...");
	private HBox info_bar = new HBox(20);
	private Insets top_inset = new Insets(10, 0, 0, 0);
	private Insets left_inset = new Insets(0, 0, 0, 10);
	private Font console_font = new Font("Monospaced Regular", 12);
	private SimSelfNode selected_node;
}
