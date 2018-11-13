package mecono.node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import mecono.parceling.BadPathException;
import mecono.parceling.Parcel;
import mecono.parceling.types.PingPayload;
import mecono.protocol.Protocol;
import mecono.ui.UtilGUI;

/**
 *
 * @author jak
 */
public class RemoteNode implements Node {

	public RemoteNode(String address, SelfNode indexer) {
		this.address = address;
		this.indexer = indexer;
	}

	@Override
	public String getAddress() {
		return address;
	}

	@Override
	public String getLabel() {
		return label;
	}

	public boolean isAdversarial() {
		return adversarial;
	}

	public void learnPath(NodeChain path, RemoteNode learned_from) throws BadPathException {
		if (indexer.isNeighbor((RemoteNode) path.getStop(1)) && path.getStop(path.getPathLength() - 1).equals(this)) {
			// If the first stop is the self node, and the last stop is this node, then store
			if (!isPathKnown(path)) {
				// If this path isn't already known
				Path path_stats = new Path(path, indexer, learned_from);
				paths_to.add(path_stats);

				// If this is the first learned path, we should ping this node
				if (paths_to.size() == 1) {
					Parcel parcel = new Parcel(indexer.getMailbox());
					PingPayload payload = new PingPayload();
					parcel.setPayload(payload);
					
					parcel.setDestination(this);
					indexer.getMailbox().getHandshakeHistory().enqueueSend(parcel);
				}
			}
		}
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Node) {
			Node other = (Node) o;

			return other.getAddress().equals(this.getAddress());
		}

		return false;
	}

	public void updateSuccessfulPing(int ping) {
		if (ping > 60000) {
			// If ping is over 60 seconds, ping is shown as "60+ seconds"
			ping = 60000;
		}

		this.ping = ping;
		last_ping_time = Protocol.getEpochMinute();
	}

	public long getLastPinged() {
		if (getIdealPath() == null) {
			return 0;
		}

		return getIdealPath().getLastUse();
	}

	public boolean isOnline() {
		return Protocol.elapsedMillis(getLastPinged()) < ONLINE_THRESHOLD;
	}

	public boolean isReady() {
		// Node is ready when the ideal path exists, and the node is online/offline as per user settings.
		return !((!indexer.ready_when_offline && !isOnline()) || getIdealPath() == null);
	}

	public int countPathsTo() {
		return paths_to.size();
	}

	public ArrayList<Path> getPathsTo() {
		return paths_to;
	}

	public int getLastOnline() {
		return last_ping_time;
	}

	public int getPing() {
		return ping;
	}

	public Path getIdealPath() {
		if (indexer.isNeighbor(this)) {
			ArrayList<Node> stops = new ArrayList<>();
			stops.add(indexer);
			stops.add(this);
			NodeChain direct_path = new NodeChain(stops);
			try {
				learnPath(direct_path, null);
			} catch (BadPathException ex) {
				indexer.nodeLog(2, "Could not learn neighborship", ex.getMessage());
			}
		}

		sortPaths();

		if (countPathsTo() > 0) {
			// Return top path
			return paths_to.get(0);
		} else {
			return null;
		}
	}

	private boolean isPathKnown(NodeChain target) {
		for (Path path : paths_to) {
			if (path.getNodeChain().equals(target)) {
				//indexer.nodeLog(2, "Path already known", target.toString());
				return true;
			}
		}

		return false;
	}

	private void sortPaths() {
		Collections.sort(paths_to, new Comparator<Path>() {
			@Override
			public int compare(Path path2, Path path1) {

				return (int) (1000 * (path2.reliability() - path1.reliability()));
			}
		});
	}

	public String getOnlineString() {
		Path ideal_path = getIdealPath();

		if (ideal_path != null) {
			if (ideal_path.online()) {
				return ideal_path.getPing() + "ms";
			} else {
				return "offline";
			}
		}

		return "unknown path";
	}

	public long getTimeLastConsulted() {
		return last_consulted;
	}

	public void updateTimeLastConsulted() {
		last_consulted = Protocol.getEpochMilliSecond();
	}

	public String getSuccessesString() {
		Path ideal_path = getIdealPath();

		if (ideal_path != null) {
			return "" + ideal_path.successes();
		}

		return "never";
	}

	public String getReliabilityString() {
		Path ideal_path = getIdealPath();

		if (ideal_path != null) {
			return UtilGUI.formatPercentage(getIdealPath().reliability());
		}

		return "untested";
	}

	public String getPinnedString() {
		return UtilGUI.getBooleanString(indexer.isPinned(this));
	}
	
	public void markAssist(){
		assists++;
	}
	
	public int getAssists(){
		return assists;
	}

	public long getSendSeqNum(){
		return send_seq_num;
	}
	
	public void incSendSeqNum(){
		send_seq_num++;
	}
	
	public boolean validRecvSeqNum(long recv_seq_num){
		return recv_seq_num > min_recv_seq_num && !explicit_recv_seq_nums.contains(recv_seq_num);
	}
	
	private void logRecvSeqNum(long recv_seq_num){
		explicit_recv_seq_nums.add(recv_seq_num);
		
		if(explicit_recv_seq_nums.size() > MAX_EXPLICIT_RECV_SEQ_NUMS){
			consolidateRecvSeqNums();
		}
	}
	
	private void consolidateRecvSeqNums(){
		min_recv_seq_num = Collections.max(explicit_recv_seq_nums);
		explicit_recv_seq_nums.clear();
	}
	
	public final static long ONLINE_THRESHOLD = 30000;
	public final static int MAX_EXPLICIT_RECV_SEQ_NUMS = 100;

	private final String address;
	private String label;
	private boolean adversarial; // Flagged as an adversarial node.
	private int ping;
	private long last_consulted; // Time of last consultation for a path
	private int last_ping_time; // Time of the last ping, in minutes.
	private final ArrayList<Path> paths_to = new ArrayList<>();
	private final SelfNode indexer;
	private int assists;
	private long send_seq_num;
	private long min_recv_seq_num;
	private final ArrayList<Long> explicit_recv_seq_nums = new ArrayList<>();
}
