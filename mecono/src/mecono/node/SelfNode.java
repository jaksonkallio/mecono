package mecono.node;

import mecono.protocol.UnknownResponsibilityException;
import mecono.parceling.ParcelType;
import mecono.parceling.types.DataParcel;
import mecono.parceling.MissingParcelDetailsException;
import mecono.parceling.DestinationParcel;
import java.util.ArrayList;
import mecono.parceling.BadPathException;
import mecono.parceling.DestinationParcel.TransferDirection;
import mecono.parceling.types.FindParcel;
import mecono.parceling.types.FindResponseParcel;

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
        return getAddress().substring(0, 4);
    }

    /**
     * Get the label of the node.
     */
    @Override
    public String getLabel() {
		if(label.equals("")){
			return getAddress();
		}else{
			return label;
		}
    }

    /**
     * Receive and process a destination parcel from the mecono network.
     *
     * @param parcel The parcel received.
     */
    public void receiveParcel(DestinationParcel parcel) {
        nodeLog(1, "Data received via mecono network: " + parcel.toString());
		
		try {
			learnPath(parcel.getActualPath());
			
			if(parcel instanceof FindParcel){
				RemoteNode originator = (RemoteNode) parcel.getOriginator();
				
				if(((FindParcel) parcel).getTarget() == null){
					throw new MissingParcelDetailsException("Unknown find target");
				}
				
				FindResponseParcel response = new FindResponseParcel(getMailbox(), TransferDirection.OUTBOUND);
				RemoteNode target = ((FindParcel) parcel).getTarget();
				ArrayList<Path> available_paths = Path.convertToRawPaths(target.getPathsTo());
				response.setTargetAnswers(available_paths); // Set response to our answer
				response.setDestination(originator); // Set the destination to the person that contacted us (a response)
				response.placeInOutbox(); // Send the response
			}else{
				throw new MissingParcelDetailsException("Unknown parcel type");
			}
		} catch(MissingParcelDetailsException ex){
			nodeLog(2, "Could not handle received parcel: " + ex.getMessage());
		} catch(UnknownResponsibilityException ex){
			nodeLog(2, "Unknown responsibility when sending response: " + ex.getMessage());
		} catch(BadPathException ex){
			nodeLog(2, "Cannot learn path from received parcel: " + ex.getMessage());
		}
    }

    /**
     * Log a message to the node's log.
     *
     * @param importance
     * @param message
     * @return
     */
    public String nodeLog(int importance, String message) {
        String[] importance_levels = {"INFO", "NOTE", "WARN", "CRIT", "GOOD"};

        String construct = "";
        if (importance <= (importance_levels.length - 1)) {
            construct = "[" + getLabel().substring(0, 4) + "][" + importance_levels[importance] + "] " + message;
            System.out.println(construct);
        }

        return construct;
    }
	
	public String nodeLog(int importance, String message, String submessage) {
		return nodeLog(importance, message + ": " + submessage);
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

    /**
     * Takes in a path and updates all remote node's paths mentioned.
     *
     * @param path
	 * @throws mecono.parceling.BadPathException
     */
    public void learnPath(Path path) throws BadPathException {
		// TODO: Fix to work with OutwardPath system. Given a path, find/construct useful outward paths.
		
		// Learning a path is only useful if there are 2+ nodes
		if(path.getPathLength() < 2){
			throw new BadPathException("Path contains less than two nodes");
		}
		
		boolean self_node_found = false;
		int count = 0;

		for(int i = 0; i < path.getPathLength(); i++){
			if(path.getStop(i).equals(this)){
				// We don't want there to be multiple instances of a self node in a path
				if(self_node_found){
					throw new BadPathException("Self node included twice in one path");
				}

				self_node_found = true;
			}else{
				count++;
			}
		}

		if(!self_node_found){
			throw new BadPathException("Self node not included in path");
		}

		if(count == 0){
			// Base case, the self node is the first node in the path
			// Learn every subsequent path, like:
			/*
			SelfNode -> B -> C -> D (Learn a path from SelfNode to D)
			SelfNode -> B -> C
			SelfNode -> B (Will almost always be ignored, since B is a neighbor)
			(Done)
			*/ 
			while (path.getPathLength() >= 2) {
                ((RemoteNode) path.getStop(path.getPathLength() - 1)).learnPath(path);
                // Get the subpath, which is the same path but with the last node chopped off.
                path = path.getSubpath(path.getPathLength() - 2);
            }
		}else{
			Path before = path.getSubpath(0, count);
			Path after = path.getSubpath(count, path.getPathLength());

			before.reverse();
			learnPath(before);
			learnPath(after);
		}
        /*if (path.getStop(0).equals(this)) {
            // Verify that stop 0 is the self node
            Path working_path = path;

            while (working_path.getPathLength() > 1) {
                ((RemoteNode) working_path.getStop(working_path.getPathLength() - 1)).learnPath(working_path);
                // Get the subpath, which is the same path but with the last node chopped off.
                working_path = working_path.getSubpath(working_path.getPathLength() - 2);
            }
        }*/
    }

    /**
     * Sends a data parcel to a given node, containing the given message.
     *
     * @param destination
     * @param message
     */
    public void sendDataParcel(RemoteNode destination, String message) {
        try {
			nodeLog(0, "Attempting to send data: " + message + " to " + destination.getAddress());
            DataParcel parcel = new DataParcel(getMailbox(), TransferDirection.OUTBOUND);
            parcel.setDestination(destination);
            parcel.setMessage(message);
            parcel.placeInOutbox();
        } catch (UnknownResponsibilityException | MissingParcelDetailsException ex) {
            nodeLog(2, "Cannot send data parcel: " + ex.getMessage());
        }
    }

    /**
     * Checks if the given neighbor is a neighbor.
     *
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
        neighbors.add(node);
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
    public final int offline_successful_ping_threshold = 8; // A successful ping within the last x minutes means the node is online.
    public final int pinned_ping_interval = 4; // How many minutes between each ping to pinned nodes.
    public final boolean ready_when_offline = true; // Should nodes be considered ready even when offline
    public final boolean require_tested_path_before_send = true; // For normal parcels, do we require a tested (path with >1 use) before sending?
    public final int cooperativity_minimum_sample_size = 5; // Cooperativity will be calculated only after total uses is at least X.
    public final double path_reliability_rating_bonus = 0.10;
    public final boolean forward_signals_for_blacklisted_nodes = false;
    public final int signal_attempts = 100; // Attempt to send a signal X times, retrying after each timeout failure.
    public final int timeout_failure_time = 8; // X minutes before a signal is considered failure.
    public final int timeout_failure_expiry = 60; // X minutes before a signal's upon response action is deleted. Must be greater than `timeout_failure_time`. 
    public final int unauthorized_neighborship_expiry = 60; // Only keep unauthorized neighborship connections for the X minutes.
    public final double performance_modifier = 0.9; // Percentage, how intense should resource usage be? Higher value means more intense but faster and more dedicated. Lower means slower but less impactful of host system.
}
