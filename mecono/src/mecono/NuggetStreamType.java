package mecono;

/**
 *
 * @author jak
 */
public enum NuggetStreamType {
	PING, // Pings a node to discover a path, see if it exists/online, or check speed/lag.
	REPORT, // Report misbehavior to community
	DISCOVER,
	DATA // Normal data 
}
