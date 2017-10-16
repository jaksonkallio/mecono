package mecono;

/**
 *
 * @author jak
 */
public class NetworkController {
	
	public void receiveData(String raw_data){
		mailbox.receiveNugget(raw_data);
	}
	
	private Mailbox mailbox;
}
