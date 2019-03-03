package parcel;

import mecono.ErrorLevel;
import mecono.Self;
import node.InsufficientKnowledgeException;

public abstract class Response extends Terminus {
	public Response(Self self){
		super(self);
	}
	
	public String getTriggerID(){
		return trigger_id;
	}
	
	public void setTriggerID(String trigger_id){
		this.trigger_id = trigger_id;
	}
    
    @Override
    public void process(){
        try {
            // Lookup the trigger, give it this parcel
            Trigger trigger = getSelf().lookupTrigger(getTriggerID());
            trigger.setResponse(this);
            trigger.logResponse();
        }catch(InsufficientKnowledgeException ex){
            getSelf().log(ErrorLevel.ERROR, "Unable to process response: " + ex.getMessage());
        }
    }
	
	public abstract ParcelType getTriggerType();
	
	private String trigger_id;
}
