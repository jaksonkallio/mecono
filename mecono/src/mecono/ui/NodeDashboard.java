/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mecono.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mecono.node.HandshakeHistory;
import mecono.node.Mailbox;
import mecono.node.SelfNode;
import mecono.parceling.DestinationParcel;
import mecono.parceling.Handshake;

/**
 *
 * @author Jakson
 */
public class NodeDashboard extends Stage {
	public NodeDashboard(SelfNode self_node){
		this.self_node = self_node;
		setTitle("Node Dashboard");
		setScene(new Scene(genMainContainer(), 800, 400));
		show();
	}
	
	@Override
	public void close(){
		super.close();
		
		stopGUITimers();
	}
	
	public void stopGUITimers(){
		stopOutboxUpdateTimer();
	}
	
	private VBox genMainContainer(){
		VBox main_container = new VBox();
		Label node_title = new Label("Node: " + self_node.getAddress());
		node_title.setFont(UtilGUI.TITLE_FONT);
		
		main_container.getChildren().addAll(node_title, genTabs());
		
		return main_container;
	}
	
	private TabPane genTabs(){
		TabPane tabs = new TabPane();
		tabs.getTabs().addAll(genOutboxTab(), genComposeTab(), genNodesTab(), genConfigTab());
		tabs.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		return tabs;
	}
	
	private Tab genOutboxTab(){
		TableColumn id_column = new TableColumn("ID");
		TableColumn type_column = new TableColumn("Type");
		TableColumn destination_column = new TableColumn("Destination");
		TableColumn path_column = new TableColumn("Path");
		TableColumn path_online_column = new TableColumn("Path Online?");
		
		id_column.setCellValueFactory(new PropertyValueFactory<DestinationParcel, String>("uniqueID"));
		type_column.setCellValueFactory(new PropertyValueFactory<DestinationParcel, String>("parcelTypeString"));
		destination_column.setCellValueFactory(new PropertyValueFactory<DestinationParcel, String>("destinationAddressString"));
		path_column.setCellValueFactory(new PropertyValueFactory<DestinationParcel, String>("outboundActualPathString"));
		path_online_column.setCellValueFactory(new PropertyValueFactory<DestinationParcel, String>("pathOnlineString"));
		
		outbox_table.getColumns().addAll(id_column, type_column, destination_column, path_column, path_online_column);
		outbox_tab.setContent(outbox_table);
		startOutboxUpdateTimer();
		
		return outbox_tab;
	}
	
	private ObservableList<DestinationParcel> getObservablePendingCollection(){
		HandshakeHistory handshake_history = self_node.getMailbox().getHandshakeHistory();
		List<Handshake> handshakes = handshake_history.getPendingParcels();
		ArrayList<DestinationParcel> pending = new ArrayList<>();
		
		for(Handshake handshake : handshakes){
			pending.add(handshake.getTriggerParcel());
		}
		
		ObservableList<DestinationParcel> observable_pending = FXCollections.observableArrayList(pending);
		
		return observable_pending;
	}
	
	private Tab genComposeTab(){
		return new Tab();
	}
	
	private Tab genNodesTab(){
		return new Tab();
	}
	
	private Tab genConfigTab(){
		return new Tab();
	}
	
	private void updateOutboxTable(){
		if(outbox_tab.isSelected()){
			outbox_table.setItems(getObservablePendingCollection());
		}
	}
	
	private void startOutboxUpdateTimer(){
		outbox_refresh_timer_active = true;
		outbox_refresh_timer.schedule(new TimerTask() {
			public void run() {
				 Platform.runLater(new Runnable() {
					public void run() {
						updateOutboxTable();
					}
				});
			}
		}, 20, 250);
	}
	
	private void stopOutboxUpdateTimer(){
		if(outbox_refresh_timer_active){
			outbox_refresh_timer.cancel();
			outbox_refresh_timer.purge();
			outbox_refresh_timer_active = false;
		}
	}
	
	private SelfNode self_node;
	private Timer outbox_refresh_timer = new java.util.Timer();
	private boolean outbox_refresh_timer_active = false;
	private TableView outbox_table = new TableView();
	private Tab outbox_tab = new Tab("Outbox");
}
