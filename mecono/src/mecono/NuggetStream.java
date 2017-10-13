package mecono;

import java.util.ArrayList;

/**
 *
 * @author jak
 */
public class NuggetStream {
	
	public NuggetStream(Mailbox mailbox){
		this.mailbox = mailbox;
	}
	
	public void createNewMessage(NuggetStreamType stream_type, RemoteNode destination, String message_text){
		this.stream_type = stream_type;
		this.destination = destination;
		this.message_text = message_text;
	}
	
	private void createNuggetsFromString(String message_text){
		nuggets.clear();
		int start_index = 0;
		
		while(start_index < message_text.length()){
			int end_index = start_index + 8;
			
			if(end_index > message_text.length()){
				end_index = message_text.length();
			}
			
			nuggets.add(new Nugget(this, message_text.substring(start_index, end_index)));
			
			start_index += 8;
		}
		
	}
	
	public boolean hasNugget(Nugget target){
		for(Nugget nugget : nuggets){
			if(target.equals(nugget)){
				return true;
			}
		}
		
		return false;
	}
	
	private void buildMessage(){
		message_text = "";
		for(Nugget nugget : nuggets){
			message_text += nugget.getMessagePiece();
		}
	}
	
	private ArrayList<Nugget> nuggets; // The nuggets in the stream
	private int time_sent; // Not trustworthy, but may be helpful
	private int expected_count; // The expected number of nuggets total
	private String message_text;
	private RemoteNode originator; // Node of the originator
	private RemoteNode destination; // Node of the destination
	private String identifier; // The string used to associate nuggets into one nugget stream.
	private Path path;
	private NuggetStreamType stream_type;
	private Mailbox mailbox;
}
