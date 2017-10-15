package mecono;

/**
 *
 * @author jak
 */
public class Nugget {

	/**
	 * Constructor for when the self is NOT destination.
	 *
	 * @param past_history
	 * @param message_piece
	 */
	public Nugget(Path path_history, String payload) {
		this.final_dest = false;
		this.payload = payload;

		updateReceivedTime();
	}

	/**
	 * Constructor for when the self is the destination.
	 *
	 * @param past_history
	 * @param message_piece
	 */
	//pathhistory,[destination,nstreamtype,streamid,originator,nuggetcount,nuggetid,content,signature(destination+originator+streamid+nuggetcount+content)]
	public Nugget(NuggetStream nstream_parent, Path path_history, RemoteNode originator, int id, String message_piece, String signature) throws BadProtocolException {
		this.final_dest = true;

		try {
			this.nstream_parent = nstream_parent;
			this.path_history = path_history;
			setMessagePiece(message_piece);
			setID(id);
		} catch (BadProtocolException ex) {

		}

		updateReceivedTime();
	}

	public void updateReceivedTime() {
		time_received = Protocol.getEpochMinute();
	}

	public int getTimeReceived() {
		return time_received;
	}

	public int age() {
		return Math.max(Protocol.getEpochMinute() - time_received, 0);
	}

	public boolean equals(Object o) {
		Nugget other = (Nugget) o;
		return (other.getNStreamParent() == this.getNStreamParent() && other.getID() == this.getID());
	}

	public String getMessagePiece() {
		return message_piece;
	}

	public int getID() {
		return id;
	}

	public boolean isFinalDest() {
		return final_dest;
	}

	public void setMessagePiece(String message_piece) {
		if (message_piece.length() <= 140) {
			this.message_piece = message_piece;
		} else {
			// TODO: Throw ProtocolException
		}
	}

	public NuggetStream getNStreamParent() {
		return nstream_parent;
	}

	private void setID(int id) throws BadProtocolException {
		if (id >= 1 && id <= Protocol.max_nuggets_per_stream) {
			this.id = id;
		} else {
			throw new BadProtocolException("Invalid nugget ID.");
		}
	}

	private String message_piece;
	private String payload;
	private int id;
	private Path path_history;
	private final boolean final_dest;
	private NuggetStream nstream_parent;
	private int time_received = 0;
	private Mailbox mailbox;
}
