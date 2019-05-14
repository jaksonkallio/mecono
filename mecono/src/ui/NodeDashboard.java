package ui;

import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mecono.Self;

public class NodeDashboard extends Stage {

	public NodeDashboard(Self self) {
		this.self = self;
		setTitle("Node Dashboard");
        constructMainContainer();
		this.self.addNodeDashboardListener(this);
		setScene(new Scene(main_container, 500, 500));
		show();
	}
	
	public void appendNodeLog(String message){
		node_log.appendText(message + "\n");
	}
    
    private void constructMainContainer(){
        main_container = new VBox();
        
        node_log = new TextArea();
        node_log.setPrefRowCount(20);
        node_log.setWrapText(false);
        node_log.setEditable(false);
		node_log.setStyle("-fx-font-family: 'monospaced';");
        
        HBox button_container = new HBox();
        print_outbox_button = new Button("View Outbox");
        print_outbox_button.setOnAction((ActionEvent e) -> {
            self.printOutbox();
        });
        button_container.getChildren().add(print_outbox_button);
		
		print_nodes_button = new Button("View Nodes");
        print_nodes_button.setOnAction((ActionEvent e) -> {
            self.printNodes();
        });
        button_container.getChildren().add(print_nodes_button);
        
        main_container.getChildren().addAll(node_log, button_container);
    }
    
    private Button print_outbox_button;
	private Button print_nodes_button;
    private TextArea node_log;
    private Pane main_container;
    private final Self self;
}
