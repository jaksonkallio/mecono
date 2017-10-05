package mecono;

/**
 * The mailbox is responsible for managing nugget sending/receiving, queuing nuggets, and piecing together nugget streams and notifying the SelfNode with complete streams.
 * @author jak
 */
public class Mailbox {
	public Mailbox(SelfNode owner){
		this.owner = owner;
	}
	
	public boolean sendMessage(NuggetStreamType stream_type, RemoteNode destination, String message_text) {
		NuggetStream message = new NuggetStream(this);
		message.createNewMessage(stream_type, destination, message_text);
		
		return true;
	}
	
	private SelfNode owner;
}
