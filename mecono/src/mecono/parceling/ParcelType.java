package mecono.parceling;

/**
 *
 * @author jak
 */
public enum ParcelType {
	PING, // Check for a nodes online status and latency to the node
	PING_RESPONSE, // Responding to a particular node
	FIND, // Ask community nodes if they know about a path to a specific node
	FIND_RESPONSE, // A response with information about requested node.
	DATA, // Normal data
	DATA_RECEIPT, // Receipt that data was received.
	COMMUNITY, // Re-verify neighborships occasionally.
	COMMUNITY_RESPONSE, // Response with the signature.
	UNKNOWN // Undefined or not known
}
