package mecono.node;

import mecono.protocol.Protocol;
import mecono.protocol.RNG;

/**
 * A container that holds statistics for a given path.
 * @author jak
 */
public class PathStats {
	
	public PathStats(Path path, String identifier, SelfNode indexer){
		this.path = path;
		this.identifier = identifier;
		this.indexer = indexer;
	}
	
	public PathStats(Path path, SelfNode indexer){
		this(path, RNG.generateString(5), indexer);
	}
	
	public String identifier(){
		return identifier;
	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof PathStats){
			PathStats other = (PathStats) o;
			
			return this.identifier().equals(other.identifier());
		}
		
		return false;
	}
	
	@Override
	public String toString(){
		return "Path ["+successes()+" s]["+failures()+" f]"+getPath().toString();
	}
	
	public void markUsed(){
		last_used = Protocol.getEpochMinute();
	}
	
	public void success(){
		pending--;
		successes++;
		markUsed();
	}
	
	public void failure(){
		pending--;
		failures++;
	}
	
	public void pending(){
		pending++;
	}
	
	public int totalUses(){
		return successes + failures + pending;
	}
	
	public boolean online(){
		return successes() > 0 && Protocol.elapsedMinutes(last_used) > PATH_TESTED_EXPIRY;
	}
	
	public int successes(){
		return successes;
	}
	
	public int failures(){
		return failures;
	}
	
	public Path getPath(){
		return path;
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
	private int last_used = 0; // Epoch minute timestamp of last successful use
	private final SelfNode indexer;
	private final String identifier;
}