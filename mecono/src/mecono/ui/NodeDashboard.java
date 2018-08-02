/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mecono.ui;

import java.util.ArrayList;
import java.util.Collection;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mecono.node.SelfNode;

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
		
		main_container.getChildren().addAll(node_title);
		
		return main_container;
	}
	
	private TabPane genTabs(){
		TabPane tabs = new TabPane();
		Collection<Tab> tab_list = new ArrayList<>();
		
		tabs.getTabs().addAll(tab_list);
		
		return tabs;
	}
	
	private Tab genOutboxTab(){
		return null;
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
