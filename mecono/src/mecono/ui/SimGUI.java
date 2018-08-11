package mecono.ui;

import mecono.node.SimSelfNode;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import mecono.parceling.ParcelType;
import mecono.protocol.cse.SimNetwork;

/**
 *
 * @author jak
 */
public class SimGUI {

	public SimGUI(SimNetwork sim_network) {
		this.sim_network = sim_network;
		buildMainContainer();
	}

	public HBox getMainContainer() {
		return main_container;
	}

	public void appendGlobalConsole(String new_line) {
		consoleAppend(global_console, new_line);
	}
	
	public void stop(){
		stopGUITimers();
		closeOpenedNodeDashboards();
	}
	
	public void closeOpenedNodeDashboards(){
		for(NodeDashboard node_dashboard : opened_node_dashboards){
			node_dashboard.close();
		}
	}
	
	public void stopGUITimers(){
		network_stats_refresh_timer.cancel();
		network_stats_refresh_timer.purge();
		
		for(NodeDashboard node_dashboard : opened_node_dashboards){
			node_dashboard.stopGUITimers();
		}
	}

	private void nodeSelected() {
		selected_node = node_list.getSelectionModel().getSelectedItem();
		active_node_label.setText("Selected Node " + selected_node.getAddressLabel());
	}

	private synchronized void consoleAppend(TextArea console, String new_line) {
		javafx.application.Platform.runLater(() -> console.appendText(new_line + "\n"));
	}

	private void buildMainContainer() {
		main_container.getChildren().clear();
		main_container.setPadding(new Insets(10));

		buildNodeList();
		main_container.getChildren().add(column_1);
		buildActiveNodeArea();
		main_container.getChildren().add(active_node_area);
		buildSimNetworkOverview();
		main_container.getChildren().add(columns[2]);
	}

	private void buildNodeList() {
		for (SimSelfNode node : sim_network.getNodeSet()) {
			node_items.add(node);
		}

		node_list.setItems(node_items);
		node_list.setPrefWidth(100);
		node_list.setPrefHeight(400);

		node_list.getSelectionModel().selectedItemProperty().addListener(event -> {
			nodeSelected();
		});
		
		buildInfoBar();
		start_simulation.setOnAction(event -> {
			sim_network.startMailboxWorkers();
		});
		column_1.getChildren().addAll(node_list, info_bar, start_simulation);
	}

	private void buildActiveNodeArea() {
		global_console.setPrefHeight(200);
		global_console.setEditable(false);
		global_console.setWrapText(true);
		global_console.setFont(console_font);
		active_node_area.setPrefWidth(600);
		active_node_area.setPadding(left_inset);
		
		open_node_dashboard.setOnAction(event -> {
			opened_node_dashboards.add(new NodeDashboard(selected_node));
		});
		
		open_visual_network_map.setOnAction(event -> {
			VisualNetworkMap vis_net_map = new VisualNetworkMap(sim_network);
		});

		active_node_actions.getChildren().addAll(open_node_dashboard, open_visual_network_map);
		active_node_area.getChildren().addAll(global_console, active_node_label, active_node_actions);
	}

	private void buildInfoBar() {
		sim_stats.setText(sim_network.getStats());
		info_bar.getChildren().addAll(attribution, sim_stats);
		info_bar.setPadding(top_inset);
	}
	
	private Label generateStatLabel(String label, String value){
		return new Label(label + ": " + value);
	}
	
	private void startSimulatedNetworkStatisticsRefresher(){
		network_stats_refresh_timer.schedule(new TimerTask() {
			public void run() {
				 Platform.runLater(new Runnable() {
					public void run() {
						updateSimulatedNetworkStats();
					}
				});
			}
		}, 20, 250);
	}
	
	private void buildSimNetworkOverview(){
		columns[2].setPrefWidth(300);
		columns[2].setPadding(hor_insets);
		sim_stats.setText("Sim Net Stats");
		columns[2].getChildren().add(sim_stats);
		columns[2].getChildren().add(new Label("Version: " + sim_network.getVersionLabel()));
		columns[2].getChildren().addAll(outbox_count_data, outbox_count_find, outbox_count_ping, success_data_rate, success_find_rate, success_ping_rate);
		startSimulatedNetworkStatisticsRefresher();
	}
	
	private void updateSimulatedNetworkStats(){
		outbox_count_data.setText("Outbox Count (Data): " + sim_network.parcelsInOutbox(ParcelType.DATA));
		outbox_count_find.setText("Outbox Count (Find): " + sim_network.parcelsInOutbox(ParcelType.FIND));
		outbox_count_ping.setText("Outbox Count (Ping): " + sim_network.parcelsInOutbox(ParcelType.PING));
		success_ping_rate.setText("Success Rate (Ping): " + UtilGUI.formatPercentage(sim_network.averageSuccessRate(ParcelType.PING)));
		success_find_rate.setText("Success Rate (Find): " + UtilGUI.formatPercentage(sim_network.averageSuccessRate(ParcelType.FIND)));
		success_data_rate.setText("Success Rate (Data): " + UtilGUI.formatPercentage(sim_network.averageSuccessRate(ParcelType.DATA)));
	}

	private final SimNetwork sim_network;
	private Timer network_stats_refresh_timer = new java.util.Timer();
	private final HBox main_container = new HBox();
	private final VBox column_1 = new VBox();
	private final VBox[] columns = {new VBox(), new VBox(), new VBox()};
	private final ListView<SimSelfNode> node_list = new ListView<>();
	private final ObservableList<SimSelfNode> node_items = FXCollections.observableArrayList();
	private final Label active_node_label = new Label("Selected Node");
	private final TextArea global_console = new TextArea();
	private final Button start_simulation = new Button("Start Simulation");
	private final Button open_node_dashboard = new Button("Open Dashboard");
	private final Button open_visual_network_map = new Button("Open Visual Network Map");
	private final VBox active_node_actions = new VBox(10);
	private final VBox active_node_area = new VBox(10);
	private final Label attribution = new Label("Made by Jakson Kallio");
	private final Label sim_stats = new Label("Simulation Network Statistics");
	private final HBox info_bar = new HBox(20);
	private final Insets top_inset = new Insets(10, 0, 0, 0);
	private final Insets hor_insets = new Insets(0, 10, 0, 10);
	private final Insets left_inset = new Insets(0, 0, 0, 10);
	private final Font console_font = new Font("Monospaced Regular", 12);
	private SimSelfNode selected_node;
	private final ArrayList<NodeDashboard> opened_node_dashboards = new ArrayList<>();
	
	// Simulated network statistics
	private final Label success_data_rate = new Label();
	private final Label success_find_rate = new Label();
	private final Label success_ping_rate = new Label();
	private final Label outbox_count_data = new Label();
	private final Label outbox_count_find = new Label();
	private final Label outbox_count_ping = new Label();
}
