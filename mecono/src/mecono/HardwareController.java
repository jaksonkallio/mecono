package mecono;

import java.util.ArrayList;
import java.util.List;
import node.Connection;
import node.InsufficientKnowledgeException;
import node.Node;
import org.json.JSONObject;
import parcel.Parcel;

public class HardwareController {
	public HardwareController(Self self){
		this.self = self;
		port_connections = new ArrayList<>();
	}
	
	public void addConnection(int port, Connection connection){
		PortConnection c = new PortConnection();
		c.port = port;
		c.connection = connection;
		
		if(!port_connections.contains(c)){
			port_connections.add(c);
		}
	}
	
	public void send(JSONObject parcel, Node next) throws InsufficientKnowledgeException {
		
	}
	
	public void receive(JSONObject parcel){
		self.receive(Parcel.deserialize(parcel));
	}
	
	public int getPort(Node node){
		for(PortConnection pc : port_connections){
			if(pc.connection.getOther(self.getSelfNode()).equals(node)){
				return pc.port;
			}
		}
		
		return -1;
	}
	
	private class PortConnection {
		public boolean equals(Object o){
			if(o instanceof PortConnection){
				PortConnection other = (PortConnection) o;
				
				if(this.port == other.port || this.connection.equals(other.connection)){
					return true;
				}
			}
			
			return false;
		}
		
		public int port;
		public Connection connection;
	}

	private final Self self;
	private final List<PortConnection> port_connections;
}
