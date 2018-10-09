package mecono.parceling;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import mecono.node.Mailbox;
import mecono.node.Node;
import mecono.node.Path;
import mecono.node.RemoteNode;
import mecono.node.SelfNode;
import mecono.node.SelfNode.ErrorStatus;
import mecono.node.SelfNode.LogLevel;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Foreign parcels are parcels where the SelfNode is neither the originator or
 * destination.
 *
 * @author jak
 */
public class ForeignParcel extends Parcel {

	public ForeignParcel(Mailbox mailbox, Path path, String payload) {
		super(mailbox);
		setPath(path);
		this.payload = payload;
	}

	public RemoteNode getNextDestination() throws MissingParcelDetailsException {
		return (RemoteNode) getPath().getStop(getPath().getPathLength() - 1);
	}

	@Override
	public String toString() {
		String str = "Foreign Parcel";

		try {
			str += "[PathHistory: " + getPath().toString() + "]";
			str += "[NextNode: " + getNextNode().getAddress() + "]";
		} catch (MissingParcelDetailsException ex) {
			getMailbox().getOwner().nodeLog(ErrorStatus.FAIL, LogLevel.VERBOSE, "Unknown path history");
		}

		return str;
	}

	/**
	 * Gets how many hops this parcel has traveled so far.
	 *
	 * @return Integer Hop count
	 */
	public int getHopCount() throws MissingParcelDetailsException {
		return (getPath().getPathLength() - 2);
	}

	@Override
	public RemoteNode getNextNode() throws MissingParcelDetailsException {
		return next_node;
	}

	public void setNextNode(RemoteNode next) {
		this.next_node = next;
	}

	@Override
	public JSONObject serialize() {
		JSONObject serialized_parcel = new JSONObject();
		JSONArray serialized_path_history = new JSONArray();

		try {
			ArrayList<Node> stops = getPath().getStops();
			// Add a copy of the path history...
			for (Node stop : stops) {
				serialized_path_history.put(stop.getAddress());
			}
			// ...along with the next node in the path
			serialized_path_history.put(getNextNode().getAddress());

			serialized_parcel.put("path_history", serialized_path_history);
			serialized_parcel.put("payload", payload);
		} catch (MissingParcelDetailsException ex) {
			getMailbox().getOwner().nodeLog(2, "Could not serialize parcel: " + ex.getMessage());
		}

		return serialized_parcel;
	}

	@Override
	public Node getOriginator() throws MissingParcelDetailsException {
		return getPath().getStop(0);
	}

	private String payload;
	private Path path;
	private RemoteNode next_node;
}
