package mecono;

/**
 *
 * @author jak
 */
public class Nugget {
	
	public Nugget(NuggetStream parent, String message_segment){
		this.parent = parent;
	}
	
	public String getMessagePiece(){
		return message_piece;
	}
	
	public void setMessagePiece(String message_piece){
		if(message_piece.length() <= 8){
			this.message_piece = message_piece;
		}else{
			// TODO: Throw ProtocolException
		}
	}
	
	private String message_piece;
	private NuggetStream parent;
}
