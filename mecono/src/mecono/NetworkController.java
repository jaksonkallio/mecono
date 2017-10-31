package mecono;

import java.util.ArrayList;

/**
 *
 * @author jak
 */
public class NetworkController {
	
	public NetworkController(Mailbox mailbox){
		this.mailbox = mailbox;
	}
	
	public void receiveData(String raw_data){
		mailbox.receiveParcel(raw_data);
	}
	
	public void sendParcel(Parcel parcel){
		// Serialize the parcel. Serialization includes an encryption process.
		String serialized_parcel = parcel.serialize();
		
		// Simulation of the network controller means that we call the receive method on the next nodes network controller.
		RemoteNode remote_receiver = parcel.getNextNode();
		
		if(Mecono.simulated_network){
			
			SimSelfNode receiver = SimNetwork.getSelfNodeFromRemoteNode(remote_receiver);
			receiver.getMailbox().getNetworkController().receiveData(serialized_parcel);
		}else{
			// TODO: Non-simulation sending routine
		}
	}
	
	private void cleanOldNeighborConnections(){
		for(int i = (neighbor_connections.size() - 1); i >= 0; i--){
			if(neighbor_connections.get(i).getLastUse() > Protocol.elapsedMinutes(mailbox.getOwner().unauthorized_neighborship_expiry)){
				neighbor_connections.remove(i);
			}
		}
	}
	
	private class NeighborConnection {
		public NeighborConnection(RemoteNode neighbor, int port){
			this.neighbor = neighbor;
			this.port = port;
		}
		
		public RemoteNode getNeighbor(){
			return neighbor;
		}
		
		public int getPort(){
			return port;
		}
		
		/**
		 * An authorized neighborship means that the self node acknowledges the connection with the given address and port combination. 
		 * @return Boolean Whether the connection is authorized.
		 */
		public boolean authorized(){
			return mailbox.getOwner().isNeighbor(neighbor);
		}
		
		public int getLastUse(){
			return last_use;
		}
		
		private RemoteNode neighbor;
		private int last_use;
		// TODO: Represent port correctly, it'll probably be something like a network card identifier in practice.
		private int port;
	}
	
	private final Mailbox mailbox;
	private ArrayList<NeighborConnection> neighbor_connections;
}
