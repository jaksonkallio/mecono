package mecono;

import java.util.ArrayList;

/**
 *
 * @author jak
 */
public class NuggetStream {

	public NuggetStream(Mailbox mailbox) {
		this.mailbox = mailbox;
	}

	public NuggetStream(Mailbox mailbox, String stream_id) {
		try {
			Protocol.validateNStreamID(stream_id);
		} catch (BadProtocolException ex) {

		}
		this.stream_id = stream_id;
	}
	
	public void importNugget(Nugget nugget){
		if(!hasNugget(nugget)){
			nuggets.add(nugget.getID(), nugget);
		}
	}

	public void createNewMessage(NuggetStreamType stream_type, RemoteNode destination, String message_text) {
		this.nstream_type = stream_type;
		this.destination = destination;
		this.message_text = message_text;
	}
	
	@Override
	public String toString(){
		String str = "";
		str += "Stream ID 0x"+stream_id+"\n";
		str += "- Type: "+nstream_type+"\n";
		str += "- Count: "+getNuggetCount()+" of "+expected_count+"\n";
		str += "- Message: "+buildMessage()+"\n";
		return str;
	}

	private void createNuggetsFromString(String message_text) {
		/*nuggets.clear();
		int start_index = 0;
		
		while(start_index < message_text.length()){
			int end_index = start_index + 8;
			
			if(end_index > message_text.length()){
				end_index = message_text.length();
			}
			
			nuggets.add(new Nugget(this, message_text.substring(start_index, end_index)));
			
			start_index += 8;
		}*/
	}

	public boolean hasNugget(Nugget target) {
		for (Nugget nugget : nuggets) {
			if (target.equals(nugget)) {
				return true;
			}
		}

		return false;
	}

	public String getStreamID() {
		return stream_id;
	}

	public NuggetStreamType getNStreamType() {
		return nstream_type;
	}

	public void setNStreamType(NuggetStreamType nstream_type) {
		this.nstream_type = nstream_type;
	}

	public Nugget getNuggetByIndex(int i){
		return nuggets.get(i);
	}
	
	public int getNuggetCount(){
		return nuggets.size();
	}
	
	public boolean allNuggetsReceived(){
		boolean all_received = true;
		
		for(int i = 0; all_received && i < expected_count; i++){
			boolean found = true;
			
			for(Nugget nugget : nuggets){
				if(nugget.getID() == i){
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
		
		for (Nugget nugget : nuggets) {
			message_text += nugget.getMessagePiece();
		}
		
		return message_text;
	}

	private ArrayList<Nugget> nuggets; // The nuggets in the stream
	private int time_sent; // Not trustworthy, but may be helpful
	private int expected_count; // The expected number of nuggets total
	private String message_text;
	private RemoteNode originator; // Node of the originator
	private RemoteNode destination; // Node of the destination
	private String stream_id; // The string used to associate nuggets into one nugget stream.
	private Path path;
	private NuggetStreamType nstream_type = NuggetStreamType.UNKNOWN;
	private Mailbox mailbox;
}
