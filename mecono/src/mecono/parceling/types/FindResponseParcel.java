package mecono.parceling.types;

import java.util.ArrayList;
import mecono.node.Mailbox;
import mecono.node.Path;
import mecono.parceling.DestinationParcel;
import mecono.parceling.ParcelType;
import mecono.parceling.Response;
import org.json.JSONObject;

/**
 *
 * @author jak
 */
public class FindResponseParcel extends DestinationParcel implements Response {

	public FindResponseParcel(Mailbox mailbox, TransferDirection direction) {
		super(mailbox, direction);
	}
	
	public void setTargetAnswers(ArrayList<Path> target_answers){
		if(this.target_answers.isEmpty()){
			this.target_answers = target_answers;
		}
	}
	
	public ArrayList<Path> getTargetAnswers(){
		return target_answers;
	}

	public void unserializeContent() {

	}
	
	@Override
	public ParcelType getParcelType() {
        return ParcelType.FIND_RESPONSE;
    }
	
	@Override
	public JSONObject getSerializedContent(){
		JSONObject json_content = new JSONObject();
        json_content.put("target_answers", target_answers);
        return json_content;
	}
	
	@Override
	public boolean requiresTestedPath(){
		return false;
	}

	private ArrayList<Path> target_answers = new ArrayList<>();
}
