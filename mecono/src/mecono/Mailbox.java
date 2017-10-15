package mecono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * The mailbox is responsible for managing nugget sending/receiving, queuing
 * nuggets, and piecing together nugget streams and notifying the SelfNode with
 * complete streams.
 *
 * @author jak
 */
public class Mailbox {

	public Mailbox(SelfNode owner) {
		this.owner = owner;
	}

	public boolean sendMessage(NuggetStreamType stream_type, RemoteNode destination, String message_text) {
		NuggetStream message = new NuggetStream(this);
		message.createNewMessage(stream_type, destination, message_text);

		return true;
	}

	public void receiveNugget(String ser_nugget) {
		try {
			Nugget nugget = unserializeNugget(ser_nugget);
			if (nugget.isFinalDest()) {
				partial_nstreams.add(nugget.getNStreamParent());
			} else {
				forward_queue.offer(nugget);
			}
		} catch (UnknownResponsibilityException | BadProtocolException ex) {
			owner.nodeLog(2, "Bad nugget received.");
		}
	}

	public NuggetStream getNStreamByID(String stream_id) {
		// Search known streams for the Stream ID.
		for (NuggetStream nstream : partial_nstreams) {
			if (nstream.getStreamID() == stream_id) {
				return nstream;
			}
		}

		// Stream ID not found
		return new NuggetStream(this, stream_id);
	}

	/**
	 * Convert an unencrypted serialized nugget into a Nugget object.
	 *
	 * @param ser_nugget
	 * @return Nugget
	 */
	private Nugget unserializeNugget(String ser_nugget) throws BadProtocolException, UnknownResponsibilityException {
		/*
		`[...]` denotes encrypted payload that only destination may access.
		
		pathhistory,[destination,nstreamtype,streamid,originator,nuggetcount,nuggetid,content,signature(destination+originator+streamid+nuggetcount+content)]
		 */

		List<String> pieces = Arrays.asList(ser_nugget.split(","));
		ArrayList<RemoteNode> path_nodes = new ArrayList<>();
		for (String remote_node_address : pieces.get(0).split("-")) {
			path_nodes.add(SelfNode.getRemoteNode(remote_node_address));
		}
		Path path_history = new Path(path_nodes);

		if (pieces.get(1).equals(owner.getAddress())) {
			// We are the destination

			NuggetStream nstream_parent = getNStreamByID(pieces.get(3));
			if (nstream_parent.getNStreamType() == NuggetStreamType.UNKNOWN) {
				nstream_parent.setNStreamType(Protocol.unserializeNStreamType(pieces.get(2)));
			}

			RemoteNode originator = SelfNode.getRemoteNode(pieces.get(4));

			return new Nugget(nstream_parent, path_history, originator, Integer.parseInt(pieces.get(6)), pieces.get(7), pieces.get(8));
		} else {
			// We are not the destination

			// If selfnode is the second to last node in the path history (last node in history would be the next node) then nugget is in the right place
			if (!path_nodes.get(path_nodes.size() - 2).equals(owner)) {
				throw new UnknownResponsibilityException("SelfNode isn't meant to have this nugget at this point in the path.");
			}

			return new Nugget(path_history, pieces.get(1));
		}
	}

	private SelfNode owner;
	private Set<NuggetStream> partial_nstreams;
	private Queue<Nugget> forward_queue;
}
