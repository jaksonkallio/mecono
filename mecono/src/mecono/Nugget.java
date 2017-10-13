package mecono;

/**
 *
 * @author jak
 */
public class Nugget {
	
	/**
	 * Constructor for when the self is NOT destination.
	 * @param past_history
	 * @param message_piece 
	 */
	public Nugget(Path path_history, String payload){
		this.final_dest = false;
		this.payload = payload;
		
		try {
			setID(id);
		} catch(BadProtocolException ex){
			
		}
	}
	
	/**
	 * Constructor for when the self is the destination.
	 * @param past_history
	 * @param message_piece 
	 */
	public Nugget(NuggetStreamType nstream_type, Path past_history, String message_piece){
		this.final_dest = true;
		this.payload = payload;
		setMessagePiece(message_piece);
		setID(id);
	}
	
	public boolean equals(Object o){
		Nugget other = (Nugget) o;
		return (other.getNStreamParent() == this.getNStreamParent() && other.getID() == this.getID());
	}
	
	public String getMessagePiece(){
		return message_piece;
	}
	
	public int getID(){
		return id;
	}
	
	public void setMessagePiece(String message_piece){
		if(message_piece.length() <= 140){
			this.message_piece = message_piece;
		}else{
			// TODO: Throw ProtocolException
		}
	}
	
	public NuggetStream getNStreamParent(){
		return nstream_parent;
	}
	
	private void setID(int id) throws BadProtocolException{
		if(id >= 1 && id <= Protocol.max_nuggets_per_stream){
			this.id = id;
		}else{
			throw new BadProtocolException("Invalid nugget ID.");
		}
	}
	
	private String message_piece;
	private String payload;
	private int id;
	private Path path_history;
	private final boolean final_dest;
	private NuggetStream nstream_parent;
}
