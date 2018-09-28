package mecono.node;

import mecono.parceling.ParcelType;
import mecono.parceling.types.DataParcel;
import mecono.parceling.MissingParcelDetailsException;
import java.util.ArrayList;
import mecono.parceling.BadPathException;
import mecono.parceling.DestinationParcel.TransferDirection;
import mecono.protocol.BadProtocolException;

/**
 *
 * @author jak
 */
public class SelfNode implements Node {

	/**
	 * Build a self node using a label and the node address it shall use.
	 *
	 * @param label
	 * @param address
	 */
	public SelfNode(String label, NodeAddress address) {
		this.label = label;
		this.address = address;
		this.memory_controller = new MemoryController(this);
		mailbox = new Mailbox(this);
		nodeLog(0, "SelfNode \"" + getAddressLabel() + "\" started.");
	}

	/**
	 * Build a self node using a label, and the node's address will be generated
	 * randomly.
	 *
	 * @param label
	 */
	public SelfNode(String label) {
		this(label, new NodeAddress());
	}

	/**
	 * Build a self node using a default label and randomly generated address.
	 */
	public SelfNode() {
		this("Unnamed");
	}

	/**
	 * Convert the self node to a string representation.
	 */
	@Override
	public String toString() {
		return getAddressLabel();
	}

	/**
	 * Generate a new node address and set it.
	 */
	public void generateNewAddress() {
		address = new NodeAddress();
		nodeLog(0, "SelfNode \"" + getAddressLabel() + "\" now uses address \"" + getAddress() + "\".");
	}

	/**
	 * Get the node address.
	 */
	@Override
	public String getAddress() {
		return address.getAddressString();
	}

	/**
	 * Get the shortened address label for quick reference.
	 */
	public String getAddressLabel() {
		if (getAddress().length() > 4) {
			return getAddress().substring(0, 4);
		}

		return getAddress();
	}

