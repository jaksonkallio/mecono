package mecono;

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
		RemoteNode receiver = parcel.getNextNode();
		
	}
	
	private Mailbox mailbox;
}
