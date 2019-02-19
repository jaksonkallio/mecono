package mecono;

import java.util.ArrayList;
import java.util.List;
import parcel.Parcel;
import parcel.Response;

public class ConversationArchive {
	public ConversationArchive(){
		conversations = new ArrayList<>();
	}
	
	public void enqueue(Parcel parcel){
		Conversation c = new Conversation();
		c.time_queued = Self.time();
		c.trigger = parcel;
		conversations.add(c);
	}
	
	public void process(Response parcel){
		for(Conversation c : conversations){
			if(c.trigger.isResponse(parcel)){
				c.response = parcel;
				c.time_responded = Self.time();
			}
		}
	}
	
	private class Conversation {
		public boolean responded(){return time_responded > 0;}
		public boolean sent(){return time_sent > 0;}
		public long time_queued;
		public long time_sent;
		public long time_responded;
		public Parcel trigger;
		public Parcel response;
	}
	
	private final List<Conversation> conversations;
}
