package mecono;

import java.util.ArrayList;
import org.json.JSONObject;

/**
 *
 * @author jak
 */
public class NetworkController {

	public NetworkController(Mailbox mailbox) {
		this.mailbox = mailbox;
	}

	public void receiveData(JSONObject json_parcel) {
		Parcel received_parcel;

		try {
			received_parcel = Parcel.unserialize(json_parcel, mailbox.getOwner());
			mailbox.receiveParcel(received_parcel);
		} catch (BadProtocolException | UnknownResponsibilityException ex) {
			mailbox.getOwner().nodeLog(2, "Bad parcel received.");
		}
	}

	public void sendParcel(Parcel parcel) {
		mailbox.getOwner().nodeLog(1, "Attempting to send parcel over network controller...");
		
		// Serialize the parcel. Serialization includes an encryption process.
		JSONObject serialized_parcel = parcel.serialize();

		// Simulation of the network controller means that we call the receive method on the next nodes network controller.
		RemoteNode remote_receiver = parcel.getNextNode();

		if (Mecono.simulated_network) {
			boolean neighbor_connection_exists = false;

			for (NeighborConnection neighbor : neighbor_connections) {
				if (neighbor.getNeighbor().equals(remote_receiver)) {
					SimSelfNode receiver = SimNetwork.getSelfNodeFromRemoteNode(remote_receiver);
					receiver.getMailbox().getNetworkController().receiveData(serialized_parcel);
					mailbox.getOwner().nodeLog(2, "Sent "+parcel.toString()+" to "+receiver.getAddressLabel());
					break;
				}
			}
		} else {
			// TODO: Non-simulation sending routine
		}
	}

	private void cleanOldNeighborConnections() {
		for (int i = (neighbor_connections.size() - 1); i >= 0; i--) {
			if (neighbor_connections.get(i).getLastUse() > Protocol.elapsedMinutes(mailbox.getOwner().unauthorized_neighborship_expiry)) {
				neighbor_connections.remove(i);
			}
		}
	}

	private class NeighborConnection {

		public NeighborConnection(RemoteNode neighbor, int port) {
			this.neighbor = neighbor;
			this.port = port;
		}

		public RemoteNode getNeighbor() {
			return neighbor;
		}

		public int getPort() {
			return port;
		}

		/**
		 * An authorized neighborship means that the self node acknowledges the
		 * connection with the given address and port combination.
		 *
		 * @return Boolean Whether the connection is authorized.
		 */
		public boolean authorized() {
			return mailbox.getOwner().isNeighbor(neighbor);
		}

		public int getLastUse() {
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
