package mecono.node;

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
	
	public String getIdentifier(){
		return identifier;
	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof PathStats){
			PathStats other = (PathStats) o;
			
			return this.getIdentifier().equals(other.getIdentifier());
		}
		
		return false;
	}
	
	public void addSuccess(){
		pending--;
		successes++;
	}
	
	public void addFailure(){
		pending--;
		failures++;
	}
	
	public void addPending(){
		pending++;
	}
	
	public int getTotalUses(){
		return successes + failures + pending;
	}
	
	public Path getPath(){
		return path;
	}
	
	public double getReliability() {
        double cooperativity = 0;

        if (getTotalUses() > 0) {
            if (successes > 0) {
                // Cooperativity bonus favors nodes that have had a lot of signals sent over them. This gives frequently used paths some slack, and also allows them to improve their reliability over time (up to 100%).
                cooperativity = (successes + (getTotalUses() * indexer.path_reliability_rating_bonus)) / getTotalUses();
            } else {
                // Only nodes that have had at least one successful signal sent over them get a cooperativity bonus.
                cooperativity = 0;
            }
        } else {
            // Until we get a good sample size, the cooperativity is constant.
            cooperativity = 0.25;
        }

        // Cooperativity may never be greater than 100%.
        cooperativity = Math.min(cooperativity, 1.00);

        return cooperativity;
    }
	
	private final Path path;
	private int successes = 0;
	private int failures = 0;
	private int pending = 0;
	private final SelfNode indexer;
	private final String identifier;
}
