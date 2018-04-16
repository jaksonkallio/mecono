package mecono.parceling.types;

import java.util.ArrayList;
import mecono.node.Mailbox;
import mecono.node.Path;
import mecono.node.RemoteNode;
import mecono.node.SelfNode;
import mecono.parceling.MissingParcelDetailsException;
import mecono.parceling.ParcelType;
import mecono.parceling.Response;
import mecono.parceling.ResponseParcel;
import mecono.protocol.BadProtocolException;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author jak
 */
public class FindResponseParcel extends ResponseParcel implements Response {

	public FindResponseParcel(Mailbox mailbox, TransferDirection direction) {
		super(mailbox, direction);
	}
	
	public void setTargetAnswers(ArrayList<Path> target_answers){
		if(this.target_answers.isEmpty()){
			this.target_answers = target_answers;
		}
	}
	
	public void setTargetAnswers(JSONArray serialized_target_answers){
		setTargetAnswers(unserializeContent(serialized_target_answers));
	}
	
	public ArrayList<Path> getTargetAnswers(){
		return target_answers;
	}

	public void unserializeContent() {

	}
	
	@Override
	public void onReceiveAction() throws BadProtocolException, MissingParcelDetailsException {
		for(Path target_answer : getTargetAnswers()){
			mailbox.getOwner().nodeLog(SelfNode.ErrorStatus.GOOD, SelfNode.LogLevel.VERBOSE, "Target answer: "+target_answer.toString());

			// A protocol policy is to only return paths that start with self node
			if(target_answer.getStop(0).equals(getOriginator())){
				mailbox.getOwner().learnUsingPathExtension(target_answer, (RemoteNode) getOriginator());
			}
		}
	}
	
	@Override
	public String toString(){
		StringBuilder target_answers_str = new StringBuilder();
		if(getTargetAnswers() == null || getTargetAnswers().isEmpty()){
			target_answers_str.append("NA");
		}else{
			for(Path path : getTargetAnswers()){
				if(target_answers_str.length() != 0){
					target_answers_str.append(" ,");
				}
				target_answers_str.append(path.toString());
			}
			
		}
		
		return super.toString() + "[TargetAnswers: " + target_answers_str.toString() + "]";
	}
	
	@Override
	public ParcelType getParcelType() {
        return ParcelType.FIND_RESPONSE;
    }
	
	@Override
	public JSONObject getSerializedContent(){
		JSONObject json_content = new JSONObject();
		JSONArray target_answer_array = new JSONArray();
		
		for(Path path : target_answers){
			target_answer_array.put(path.serialize());
		}
		
        json_content.put("target_answers", target_answer_array);
        return json_content;
	}
	
	private ArrayList<Path> unserializeContent(JSONArray target_answers_json){
		ArrayList<Path> unserialized_target_answers = new ArrayList<>();
		
		for(int i = 0; i < target_answers_json.length(); i++){
			unserialized_target_answers.add(Path.unserialize(target_answers_json.getString(i), getMailbox().getOwner()));
		}
		
		return unserialized_target_answers;
	}
	
	@Override
	public boolean requiresTestedPath(){
		return false;
	}

	private ArrayList<Path> target_answers = new ArrayList<>();
}
