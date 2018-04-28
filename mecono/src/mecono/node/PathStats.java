package mecono.node;

import mecono.parceling.BadPathException;
import mecono.protocol.Protocol;
import mecono.protocol.RNG;

/**
 * A container that holds statistics for a given path.
 *
 * @author jak
 */
public class PathStats {

	public PathStats(Path path, String identifier, SelfNode indexer, RemoteNode learned_from) throws BadPathException {
		this.identifier = identifier;
		this.indexer = indexer;

		validateOutwardPath(path);
		this.path = path;
		this.learned_from = learned_from;
	}

	public PathStats(Path path, SelfNode indexer, RemoteNode learned_from) throws BadPathException {
		this(path, RNG.generateString(5), indexer, learned_from);
	}

	public PathStats(Path path, SelfNode indexer) throws BadPathException {
		this(path, indexer, null);
	}

	public String identifier() {
		return identifier;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof PathStats) {
			PathStats other = (PathStats) o;

			return this.identifier().equals(other.identifier());
		}

		return false;
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("Path [");
		str.append(successes());
		str.append(" success][");
		str.append(failures());
		str.append(" fail][");
		str.append(getPing());
		str.append(" ms]");

		if (getLearnedFrom() != null) {
			str.append("[Learned from: ");
			str.append(getLearnedFrom().getAddress());
			str.append("] ");
		}

		str.append(getPath().toString());

		return str.toString();
	}

	public void markUsed() {
		last_used = Protocol.getEpochSecond();
	}

	public void success() {
		pending--;
		successes++;
		markUsed();
	}

	public void failure() {
		pending--;
		failures++;
	}

	public void pending() {
		pending++;
	}

	public int totalUses() {
		return successes + failures + pending;
	}

	public boolean online() {
		return successes() > 0 && Protocol.elapsedSeconds(last_used) <= PATH_TESTED_EXPIRY;
	}

	public int successes() {
		return successes;
	}

	public int failures() {
		return failures;
	}
	
	public void setPing(long ping){
		this.ping = ping;
	}
	
	public long getPing(){
		return ping;
	}

	private void validateOutwardPath(Path path) throws BadPathException {
		if (path == null) {
			throw new BadPathException("Path is null");
		}

		if (path.getPathLength() < 2) {
			throw new BadPathException("Path " + path.toString() + " is less than two nodes");
		}

		if (!path.getStop(0).equals(indexer)) {
			throw new BadPathException("Indexer is not first node in path " + path.toString());
		}
	}

	public Path getPath() {
		return path;
	}

	public RemoteNode getLearnedFrom() {
		return learned_from;
	}

	public double reliability() {
		double reliability = 0;

		if (totalUses() > 0) {
			if (successes() > 0) {
				// Cooperativity bonus favors nodes that have had a lot of signals sent over them. This gives frequently used paths some slack, and also allows them to improve their reliability over time (up to 100%).
				reliability = (successes() * (1 + PATH_RELIABILITY_BONUS)) / totalUses();
			} else {
				// Only nodes that have had at least one successful signal sent over them get a cooperativity bonus.
				reliability = 0;
			}
		} else {
			// Until we get a good sample size, the cooperativity is constant.
			reliability = 0.25;
		}

		// Cooperativity may never be greater than 100%.
		reliability = Math.min(reliability, 1.00);

		return reliability;
	}

	public static final int PATH_TESTED_EXPIRY = 120; // X minutes since last use before path is marked as not online
	public static final double PATH_RELIABILITY_BONUS = 0.05; // X minutes since last use before path is marked as not online

	private final Path path;
	private int successes = 0;
	private int failures = 0;
	private int pending = 0;
	private long ping;
	private final RemoteNode learned_from;
	private long last_used = 0; // Epoch second timestamp of last successful use
	private final SelfNode indexer;
	private final String identifier;
}
