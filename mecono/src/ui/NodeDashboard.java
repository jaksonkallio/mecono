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
		setScene(new Scene(main_container, 500, 500));
		show();
	}
    
    private void constructMainContainer(){
        main_container = new VBox();
        
        node_log = new TextArea();
        node_log.setPrefRowCount(20);
        node_log.setWrapText(false);
        node_log.setEditable(false);
        
        HBox button_container = new HBox();
        print_outbox_button = new Button("View Outbox");
        print_outbox_button.setOnAction((ActionEvent e) -> {
            self.printOutbox();
        });
        button_container.getChildren().add(print_outbox_button);
        
        main_container.getChildren().addAll(node_log, button_container);
    }
    
    private Button print_outbox_button;
    private TextArea node_log;
    private Pane main_container;
    private final Self self;
}