	/**
	 * Get the label of the node.
	 */
	@Override
	public String getLabel() {
		if (label.equals("")) {
			return getAddress();
		} else {
			return label;
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

	/**
	 * Constructs paths using an isolated path, most likely given by a
	 * FindResponse parcel.
	 *
	 * @param extension
	 * @param learned_from
	 */
	public void learnUsingPathExtension(Path extension, RemoteNode learned_from) {
		/*
		SelfNode knows path...
		SN -> B -> C
		...and asks "C" for a path to "F". One of the responses (`extension` argument) from "C" is...
		C -> D -> E -> F
		SelfNode looks up "C" and all the paths to it. For each known path to C, we can learnPath(known_path + extension)
		 */
		ArrayList<PathStats> paths_to_responder = ((RemoteNode) extension.getStop(0)).getPathsTo();
		for (PathStats path_stats : paths_to_responder) {
			try {
				Path extended_path = new Path(path_stats.getPath(), extension.getSubpath(1, (extension.getPathLength() - 1)));
				nodeLog(ErrorStatus.INFO, LogLevel.VERBOSE, "Attempting to learn extended path", extended_path.toString());

				learnPath(extended_path, learned_from);
			} catch (BadPathException ex) {
				nodeLog(ErrorStatus.FAIL, LogLevel.VERBOSE, "Did not learn path", ex.getMessage());
			}
		}
	}

	public static enum LogLevel {
		VERBOSE, COMMON, ATTENTION, INTERVENE
	}

	public static enum ErrorStatus {
		INFO, FAIL, GOOD
	}

	/**
	 * Log a message to the node's log.
	 *
	 * @param importance
	 * @param log_level
	 * @param message
	 * @return
	 */
	public String nodeLog(ErrorStatus error_status, LogLevel log_level, String message) {
		/*
		Log Levels:
		0 - Extremely verbose details. Example: "didn't learn path", "upon receive parcel timed out"
		1 - Common actions. Example: "sent parcel", "forwarded parcel"
		2 - Attention actions. Example: "Data received by mecono network"
		3 - Intervene-required actions. No examples yet.
		 */
		if (log_level.ordinal() < MIN_LOG_LEVEL) {
			return null;
		}

		String construct = "[" + getAddressLabel() + "][" + error_status.name() + "] " + message;
		System.out.println(construct);

		return construct;
	}

	public String nodeLog(int importance, LogLevel log_level, String message) {
		ErrorStatus error_status = ErrorStatus.INFO;

		if (importance == 0 || importance == 1) {
			error_status = ErrorStatus.INFO;
		}

		if (importance == 2 || importance == 3) {
			error_status = ErrorStatus.FAIL;
		}

		if (importance == 4) {
			error_status = ErrorStatus.GOOD;
		}

		return nodeLog(error_status, log_level, message);
	}

	public String nodeLog(int importance, String message) {
		return nodeLog(importance, LogLevel.COMMON, message);
	}

	public String nodeLog(int importance, String message, String submessage) {
		return nodeLog(importance, message + ": " + submessage);
	}

	public String nodeLog(ErrorStatus error_status, LogLevel log_level, String message, String submessage) {
		return nodeLog(error_status, log_level, message + ": " + submessage);
	}

	/**
	 * Get the node's mailbox.
	 *
	 * @return The node's mailbox.
	 */
	public Mailbox getMailbox() {
		return mailbox;
	}

	/**
	 * Get the node's memory controller.
	 *
	 * @return The node's memory controller.
	 */
	public MemoryController getMemoryController() {
		return memory_controller;
	}

	public void messageReceived(String message) {
		nodeLog(ErrorStatus.GOOD, LogLevel.ATTENTION, "Message received via Mecono network", message);
	}

	/**
	 * Takes in a path and updates all remote node's paths mentioned.
	 *
	 * @param path
	 * @param learned_from
	 * @throws mecono.parceling.BadPathException
	 */
	public void learnPath(Path path, RemoteNode learned_from) throws BadPathException {
		nodeLog(ErrorStatus.INFO, LogLevel.VERBOSE, "Learning path", path.toString());

		// Learning a path is only useful if there are 2+ nodes
		if (path.getPathLength() < 2) {
			throw new BadPathException("Path contains less than two nodes");
		}

		boolean self_node_found = false;
		int count = 0;

		for (int i = 0; i < path.getPathLength(); i++) {
			if (path.getStop(i).equals(this)) {
				// We don't want there to be multiple instances of a self node in a path
				if (self_node_found) {
					throw new BadPathException("Self node included twice in one path");
				}

				self_node_found = true;
			} else {
				if (!self_node_found) {
					count++;
				}
			}
		}

		if (!self_node_found) {
			throw new BadPathException("Self node not included in path");
		}

		if (count == 0) {
			// Self node is already first node, so its organized
			learnOrganizedPath(path, learned_from);
		} else {
			try {
				Path before = path.getSubpath(0, count);
				Path after = path.getSubpath(count, (path.getPathLength() - 1));
				before.reverse();
				learnOrganizedPath(before, learned_from);
				learnOrganizedPath(after, learned_from);
			} catch (BadPathException ex) {
				nodeLog(ErrorStatus.FAIL, LogLevel.VERBOSE, "Cannot learn organized path", ex.getMessage());
			}
		}
	}

	private void learnOrganizedPath(Path path, RemoteNode learned_from) throws BadPathException {
		/*
		SelfNode -> B -> C -> D (Learn a path from SelfNode to D)
		SelfNode -> B -> C
		SelfNode -> B (Will almost always be ignored, since B is a neighbor)
		(Done)
		 */

		if (path.getPathLength() < 2) {
			throw new BadPathException("Path contains less than two nodes");
		}

		if (!path.getStop(0).equals(this)) {
			throw new BadPathException("Self node is not first node in path");
		}

		while (path.getPathLength() >= 2) {
			((RemoteNode) path.getStop(path.getPathLength() - 1)).learnPath(path, learned_from);
			// Get the subpath, which is the same path but with the last node chopped off.
			path = path.getSubpath(path.getPathLength() - 2);
		}
	}

	/**
	 * Sends a data parcel to a given node, containing the given message.
	 *
	 * @param destination
	 * @param message
	 */
	public void sendDataParcel(RemoteNode destination, String message) {
		nodeLog(0, "Attempting to send data: " + message + " to " + destination.getAddress());
		DataParcel parcel = new DataParcel(getMailbox(), TransferDirection.OUTBOUND);
		parcel.setDestination(destination);
		parcel.setMessage(message);
		getMailbox().getHandshakeHistory().enqueueSend(parcel); // Send the response
	}

	/**
	 * Checks if the given neighbor is a neighbor.
	 *
	 * @param neighbor
	 * @param node
	 * @return Neighborship status.
	 */
	public boolean isNeighbor(Neighbor neighbor) {
		// Gets if the node is in the community list at hop 1. (index + 1 = hop)
		return neighbors.contains(neighbor);
	}

	/**
	 * Checks if the given node is a neighbor.
	 *
	 * @param node
	 * @return Neighborship status.
	 */
	public boolean isNeighbor(RemoteNode node) {
		// Gets if the node is in the community list at hop 1. (index + 1 = hop)
		for (Neighbor neighbor : neighbors) {
			if (neighbor.getNode().equals(node)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Get the count of neighbors.
	 *
	 * @return Count of neighbors.
	 */
	public int getNeighborCount() {
		return neighbors.size();
	}

	/**
	 * Add a neighbor.
	 *
	 * @param node
	 */
	public void addNeighbor(Neighbor node) {
		if (!neighbors.contains(node) && !this.equals(node.getNode())) {
			neighbors.add(node);
			node.getNode().getIdealPath();
		}
	}

	/**
	 * Get neighbor list
	 *
	 * @return
	 */
	public ArrayList<Neighbor> getNeighbors() {
		return neighbors;
	}

	/**
	 * Get a list of trusted nodes.
	 *
	 * @return List of trusted nodes.
	 */
	public ArrayList<RemoteNode> getTrustedNodes() {
		return trusted_nodes;
	}

	public ArrayList<RemoteNode> getPinnedNodes() {
		ArrayList<RemoteNode> all_pinned_nodes = pinned_nodes;

		for (Neighbor neighbor : getNeighbors()) {
			all_pinned_nodes.add(neighbor.getNode());
		}

		return all_pinned_nodes;
	}

	/**
	 * Parcel history statistics.
	 *
	 * @param successful
	 * @return The number of successful/failed parcels.
	 */
	public int parcelHistoryCount(boolean successful) {
		int sum = 0;

		for (ParcelType parcel_type : ParcelType.values()) {
			sum += parcelHistoryCount(parcel_type, successful);
		}

		return sum;
	}

	/**
	 * Parcel history statistics for a certain parcel type.
	 *
	 * @param parcel_type
	 * @param successful
	 * @return The number of successful/failed parcels of this type.
	 */
	public int parcelHistoryCount(ParcelType parcel_type, boolean successful) {
		for (HistoricParcelType historic_parcel_type : parcel_type_history) {
			if (historic_parcel_type.getParcelType() == parcel_type) {
				if (successful) {
					return historic_parcel_type.getSuccesses();
				} else {
					return historic_parcel_type.getFailures();
				}

			}
		}

		return 0;
	}

	public boolean isTrusted(RemoteNode node) {
		return trusted_nodes.contains(node);
	}

	public boolean isPinned(RemoteNode node) {
		return isTrusted(node) || pinned_nodes.contains(node);
	}

	private NodeAddress address;
	private String label;
	protected final Mailbox mailbox;
	private MemoryController memory_controller; // The memory controller to load/save different paths, nodes, etc.
	private ArrayList<Neighbor> neighbors = new ArrayList<>();
	private ArrayList<RemoteNode> trusted_nodes = new ArrayList<>();
	private ArrayList<RemoteNode> pinned_nodes = new ArrayList<>();
	private ArrayList<HistoricParcelType> parcel_type_history = new ArrayList<>();

	private class HistoricParcelType {

		public HistoricParcelType(ParcelType parcel_type) {
			this.parcel_type = parcel_type;
		}

		public int getSuccesses() {
			return successes;
		}

		public int getFailures() {
			return fails;
		}

		public int getTotal() {
			return getSuccesses() + getFailures();
		}

		public ParcelType getParcelType() {
			return parcel_type;
		}

		private int successes;
		private int fails;
		private ParcelType parcel_type;
	}

	// Node preferences
	public static final int MIN_LOG_LEVEL = 1;
	public final int offline_successful_ping_threshold = 8; // A successful ping within the last x minutes means the node is online.
	public final int pinned_ping_interval = 4; // How many minutes between each ping to pinned nodes.
	public final boolean ready_when_offline = true; // Should nodes be considered ready even when offline
	public final boolean require_tested_path_before_send = true; // For normal parcels, do we require a tested (path with >1 use) before sending?
	public final int cooperativity_minimum_sample_size = 5; // Cooperativity will be calculated only after total uses is at least X.
	public final double path_reliability_rating_bonus = 0.10;
	public final int MAX_PATH_HIST_LENGTH = 30; // Only forward a foreign parcel if the path history has fewer than this many stops.
	public final long PINNED_NODE_PING_RATE = 120; // Contact pinned nodes every X seconds. Should probably be less than online threshold.
	public final long CONSULTATION_COOLDOWN = 30000; // Time between consultations to reduce spam
	public final boolean FORWARD_WHEN_BLACKLISTED_IN_HISTORY = true;
	public final int SENT_NO_RESPONSE_TIMEOUT = 60; // If no response in X seconds, consider the transfer failed.
	public final int SENT_SUCCESS_TIMEOUT = 30; // After X seconds, clear out an old sent parcel. This will allow a future congruent parcel to be sent.
	public final double NODE_PERFORMANCE_MODIFIER = 0.9; // Percentage, how intense should resource usage be? Higher value means more intense but faster and more dedicated. Lower means slower but less impactful of host system.
}
