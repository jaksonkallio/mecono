package mecono;

import java.util.ArrayList;

/**
 *
 * @author jak
 */
public class Pallet {

	public Pallet(Mailbox mailbox) {
		this.pallet_id = generatePalletID();
	}
	
	public Pallet(Mailbox mailbox, String pallet_id) {
		this.pallet_id = pallet_id;
		
		try {
			Protocol.validatePalletID(pallet_id);
		} catch (BadProtocolException ex) {

		}
	}

	public void updateTimeSent() {
		time_sent = Protocol.getEpochSecond();
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
		str += "Pallet ID "+pallet_id+"\n";
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

	public String getPalletID() {
		return pallet_id;
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
	
	private String generatePalletID() {
		char[] text = new char[pallet_id_length];

		for (int i = 0; i < pallet_id_length; i++) {
			text[i] = Protocol.hex_chars[Protocol.rng.nextInt(Protocol.hex_chars.length)];
		}

		return new String(text);
	}

	private ArrayList<Parcel> parcels; // The parcels in the stream
	private long time_sent; // For self use only, the time sent
	private int expected_count; // The expected number of parcels total
	private String message_text;
	private RemoteNode originator; // Node of the originator
	private RemoteNode destination; // Node of the destination
	private String pallet_id; // The string used to associate parcels into one parcel stream.
	private Path path;
	private PalletType pallet_type = PalletType.UNKNOWN;
	private Mailbox mailbox;
	public static final int pallet_id_length = 4;
}
