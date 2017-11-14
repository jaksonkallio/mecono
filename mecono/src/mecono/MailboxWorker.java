package mecono;

/**
 * The mailbox worker is a threaded helper object that processes the mailbox inbox and outbox.
 * @author sabreok
 */
public class MailboxWorker implements Runnable {
	public MailboxWorker(Mailbox mailbox){
		this.mailbox = mailbox;
		this.t = new Thread(this);
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
		t.start();
	}
	
	/**
	 * Main helper loop.
	 */
	@Override
	public void run() {
		working = true;
		int i = 0;
		
		while(working){
			System.out.println("Attempting to process "+mailbox.getOwner().getAddressLabel()+" #"+i);
			
			if(i < mailbox.getOutboxCount()){
				mailbox.processOutboxItem(i);
				i++;
			}else{
				i = 0;
			}
			
			try{
				Thread.sleep((long) (((int) (Math.random()*100)) + 5000 * (1 - mailbox.getOwner().performance_modifier)));
			} catch (InterruptedException ex) {
				working = false;
			}
		}
	}
	
	private boolean working = false;
	private final Mailbox mailbox;
	private final Thread t;
}
