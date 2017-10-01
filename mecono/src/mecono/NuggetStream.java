package mecono;

import java.util.ArrayList;

/**
 *
 * @author jak
 */
public class NuggetStream {
	
	
	
	private ArrayList<Nugget> nuggets; // The nuggets in the stream
	private int time_sent; // Not trustworthy, but may be helpful
	private int expected_count; // The expected number of nuggets total
	private RemoteNode originator; // Node of the originator
	private RemoteNode destination; // Node of the destination
	private String identifier; // The string used to associate nuggets into one nugget stream.
	private Path path;
	private NuggetStreamType stream_type;
}
