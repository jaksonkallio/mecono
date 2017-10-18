package mecono;

import java.util.ArrayList;

/**
 *
 * @author jak
 */
public class Pallet {

	public Pallet(Mailbox mailbox) {
		this.mailbox = mailbox;
	}

	public Pallet(Mailbox mailbox, String stream_id) {
		try {
			Protocol.validatePalletID(stream_id);
		} catch (BadProtocolException ex) {

		}
		this.stream_id = stream_id;
	}
	
	public void importParcel(Parcel parcel){
		if(!hasParcel(parcel)){
			parcels.add(parcel.getID(), parcel);
		}
	}

	public void createNewMessage(PalletType stream_type, RemoteNode destination, String message_text) {
		this.pallet_type = stream_type;
		this.destination = destination;
		this.message_text = message_text;
	}
	
	@Override
	public String toString(){
		String str = "";
		str += "Stream ID 0x"+stream_id+"\n";
		str += "- Type: "+pallet_type+"\n";
		str += "- Count: "+getParcelCount()+" of "+expected_count+"\n";
		str += "- Message: "+buildMessage()+"\n";
		return str;
	}

	private void createParcelsFromString(String message_text) {
		/*parcels.clear();
		int start_index = 0;
		
		while(start_index < message_text.length()){
			int end_index = start_index + 8;
			
			if(end_index > message_text.length()){
				end_index = message_text.length();
			}
			
			parcels.add(new Parcel(this, message_text.substring(start_index, end_index)));
			
			start_index += 8;
		}*/
	}

	public boolean hasParcel(Parcel target) {
		for (Parcel parcel : parcels) {
			if (target.equals(parcel)) {
				return true;
			}
		}

		return false;
	}

	public String getStreamID() {
		return stream_id;
	}

	public PalletType getPalletType() {
		return pallet_type;
	}

	public void setPalletType(PalletType pallet_type) {
		this.pallet_type = pallet_type;
	}

	public Parcel getParcelByIndex(int i){
		return parcels.get(i);
	}
	
	public int getParcelCount(){
		return parcels.size();
	}
	
	public boolean allParcelsReceived(){
		boolean all_received = true;
		
		for(int i = 0; all_received && i < expected_count; i++){
			boolean found = true;
			
			for(Parcel parcel : parcels){
				if(parcel.getID() == i){
					found = true;
				}
			}
			
			if(!found){
				all_received = false;
			}
		}
		
		return all_received;
	}
	
	public String buildMessage() {
		message_text = "";
		
		for (Parcel parcel : parcels) {
			message_text += parcel.getMessagePiece();
		}
		
		return message_text;
	}

	private ArrayList<Parcel> parcels; // The parcels in the stream
	private int time_sent; // Not trustworthy, but may be helpful
	private int expected_count; // The expected number of parcels total
	private String message_text;
	private RemoteNode originator; // Node of the originator
	private RemoteNode destination; // Node of the destination
	private String stream_id; // The string used to associate parcels into one parcel stream.
	private Path path;
	private PalletType pallet_type = PalletType.UNKNOWN;
	private Mailbox mailbox;
}
