package mecono.node;

import mecono.protocol.BadProtocolException;
import mecono.protocol.UnknownResponsibilityException;
import mecono.parceling.Parcel;
import mecono.parceling.ForeignParcel;
import mecono.parceling.MissingParcelDetailsException;
import java.util.ArrayList;
import mecono.Mecono;
import mecono.protocol.cse.SimNetwork;
import org.json.JSONObject;

/**
 *
 * @author jak
 */
public class NetworkController {

	public NetworkController(Mailbox mailbox) {
		this.mailbox = mailbox;
	}

	public void receiveData(String received_parcel_string) {
		//Parcel received_parcel;
		JSONObject received_parcel_json = new JSONObject(received_parcel_string);

		//try {
		mailbox.enqueueInbound(received_parcel_json);
		//received_parcel = Parcel.unserialize(received_parcel_json, mailbox.getOwner());
		// mailbox.receiveParcel(received_parcel);
		//} catch (MissingParcelDetailsException ex) {
		//mailbox.getOwner().nodeLog(2, "Could not unserialize received parcel: " + ex.getMessage());
		//mailbox.getOwner().nodeLog(2, "Received parcel JSON: " + received_parcel_json.toString(2));
		// }
	}

	public void sendParcel(ForeignParcel parcel) throws MissingParcelDetailsException, BadProtocolException {
		//mailbox.getOwner().nodeLog(0, "In sendParcel");

		// Serialize the parcel. Serialization includes an encryption process.
		JSONObject serialized_parcel = parcel.serialize();

		// Simulation of the network controller means that we call the receive method on the next nodes network controller.
		RemoteNode remote_receiver = parcel.getNextNode();

		if (remote_receiver != null) {
			if (Mecono.simulated_network) {
				boolean neighbor_connection_exists = false;

				if (mailbox.getOwner().isNeighbor(remote_receiver)) {
					SimSelfNode receiver = SimNetwork.getSelfNodeFromRemoteNode(remote_receiver);
					receiver.getMailbox().getNetworkController().receiveData(serialized_parcel.toString());
					//mailbox.getOwner().nodeLog(4, "Sent " + parcel.toString() + " to " + receiver.getAddressLabel());
				} else {
					throw new MissingParcelDetailsException("Next node is not neighbor");
				}
			} else {
				// TODO: Non-simulation sending routine
				throw new BadProtocolException("Only simulated network is available");
			}
		} else {
			throw new MissingParcelDetailsException("Next node is null");
		}
	}

	private final Mailbox mailbox;
}
