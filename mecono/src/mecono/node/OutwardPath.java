package mecono.node;

import java.util.ArrayList;

/**
 * An outward path is a specific kind of path where the start point is always the self node and the end point is some remote node. Embedded are statistics about past usage.
 * @author jak
 */
public class OutwardPath extends Path {
	public OutwardPath(ArrayList<Node> stops){
		super(stops);
	}
	
	public int getTotalUses() {
        return getTotalFailures() + getTotalSuccesses();
    }

    public int getTotalSuccesses() {
        return successes;
    }

    public int getTotalFailures() {
        return failures;
    }

    public boolean isTested() {
        return getTotalSuccesses() > 0;
    }

    public double getReliability() {
        double cooperativity = 0;

        if (getTotalUses() > 0) {
            if (successes > 0) {
                // Cooperativity bonus favors nodes that have had a lot of signals sent over them. This gives frequently used paths some slack, and also allows them to improve their reliability over time (up to 100%).
                cooperativity = (successes + (getTotalUses() * relative_origin.path_reliability_rating_bonus)) / getTotalUses();
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

    public int getLastUse() {
        return last_use;
    }
	
	public Path getRawPath(){
		return new Path(getStops());
	}
	
	private SelfNode relative_origin;
    private int successes = 0;
    private int failures = 0;
    private int last_use = 0;

    // TODO: These values should probably be in the self node preferences list
    private final double ideality_cooperativity_component = 0.50; // The cooperativity weight for finding ideality rating of paths.
    private final double ideality_online_count_component = 0.40; // The online count weight for finding ideality rating of paths.
    private final double ideality_trusted_count_component = 0.10; // The trusted node count weight for finding ideality rating of paths.
}
