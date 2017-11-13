package mecono;

/**
 * The mailbox worker is a threaded helper object that processes the mailbox inbox and outbox.
 * @author sabreok
 */
public class MailboxWorker implements Runnable {
	public MailboxWorker(Mailbox mailbox){
		this.mailbox = mailbox;
	}
	
	/**
	 * Main helper loop.
	 */
	@Override
	public void run() {
		working = true;
		
		while(working){
		
		}
	}
	
	private boolean working = false;
	private final Mailbox mailbox;
}
