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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mecono.node.HandshakeHistory;
import mecono.node.Mailbox;
import mecono.node.MemoryController;
import mecono.node.RemoteNode;
import mecono.node.SelfNode;
import mecono.parceling.Parcel;
import mecono.parceling.Parcel.TransferDirection;
import mecono.parceling.Handshake;
import mecono.parceling.Parcel;
import mecono.parceling.ParcelType;
import mecono.parceling.types.DataParcel;
import mecono.parceling.types.FindParcel;
import mecono.parceling.types.PingParcel;

/**
 *
 * @author Jakson
 */
public class NodeDashboard extends Stage {

	public NodeDashboard(SelfNode self_node) {
		this.self_node = self_node;
		setTitle("Node Dashboard");
		setScene(new Scene(genMainContainer(), 800, 400));
		show();
	}

	@Override
	public void close() {
		super.close();

		stopGUITimers();
	}

	public void stopGUITimers() {
		stopOutboxUpdateTimer();
		stopNodesUpdateTimer();
	}

	private VBox genMainContainer() {
		VBox main_container = new VBox();
		Label node_title = new Label("Node: " + self_node.getAddress());
		node_title.setFont(UtilGUI.TITLE_FONT);

		main_container.getChildren().addAll(node_title, genTabs());

		return main_container;
	}

	private TabPane genTabs() {
		TabPane tabs = new TabPane();
		tabs.getTabs().addAll(genOutboxTab(), genComposeTab(), genNodesTab(), genConfigTab());
		tabs.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		return tabs;
	}

	private Tab genOutboxTab() {
		TableColumn id_column = new TableColumn("ID");
		TableColumn type_column = new TableColumn("Type");
		TableColumn destination_column = new TableColumn("Destination");
		TableColumn path_column = new TableColumn("Path");
		TableColumn path_online_column = new TableColumn("Path Online?");

		id_column.setCellValueFactory(new PropertyValueFactory<Parcel, String>("uniqueID"));
		type_column.setCellValueFactory(new PropertyValueFactory<Parcel, String>("parcelTypeString"));
		destination_column.setCellValueFactory(new PropertyValueFactory<Parcel, String>("destinationAddressString"));
		path_column.setCellValueFactory(new PropertyValueFactory<Parcel, String>("outboundActualPathString"));
		path_online_column.setCellValueFactory(new PropertyValueFactory<Parcel, String>("pathOnlineString"));

		outbox_table.getColumns().addAll(id_column, type_column, destination_column, path_column, path_online_column);
		outbox_tab.setContent(outbox_table);
		startOutboxUpdateTimer();

		return outbox_tab;
	}

	private ObservableList<Parcel> getObservablePendingCollection() {
		HandshakeHistory handshake_history = self_node.getMailbox().getHandshakeHistory();
		List<Handshake> handshakes = handshake_history.getPendingParcels();
		ArrayList<Parcel> pending = new ArrayList<>();

		for (Handshake handshake : handshakes) {
			pending.add(handshake.getTriggerParcel());
		}

		ObservableList<Parcel> observable_pending = FXCollections.observableArrayList(pending);

		return observable_pending;
	}

	private Tab genComposeTab() {
		VBox compose_form = new VBox();
		TextArea arb_message = new TextArea();
		TextField dest_address = new TextField();
		Button send_button = new Button("Send");
		ComboBox<ParcelType> parcel_type_select = new ComboBox<>();

		ArrayList<ParcelType> parcel_type_list = new ArrayList<>();
		parcel_type_list.add(ParcelType.PING);
		parcel_type_list.add(ParcelType.FIND);
		parcel_type_list.add(ParcelType.DATA);
		ObservableList<ParcelType> observable_pending = FXCollections.observableArrayList(parcel_type_list);
		parcel_type_select.getItems().setAll(observable_pending);

		arb_message.setPrefRowCount(1);
		arb_message.setPromptText("Extra Data (only for DATA/FIND parcels)");
		dest_address.setPromptText("Destination node address");

		send_button.setOnAction(event -> {
			if (dest_address.getText().length() > 0 && parcel_type_select.getSelectionModel().getSelectedItem() != null) {
				composeSendMessage(arb_message.getText(), parcel_type_select.getSelectionModel().getSelectedItem(), dest_address.getText());
			}
		});

		compose_form.getChildren().addAll(dest_address, arb_message, parcel_type_select, send_button);
		compose_form.setPadding(UtilGUI.STD_PADDING);
		compose_form.setSpacing(UtilGUI.STD_SPACING);
		compose_tab.setContent(compose_form);

		return compose_tab;
	}

