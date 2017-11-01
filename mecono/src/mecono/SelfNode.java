package mecono;

import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jak
 */
public class SelfNode implements Node {

	public SelfNode(String label, NodeAddress address) {
		this.label = label;
		this.address = address;
		this.memory_controller = new MemoryController(this);
		mailbox = new Mailbox(this);
		nodeLog(0, "SelfNode \""+getAddressLabel()+"\" started.");
	}
	
	public SelfNode(String label) {
		this(label, new NodeAddress());
	}
	
	public SelfNode() {
		this("Unnamed");
	}
	
	public void generateNewAddress(){
		address = new NodeAddress();
		nodeLog(0, "SelfNode \""+getAddressLabel()+"\" now uses address \""+getAddress()+"\".");
	}

	@Override
	public String getAddress() {
		return address.getAddressString();
	}
	
	public String getAddressLabel() {
		return getAddress().substring(0, 3);
	}

	@Override
	public String getLabel() {
		return label;
	}

	public void receiveParcel(DestinationParcel parcel) {
		nodeLog(1, "Data received via mecono network: "+parcel.toString());
	}
	
	public void nodeLog(int importance, String message){
		String[] importance_levels = {"INFO", "NOTE", "WARN", "CRIT"};
		
		if(importance <= (importance_levels.length - 1)){
			System.out.println("["+getAddressLabel()+"]["+importance_levels[importance]+"] "+message);
		}
	}
	
	public Mailbox getMailbox(){
		return mailbox;
	}
	
	public MemoryController getMemoryController(){
		return memory_controller;
	}
	
	public void learnPath(Path path){
		if(path.getStop(0).equals(this)){
			// Verify that stop 0 is the self node
			Path working_path = path;
			
			while(working_path.getPathLength() > 1){
				((RemoteNode) working_path.getStop(working_path.getPathLength() - 1)).learnPath(working_path);
				// Get the subpath, which is the same path but with the last node chopped off.
				working_path = working_path.getSubpath(working_path.getPathLength() - 2);
			}
		}
	}
	
	public void sendDataParcel(RemoteNode destination, String content) throws UnknownResponsibilityException {
		try{
			DataParcel parcel = new DataParcel();
			parcel.setDestination(destination);
			parcel.setContent(content);
			parcel.placeInOutbox();
		} catch (BadProtocolException ex) {
			nodeLog(2, "Cannot send data parcel: "+ex.getMessage());
		}
	}
	
	public boolean isNeighbor(RemoteNode node){
		// Gets if the node is in the community list at hop 1. (index + 1 = hop)
		return community.get(0).contains(node);
	}
	
	public boolean isInCommunity(RemoteNode node){
		// Gets if the node is in the community list at any hop.
		for(ArrayList<RemoteNode> hop : community){
			if(hop.contains(node)){
				return true;
			}
		}
		
		return false;
	}
	
	public void addNeighbor(RemoteNode node){
		community.get(0).add(node);
	}
	
	public ArrayList<ArrayList<RemoteNode>> getCommunity(){
		return community;
	}
	
	public ArrayList<RemoteNode> getTrustedNodes(){
		return trusted_nodes;
	}
	
	private NodeAddress address;
	private String label;
	protected final Mailbox mailbox;
	private MemoryController memory_controller; // The memory controller to load/save different paths, nodes, etc.
	private ArrayList<RemoteNode> neighbors;
	private ArrayList<ArrayList<RemoteNode>> community;
	private ArrayList<RemoteNode> trusted_nodes;
	
	// Node preferences
	public final int offline_successful_ping_threshold = 8; // A successful ping within the last x minutes means the node is online.
	public final int pinned_ping_interval = 4; // How many minutes between each ping to pinned nodes.
	public final boolean ready_when_offline = true; // Should nodes be considered ready even when offline
	public final int cooperativity_minimum_sample_size = 5; // Cooperativity will be calculated only after total uses is at least X.
	public final double cooperativity_rating_bonus = 0.10;
	public final boolean forward_signals_for_blacklisted_nodes = false;
	public final int signal_attempts = 100; // Attempt to send a signal X times, retrying after each timeout failure.
	public final int timeout_failure_time = 8; // X minutes before a signal is considered failure.
	public final int timeout_failure_expiry = 60; // X minutes before a signal's upon response action is deleted. Must be greater than `timeout_failure_time`. 
	public final int unauthorized_neighborship_expiry = 60; // Only keep unauthorized neighborship connections for the X minutes .
}
