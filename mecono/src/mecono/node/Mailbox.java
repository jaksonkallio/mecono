package mecono.node;

import mecono.protocol.BadProtocolException;
import mecono.protocol.UnknownResponsibilityException;
import mecono.parceling.Parcel;
import mecono.parceling.ForeignParcel;
import mecono.parceling.MissingParcelDetailsException;
import mecono.parceling.SentParcel;
import mecono.parceling.types.FindParcel;
import mecono.parceling.DestinationParcel;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import mecono.parceling.BadPathException;
import mecono.parceling.DestinationParcel.TransferDirection;
import org.json.JSONObject;

/**
 * The mailbox is responsible for managing parcel sending/receiving, queuing
 * parcels, and giving received parcels to the self node.
 *
 * @author jak
 */
public class Mailbox {

    public Mailbox(SelfNode owner) {
        this.owner = owner;
        this.network_controller = new NetworkController(this);
        this.worker = new MailboxWorker(this);
    }

    /*public boolean sendMessage(ParcelType stream_type, RemoteNode destination, String message_text) {
		Pallet message = new Pallet(this);
		message.createNewMessage(stream_type, destination, message_text);

		return true;
	}*/
    public void receiveParcel(Parcel parcel) {
        if (parcel instanceof DestinationParcel) {
            getOwner().receiveParcel((DestinationParcel) parcel);
			//getOwner().nodeLog(1, "Mailbox received destination parcel");
        } else if (parcel instanceof ForeignParcel) {
            outbound_queue.offer((ForeignParcel) parcel);
			//getOwner().nodeLog(1, "Mailbox received foreign parcel");
        } else if(parcel == null) {
			getOwner().nodeLog(2, "Mailbox received null parcel from network controller");
		} else {
			getOwner().nodeLog(2, "Mailbox received parcel with unknown classification");
		}
    }

    private void enqueueOutbound(ForeignParcel parcel) {
        outbound_queue.offer(parcel);
    }

    /**
     * Gets the owner of the mailbox.
     *
     * @return
     */
    public SelfNode getOwner() {
        return owner;
    }

    public MailboxWorker getWorker() {
        return worker;
    }

    public int getOutboxCount() {
        return outbox.size();
    }

    public NetworkController getNetworkController() {
        return network_controller;
    }

    public void placeInOutbox(DestinationParcel parcel) {
        upon_response_actions.add(parcel.getUponResponseAction());
        parcel.setInOutbox();
        outbox.add(parcel);
    }

    public String listOutbox() {
        String construct = "No parcels in outbox.";

        if (outbox.size() > 0) {
            construct = "";

            for (DestinationParcel parcel : outbox) {
                construct += "\n-- " + parcel.toString();

				/*try {
					if (!parcel.isActualPathKnown()) {
						throw new MissingParcelDetailsException("Missing Path");
					}
				} catch(MissingParcelDetailsException ex){
					construct += ex.getMessage();
				}*/
            }
        }

        return construct;
    }

    public void processOutboxItem(int i) {
        DestinationParcel parcel = outbox.get(i);
		//getOwner().nodeLog(0, "Attemping to send "+parcel.toString());
        RemoteNode destination = (RemoteNode) parcel.getDestination();
		
		try {
			if (parcel.readyToSend()) {
				// Create the response action/expectation
				SentParcel response_action = new SentParcel(this, parcel);

				// Give to the network controller for sending
				try {
					network_controller.sendParcel(parcel.constructForeignParcel());
					parcel.setUsedPath();
					outbox.remove(i);
				} catch (UnknownResponsibilityException | MissingParcelDetailsException | BadProtocolException ex) {
					getOwner().nodeLog(2, "Could not hand off to network controller: " + ex.getMessage());
				}
			} else if (parcel.consultWhenPathUnknown()) {
				consultTrustedForPath(destination);
			}
		} catch(MissingParcelDetailsException | BadProtocolException | BadPathException ex){
			getOwner().nodeLog(2, "Could not send parcel: " + ex.getMessage());
		}
    }

    /**
     * Consult trusted nodes for a path to a specific node.
     *
     * @param node Target node to look for.
     */
    private void consultTrustedForPath(RemoteNode node) {
        ArrayList<RemoteNode> consult_list = new ArrayList<>();

        // A neighbor has an implicitly defined path, so it can never be the target of a search.
        if (!owner.isNeighbor(node)) {
            for (Neighbor neighbor : getOwner().getNeighbors()) {
                // Add every community member to the consult list.
                if (!consult_list.contains(neighbor.getNode())) {
                    consult_list.add(neighbor.getNode());
                }
            }

            for (RemoteNode trusted_node : getOwner().getTrustedNodes()) {
                if (!consult_list.contains(trusted_node)) {
                    // Add all trusted nodes to the consult list.
                    consult_list.add(trusted_node);
                }
            }

            // Now consult the nodes
            for (RemoteNode consultant : consult_list) {
                if (!consultant.equals(node)) {
                    // Only consult a node if the consultant is NOT the node we're looking for.
                    FindParcel find = new FindParcel(this, TransferDirection.OUTBOUND);
                    find.setTarget(node);
					find.setDestination(consultant);

					if (!expectingResponse(find)) {
						owner.nodeLog(1, "Consulting " + find.getDestination().getAddress() + " for path to " + find.getTarget().getAddress());
						placeInOutbox(find);
					}
                }
            }
        }
    }

    /**
     * Checks if there is an active signal out in the network that we are
     * expecting a response to. Used to protect against spamming the network.
     */
    private boolean expectingResponse(DestinationParcel parcel) {
        for (SentParcel existing_action : upon_response_actions) {
            if (existing_action.getOriginalParcel().equals(parcel)) {
                return true;
            }
            /*if (existing_action.getOriginalParcel() instanceof FindParcel) {
				if(((FindParcel) parcel).getTarget().equals(((FindParcel) existing_action.getOriginalParcel()).getTarget()) && parcel.getDestination().equals(existing_action.getOriginalParcel().getDestination())){
					// A find response is a duplicate if 
				}
			}*/
        }

        return false;
    }
	
	public void enqueueInbound(JSONObject serialized_parcel){
		if(serialized_parcel != null){
			inbound_queue.offer(serialized_parcel);
		}
	}
	
	public void processInboundQueue(){
		if(inbound_queue.size() > 0){
			try {
				receiveParcel(Parcel.unserialize(inbound_queue.poll(), getOwner()));
			} catch(MissingParcelDetailsException ex){
				getOwner().nodeLog(2, "Could not receive parcel", ex.getMessage());
			}
		}
	}
	
	public SentParcel getSentParcel(String unique_id){
		for(SentParcel sent_parcel : upon_response_actions){
			if(sent_parcel.getOriginalParcel().getUniqueID().equals(unique_id)){
				return sent_parcel;
			}
		}
		
		return null;
	}
	
    private final SelfNode owner; // The selfnode that runs the mailbox
    private final MailboxWorker worker;
    private ArrayList<SentParcel> upon_response_actions = new ArrayList<>();
    private final NetworkController network_controller;
    private Queue<ForeignParcel> outbound_queue; // Outbound queue
    private ArrayList<DestinationParcel> outbox = new ArrayList<>();
	private Queue<JSONObject> inbound_queue = new LinkedBlockingQueue<>(); 
}
