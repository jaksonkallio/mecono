package mecono;

import java.util.ArrayList;
import java.util.List;
import node.Connection;

public class HardwareController {
	public HardwareController(){
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

	private final List<PortConnection> port_connections;
}
