package ui;

import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import mecono.Self;
import node.Node;

public class NodeDashboard extends Stage {

	public NodeDashboard(Self self) {
		this.self = self;
		setTitle("Node Dashboard");
        constructMainContainer();
		setScene(new Scene(main_container, 800, 400));
		show();
	}
    
    private void constructMainContainer(){
        
    }
    
    private Pane main_container;
    private final Self self;
}
