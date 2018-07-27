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
		int[] counters = new int[3];
		counters[0] = -1; // Outbox items
		counters[1] = -1; // Sent parcel
		counters[2] = -1; // Pinned nodes

		HandshakeHistory handshakes = mailbox.getHandshakeHistory();

		while (working) {
			handshakes.attemptSend();
			handshakes.prune();
			
			if (counters[2] >= 0) {
				mailbox.pingPinnedNode(counters[2]);
				counters[2]--;
			} else {
				counters[2] = mailbox.getPinnedNodeCount() - 1;
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
