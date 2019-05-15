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
import node.AdjacencyList;
import node.AdjacencyList.AdjacencyItem;
import node.Connection;
import static node.Connection.ONLINE_THRESHOLD;
import node.MNode;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;

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
	
	private void visualizeKnowledge(){
		Graph graph = new SingleGraph("Knowledge Visualization ("+self.getSelfNode().getTrimmedAddress()+")");
		
		AdjacencyList knowledge = self.getSelfNode().getGroup(200);
		System.err.println(knowledge.toString());
		
		graph.addAttribute("ui.stylesheet", "url('./assets/graph_visualize_style.css')");

		for(AdjacencyItem item : knowledge){
			Node n1 = graph.addNode(item.source.getTrimmedAddress());
			n1.addAttribute("ui.label", item.source.getTrimmedAddress());
			
			if(item.source.equals(self.getSelfNode())){
				n1.setAttribute("ui.class", "self");
			}
			
			for(MNode target : item.targets){
				Node n2 = graph.addNode(target.getTrimmedAddress());
				n2.addAttribute("ui.label", target.getTrimmedAddress());
				
				Edge e = graph.addEdge(self.genRandomString(5), item.source.getTrimmedAddress(), target.getTrimmedAddress());
				
				Connection e_conn = item.source.getConnection(target);
				if(e_conn != null){
					long elapsed_last_use = e_conn.elapsedLastUse();
					double recent = Math.max(0, (1 - (elapsed_last_use / Connection.ONLINE_THRESHOLD)));
					e.setAttribute("ui.color", recent);
				}
			}
		}
		
		System.setProperty("gs.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
		
		Viewer viewer = graph.display();
		viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.CLOSE_VIEWER);
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
		
		visualize_knowledge_button = new Button("Visualize");
        visualize_knowledge_button.setOnAction((ActionEvent e) -> {
			visualizeKnowledge();
        });
        button_container.getChildren().add(visualize_knowledge_button);
        
        main_container.getChildren().addAll(node_log, button_container);
    }
    
	private String visualize_stylesheet = "node{shape:box;fill-color:black;text-background-mode:plain;text-background-color:black;text-color:#ffffff;}node.self{fill-color:blue;}edge{shape:cubic-curve;fill-mode:dyn-plain;fill-color:#000000, #27AE60;stroke-mode:plain;stroke-width:2;}"; // http://minifycode.com/css-minifier/
    private Button print_outbox_button;
	private Button print_nodes_button;
	private Button visualize_knowledge_button;
    private TextArea node_log;
    private Pane main_container;
    private final Self self;
}