	private void composeSendMessage(String message, ParcelType parcel_type, String dest_address) {
		self_node.nodeLog(SelfNode.ErrorStatus.INFO, SelfNode.LogLevel.ATTENTION, "Message composed", parcel_type.name() + " parcel to " + dest_address + " with data \"" + message + "\"");

		Mailbox mailbox = self_node.getMailbox();
		RemoteNode destination = self_node.getMemoryController().loadRemoteNode(dest_address);

		Parcel parcel = new Parcel(mailbox);

		// Cases where additional payload data is needed
		if (parcel_type == ParcelType.DATA) {
			parcel = new DataParcel(mailbox, TransferDirection.OUTBOUND);
			((DataParcel) parcel).setMessage(message);
		} else if (parcel_type == ParcelType.FIND) {
			parcel = new FindParcel(mailbox, TransferDirection.OUTBOUND);
			RemoteNode target = self_node.getMemoryController().loadRemoteNode(message);
			((FindParcel) parcel).setTarget(target);
		} else if (parcel_type == ParcelType.PING) {
			parcel = new PingParcel(mailbox, TransferDirection.OUTBOUND);
		}

		parcel.setDestination(destination);
		mailbox.getHandshakeHistory().enqueueSend(parcel);
	}

	private Tab genNodesTab() {
		TableColumn address_column = new TableColumn("Address");
		TableColumn ping_column = new TableColumn("Ping");
		TableColumn successes_column = new TableColumn("Success");
		TableColumn reliability_column = new TableColumn("Reliability");
		TableColumn path_count_column = new TableColumn("Path Count");
		TableColumn pinned_column = new TableColumn("Pinned");

		address_column.setCellValueFactory(new PropertyValueFactory<RemoteNode, String>("address"));
		ping_column.setCellValueFactory(new PropertyValueFactory<RemoteNode, String>("onlineString"));
		successes_column.setCellValueFactory(new PropertyValueFactory<RemoteNode, String>("successesString"));
		reliability_column.setCellValueFactory(new PropertyValueFactory<RemoteNode, String>("reliabilityString"));
		path_count_column.setCellValueFactory(new PropertyValueFactory<RemoteNode, String>("pathCountString"));
		pinned_column.setCellValueFactory(new PropertyValueFactory<RemoteNode, String>("pinnedString"));

		nodes_table.getColumns().addAll(address_column, ping_column, successes_column, reliability_column, path_count_column, pinned_column);
		nodes_tab.setContent(nodes_table);
		startNodesUpdateTimer();

		return nodes_tab;
	}

	private ObservableList<RemoteNode> getObservableNodesCollection() {
		return FXCollections.observableArrayList(self_node.getMemoryController().getNodeMemory());
	}

	private Tab genConfigTab() {
		return config_tab;
	}

	private void updateOutboxTable() {
		if (outbox_tab.isSelected()) {
			outbox_table.setItems(getObservablePendingCollection());
		}
	}

	private void updateNodesTable() {
		if (nodes_tab.isSelected()) {
			nodes_table.setItems(getObservableNodesCollection());
		}
	}

	private void startOutboxUpdateTimer() {
		if (!outbox_refresh_timer_active) {
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
	}

	private void startNodesUpdateTimer() {
		if (!nodes_refresh_timer_active) {
			nodes_refresh_timer_active = true;
			nodes_refresh_timer.schedule(new TimerTask() {
				public void run() {
					Platform.runLater(new Runnable() {
						public void run() {
							updateNodesTable();
						}
					});
				}
			}, 20, 250);
		}
	}

	private void stopOutboxUpdateTimer() {
		if (outbox_refresh_timer_active) {
			outbox_refresh_timer.cancel();
			outbox_refresh_timer.purge();
			outbox_refresh_timer_active = false;
		}
	}

	private void stopNodesUpdateTimer() {
		if (nodes_refresh_timer_active) {
			nodes_refresh_timer.cancel();
			nodes_refresh_timer.purge();
			nodes_refresh_timer_active = false;
		}
	}

	private SelfNode self_node;
	private Timer outbox_refresh_timer = new java.util.Timer();
	private Timer nodes_refresh_timer = new java.util.Timer();
	private boolean outbox_refresh_timer_active = false;
	private boolean nodes_refresh_timer_active = false;
	private TableView outbox_table = new TableView();
	private TableView nodes_table = new TableView();
	private Tab outbox_tab = new Tab("Outbox");
	private Tab compose_tab = new Tab("Compose");
	private Tab nodes_tab = new Tab("Nodes");
	private Tab config_tab = new Tab("Configuration");
}
