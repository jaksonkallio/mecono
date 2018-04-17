package mecono.parceling.types;

import java.util.ArrayList;
import mecono.node.Mailbox;
import mecono.node.Path;
import mecono.node.RemoteNode;
import mecono.parceling.DestinationParcel;
import mecono.parceling.MissingParcelDetailsException;
import mecono.parceling.ParcelType;
import mecono.protocol.BadProtocolException;
import org.json.JSONObject;

/**
 *
 * @author jak
 */
public class FindParcel extends DestinationParcel {

	public FindParcel(Mailbox mailbox, TransferDirection direction) {
		super(mailbox, direction);
	}

	@Override
	public boolean equals(Object o) {
		FindParcel other = (FindParcel) o;

		return (o instanceof FindParcel && other.getTarget() == this.getTarget() && super.equals(other));
	}

	@Override
	public String toString() {
		String target_address = "";
		if (getTarget() == null) {
			target_address = "NA";
		} else {
			target_address = getTarget().getAddress();
		}

		return super.toString() + "[FindTarget: " + target_address + "]";
	}

	@Override
	public void onReceiveAction() throws BadProtocolException, MissingParcelDetailsException {
		super.onReceiveAction();

		RemoteNode originator = (RemoteNode) getOriginator();

		if (getTarget() == null) {
			throw new MissingParcelDetailsException("Unknown find target");
		}

		FindResponseParcel response = new FindResponseParcel(mailbox, TransferDirection.OUTBOUND);
		RemoteNode target = getTarget();
		response.setRespondedID(getUniqueID());
		ArrayList<Path> available_paths = Path.convertToRawPaths(target.getPathsTo());
		response.setTargetAnswers(available_paths); // Set response to our answer
		response.setDestination(originator); // Set the destination to the person that contacted us (a response)
		response.placeInOutbox(); // Send the response
	}

	public void setTarget(RemoteNode target) {
		this.target = target;
	}

	public RemoteNode getTarget() {
		return target;
	}

	@Override
	public JSONObject getSerializedContent() {
		JSONObject json_content = new JSONObject();
		json_content = json_content.put("target", getTarget().getAddress());
		return json_content;
	}

	@Override
	public ParcelType getParcelType() {
		return ParcelType.FIND;
	}

	@Override
	public boolean consultWhenPathUnknown() {
		return false;
	}

	@Override
	public boolean requiresTestedPath() {
		return false;
	}

	private RemoteNode target;
}
