package ui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import mecono.Self;
import mecono.VirtualEnvironment;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;

public class VirtualEnvironmentUI extends Stage {
	public VirtualEnvironmentUI(VirtualEnvironment ve) {
		this.ve = ve;
		setTitle("Virtual Environment Controller");
        constructMainContainer();
		setScene(new Scene(main_container, 500, 500));
		show();
	}
	
	private void constructMainContainer(){
		main_container = new VBox();
		buildSelfList();
		main_container.getChildren().addAll(self_list);
	}
	
	private void selfSelected(){
		Self selected = self_list.getSelectionModel().getSelectedItem();
		
		if(selected != null){
			spawnNodeDashboard(selected);
		}
	}
	
	private void spawnNodeDashboard(Self self){
		NodeDashboard new_node_dash = new NodeDashboard(self);
	}
	
	private void buildSelfList() {
		for (Self self : ve.getSelfList()) {
			self_list_items.add(self);
		}

		self_list.setItems(self_list_items);
		self_list.setPrefWidth(100);

		self_list.getSelectionModel().selectedItemProperty().addListener(event -> {
			selfSelected();
		});
}
	
	private final VirtualEnvironment ve;
	private VBox main_container;
	private final ListView<Self> self_list = new ListView<>();
	private final ObservableList<Self> self_list_items = FXCollections.observableArrayList();
}
