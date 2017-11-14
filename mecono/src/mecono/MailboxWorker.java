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
	 * Gets whether the worker is working.
	 * @return 
	 */
	public boolean isWorking(){
		return working;
	}
	
	/**
	 * Tells the worker to stop working.
	 */
	public void stopWorking(){
		working = false;
	}
	
	/**
	 * Alias for run.
	 */
	public void startWorking(){
		run();
	}
	
	/**
	 * Main helper loop.
	 */
	@Override
	public void run() {
		working = true;
		int i = 0;
		
		while(working){
			mailbox.processOutboxItem(i);
			
			if(i < mailbox.getOutboxCount()){
				i++;
			}else{
				i = 0;
			}
		}
		
		try{
			Thread.sleep(10);
		} catch (InterruptedException ex) {
			working = false;
		}
	}
	
	private boolean working = false;
	private final Mailbox mailbox;
}
