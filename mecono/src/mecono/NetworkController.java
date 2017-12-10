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

    public void receiveData(String received_parcel_string) {
        Parcel received_parcel;
        JSONObject received_parcel_json = new JSONObject(received_parcel_string);

        try {
			mailbox.getOwner().nodeLog(1, "Parcel received. Attempting to unserialize");
            received_parcel = Parcel.unserialize(received_parcel_json, mailbox.getOwner());
            mailbox.receiveParcel(received_parcel);
        } catch (BadProtocolException | UnknownResponsibilityException ex) {
            mailbox.getOwner().nodeLog(2, "Bad parcel received.");
        }
    }

    public void sendParcel(ForeignParcel parcel) {
        try {
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
                        mailbox.getOwner().nodeLog(4, "Sent " + parcel.toString() + " to " + receiver.getAddressLabel());
                    }
                } else {
                    // TODO: Non-simulation sending routine
                }
            }
        } catch (MissingParcelDetailsException ex) {
            mailbox.getOwner().nodeLog(2, "Could not send parcel: " + ex.getMessage());
        }
    }

    private final Mailbox mailbox;
}
