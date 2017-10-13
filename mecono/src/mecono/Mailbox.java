package mecono;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

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
	
	public void receiveNugget(String ser_nugget){
		Nugget nugget = unserializeNugget(ser_nugget);
	}
	
	/**
	 * Convert an unencrypted serialized nugget into a Nugget object.
	 * @param ser_nugget
	 * @return Nugget
	 */
	private Nugget unserializeNugget(String ser_nugget) {
		/*
		`[...]` denotes encrypted payload that only destination may access.
		
		nstreamtype,pathhistory,[destination,originator,streamid,nuggetcount,content,signature(destination+originator+streamid+nuggetcount+content)]
		*/
		
		List<String> pieces = Arrays.asList(ser_nugget.split(","));
		if(pieces.get(2) == owner.getAddress()){
			
		}
	}
	
	private SelfNode owner;
	private Set<NuggetStream> partial_nstreams;
}
