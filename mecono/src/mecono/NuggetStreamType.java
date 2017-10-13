package mecono;

/**
 *
 * @author jak
 */
public enum NuggetStreamType {
	PING, // Check for a nodes online status and latency to the node
	FIND, // Ask community nodes if they know about a path to a specific node
	DATA, // Normal data
	UNKNOWN // Undefined or not known
}
