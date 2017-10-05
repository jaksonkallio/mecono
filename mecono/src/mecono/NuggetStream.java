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
