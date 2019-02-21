package mecono;

import node.Node;
import org.json.JSONObject;

public class SimHardwareController extends HardwareController {
	public SimHardwareController(Self self) {
		super(self);
	}
	
	public void setSandbox(Sandbox sandbox){
		this.sandbox = sandbox;
	}
	
	public Sandbox getSandbox(){
		return sandbox;
	}
	
	@Override
	public void send(JSONObject parcel, Node next){
		HardwareController next_hc = getSandbox().lookupHardware(next);
		
		if(next_hc != null && next_hc instanceof SimHardwareController){
			next_hc = (SimHardwareController) next_hc;
			next_hc.receive(parcel);
		}
	}
	
	private Sandbox sandbox;
}
