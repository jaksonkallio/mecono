/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mecono.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
		
		return tabs;
	}
	
	private Tab genOutboxTab(){
		Tab tab = new Tab("Outbox");
		TableView table = new TableView();
		TableColumn id_column = new TableColumn("ID");
		TableColumn type_column = new TableColumn("Type");
		TableColumn destination_column = new TableColumn("Destination");
		TableColumn path_column = new TableColumn("Path");
		TableColumn path_online_column = new TableColumn("Path Online?");
		
		table.getColumns().addAll(id_column, type_column, destination_column, path_column, path_online_column);
		tab.setContent(table);
		
		return tab;
	}
	
	private ObservableList<Handshake> getObservablePendingCollection(){
		HandshakeHistory handshake_history = self_node.getMailbox().getHandshakeHistory();
		ArrayList<Handshake> pending = (ArrayList) handshake_history.getPendingParcels();
		ObservableList<Handshake> observable_pending = FXCollections.observableArrayList(pending);
		
		return observable_pending;
	}
	
	private Tab genComposeTab(){
		return null;
	}
	
	private Tab genNodesTab(){
		return null;
	}
	
	private Tab genConfigTab(){
		return null;
	}
	
	private SelfNode self_node;
}
