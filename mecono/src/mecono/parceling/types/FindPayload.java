package mecono.parceling.types;

import java.util.ArrayList;
import mecono.node.NodeChain;
import mecono.node.RemoteNode;
import mecono.parceling.MissingParcelDetailsException;
import mecono.parceling.Parcel;
import mecono.parceling.PayloadType;
import mecono.parceling.Payload;
import mecono.protocol.BadProtocolException;
import org.json.JSONObject;

/**
 *
 * @author jak
 */
public class FindPayload extends Payload {

	@Override
	public boolean equals(Object o) {
		if (o instanceof FindPayload) {
			FindPayload other = (FindPayload) o;
			return other.getTarget().equals(this.getTarget());
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

		RemoteNode originator = (RemoteNode) getParcel().getOriginator();

		if (getTarget() == null) {
			throw new MissingParcelDetailsException("Unknown find target");
		}

		Parcel parcel = new Parcel(getParcel().getMailbox());
		FindResponsePayload payload = new FindResponsePayload();
		parcel.setPayload(payload);
		
		payload.setRespondedID(getParcel().getUniqueID());
		ArrayList<NodeChain> available_paths = NodeChain.convertToRawPaths(getTarget().getPathsTo());
		payload.setTargetAnswers(available_paths); // Set response to our answer
		parcel.setDestination(originator); // Set the destination to the person that contacted us (a response)
		getParcel().getMailbox().getHandshakeHistory().enqueueSend(parcel); // Send the response
	}

	public void setTarget(RemoteNode target) {
		this.target = target;
	}

	public RemoteNode getTarget() {
		return target;
	}

	@Override
	public JSONObject serializeContent() {
		JSONObject json_content = new JSONObject();
		json_content = json_content.put("target", getTarget().getAddress());
		return json_content;
	}

	@Override
	public PayloadType getPayloadType() {
		return PayloadType.FIND;
	}

	@Override
	public boolean getResolveUnknownPath(){
		return false;
	}

	private RemoteNode target;
}
