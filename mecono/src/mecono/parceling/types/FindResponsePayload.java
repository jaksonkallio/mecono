package mecono.parceling.types;

import java.util.ArrayList;
import mecono.node.Mailbox;
import mecono.node.Path;
import mecono.node.RemoteNode;
import mecono.node.SelfNode;
import mecono.parceling.MissingParcelDetailsException;
import mecono.parceling.PayloadType;
import mecono.parceling.ResponsePayload;
import mecono.protocol.BadProtocolException;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author jak
 */
public class FindResponsePayload extends ResponsePayload {

	public void setTargetAnswers(ArrayList<Path> target_answers) {
		if (this.target_answers.isEmpty()) {
			this.target_answers = target_answers;
		}
	}

	public void setTargetAnswers(JSONArray serialized_target_answers) {
		setTargetAnswers(unserializeContent(serialized_target_answers));
	}

	public ArrayList<Path> getTargetAnswers() {
		return target_answers;
	}

	public void unserializeContent() {

	}

	@Override
	public void onReceiveAction() throws BadProtocolException, MissingParcelDetailsException {
		super.onReceiveAction();

		for (Path target_answer : getTargetAnswers()) {
			getParcel().getMailbox().getOwner().nodeLog(SelfNode.ErrorStatus.GOOD, SelfNode.LogLevel.VERBOSE, "Target answer: " + target_answer.toString());

			// A protocol policy is to only return paths that start with self node
			if (target_answer.getStop(0).equals(getParcel().getOriginator())) {
				getParcel().getMailbox().getOwner().learnUsingPathExtension(target_answer, (RemoteNode) getParcel().getOriginator());
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder target_answers_str = new StringBuilder();
		if (getTargetAnswers() == null || getTargetAnswers().isEmpty()) {
			target_answers_str.append("NA");
		} else {
			for (Path path : getTargetAnswers()) {
				if (target_answers_str.length() != 0) {
					target_answers_str.append(" ,");
				}
				target_answers_str.append(path.toString());
			}

		}

		return super.toString() + "[TargetAnswers: " + target_answers_str.toString() + "]";
	}

	@Override
	public PayloadType getParcelType() {
		return PayloadType.FIND_RESPONSE;
	}

	@Override
	public JSONObject serializeContent() {
		JSONObject json_content = new JSONObject();
		JSONArray target_answer_array = new JSONArray();

		for (Path path : target_answers) {
			target_answer_array.put(path.serialize());
		}

		json_content.put("target_answers", target_answer_array);
		return json_content;
	}

	private ArrayList<Path> unserializeContent(JSONArray target_answers_json) {
		ArrayList<Path> unserialized_target_answers = new ArrayList<>();

		for (int i = 0; i < target_answers_json.length(); i++) {
			unserialized_target_answers.add(Path.unserialize(target_answers_json.getString(i), getParcel().getMailbox().getOwner()));
		}

		return unserialized_target_answers;
	}

	@Override
	public boolean requiresOnlinePath() {
		return false;
	}

	private ArrayList<Path> target_answers = new ArrayList<>();
}