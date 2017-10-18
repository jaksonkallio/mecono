package mecono;

/**
 *
 * @author jak
 */
public class Parcel {
	
	/**
	 * Constructor for when we are just creating a parcel from scratch.
	 */
	public Parcel() {
		this.final_dest = false;
	}

	/**
	 * Constructor for when the self is NOT destination.
	 *
	 * @param path_history
	 * @param payload
	 */
	public Parcel(Path path_history, String payload) {
		this.final_dest = false;
		this.payload = payload;

		updateReceivedTime();
	}

	/**
	 * Constructor for when the self is the destination.
	 *
	 * @param pallet_parent
	 * @param path_history
	 * @param originator
	 * @param id
	 * @param message_piece
	 * @param signature
	 * @throws mecono.BadProtocolException
	 */
	//pathhistory,[destination,pallettype,streamid,originator,nuggetcount,nuggetid,content,signature(destination+originator+streamid+nuggetcount+content)]
	public Parcel(Pallet pallet_parent, Path path_history, RemoteNode originator, int id, String message_piece, String signature) throws BadProtocolException {
		this.final_dest = true;

		try {
			this.pallet_parent = pallet_parent;
			(this.pallet_parent).importParcel(this);
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

	@Override
	public boolean equals(Object o) {
		Parcel other = (Parcel) o;
		return (other.getPalletParent() == this.getPalletParent() && other.getID() == this.getID());
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

	public Pallet getPalletParent() {
		return pallet_parent;
	}
	
	public boolean originatorIsSelf() {
		return path_history.getStop(1).equals(mailbox.getOwner());
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
	private Pallet pallet_parent;
	private int time_received = 0;
	private Mailbox mailbox;
}
