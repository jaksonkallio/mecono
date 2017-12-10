package mecono;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Foreign parcels are parcels where the SelfNode is neither the originator or
 * destination.
 *
 * @author jak
 */
public class ForeignParcel extends Parcel {

    public ForeignParcel(Mailbox mailbox, Path path_history, String payload) {
        super(mailbox);
        this.path = path;
        this.payload = payload;
    }

    /**
     * Gets the next node.
     *
     * @return RemoteNode The next node
     */
    public RemoteNode getNextDestination() {
        return (RemoteNode) path_history.getStop(path_history.getPathLength() - 1);
    }

    @Override
    public String toString() {
        String next_node_address = "";
        
        try{
            next_node_address = getNextNode().getAddress();
        } catch (MissingParcelDetailsException ex) {
            next_node_address = "Unknown";
        }
        return "Foreign Parcel - Next: " + next_node_address;
    }

    @Override
    public void setPath(Path path) {
        this.path_history = path;
    }

    @Override
    public Path getPath() throws MissingParcelDetailsException {
        if (path_history == null) {
            throw new MissingParcelDetailsException("No path history supplied from the foreign parcel.");
        }

        return path_history;
    }

    /**
     * Gets how many hops this parcel has traveled so far.
     *
     * @return Integer Hop count
     */
    public int getHopCount() {
        return (path_history.getPathLength() - 2);
    }

    @Override
    public RemoteNode getNextNode() throws MissingParcelDetailsException {
        try{
            // For foreign parcels, the next node is the last item in the path.
            return (RemoteNode) getPath().getStop(path.getPathLength() - 1);
        } catch(MissingParcelDetailsException ex){
            mailbox.getOwner().nodeLog(2, "Next node in path not known: " + ex.getMessage());
            throw ex;
        }
    }

    @Override
    public JSONObject serialize() {
        JSONObject serialized_parcel = new JSONObject();
        JSONArray serialized_path_history = new JSONArray();

        try {
            ArrayList<Node> stops = getPath().getStops();
            for (Node stop : stops) {
                serialized_path_history.put(stop.getAddress());
            }

            serialized_parcel.put("path_history", serialized_path_history);
            serialized_parcel.put("payload", payload);
        } catch (MissingParcelDetailsException ex) {
            mailbox.getOwner().nodeLog(2, "Could not serialize parcel: " + ex.getMessage());
        }

        return serialized_parcel;
    }

    @Override
    public Node getOriginator() {
        return path_history.getStop(0);
    }

    private String payload;
    private Path path_history;
}
