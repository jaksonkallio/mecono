package mecono.parceling.types;

import java.util.ArrayList;
import mecono.node.Mailbox;
import mecono.node.Path;
import mecono.node.RemoteNode;
import mecono.parceling.Parcel;
import mecono.parceling.MissingParcelDetailsException;
import mecono.parceling.ParcelType;
import mecono.protocol.BadProtocolException;
import org.json.JSONObject;

/**
 *
 * @author jak
 */
public class FindParcel extends Parcel {

	public FindParcel(Mailbox mailbox) {
		super(mailbox);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof FindParcel) {
			FindParcel other = (FindParcel) o;
			return other.getTarget().equals(this.getTarget()) && super.equals(other);
		}

		return false;
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

		FindResponseParcel response = new FindResponseParcel(getMailbox());
		RemoteNode target = getTarget();
		response.setRespondedID(getUniqueID());
		ArrayList<Path> available_paths = Path.convertToRawPaths(target.getPathsTo());
		response.setTargetAnswers(available_paths); // Set response to our answer
		response.setDestination(originator); // Set the destination to the person that contacted us (a response)
		getMailbox().getHandshakeHistory().enqueueSend(response); // Send the response
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
	public boolean requiresOnlinePath() {
		return false;
	}

	private RemoteNode target;
}
