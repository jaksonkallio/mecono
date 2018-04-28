package mecono.node;

/**
 * The mailbox worker is a threaded helper object that processes the mailbox
 * inbox and outbox.
 *
 * @author sabreok
 */
public class MailboxWorker implements Runnable {

	public MailboxWorker(Mailbox mailbox) {
		this.mailbox = mailbox;
		this.t = new Thread(this);
	}

	/**
	 * Gets whether the worker is working.
	 *
	 * @return
	 */
	public boolean isWorking() {
		return working;
	}

	/**
	 * Tells the worker to stop working.
	 */
	public void stopWorking() {
		working = false;
	}

	/**
	 * Alias for run.
	 */
	public void startWorking() {
		t.start();
	}

	/**
	 * Main helper loop.
	 */
	@Override
	public void run() {
		working = true;
		int i = -1;
		int j = -1;

		while (working) {
			if (i >= 0) {
				mailbox.processOutboxItem(i);
				i--;
			} else {
				i = mailbox.getOutboxCount() - 1;
			}
			
			if (j >= 0) {
				mailbox.cleanSentParcel(j);
				j--;
			} else {
				j = mailbox.getSentParcelCount() - 1;
			}
			
			
			mailbox.processInboundQueue();
			mailbox.processForwardQueue();
			
			try {
				long delay = (long) (((int) (Math.random() * 50)) + 1000 * (1 - mailbox.getOwner().NODE_PERFORMANCE_MODIFIER));
				Thread.sleep(delay);
			} catch (InterruptedException ex) {
				working = false;
			}
		}
	}

	private boolean working = false;
	private final Mailbox mailbox;
	private final Thread t;
}
