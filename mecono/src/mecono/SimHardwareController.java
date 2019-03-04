package mecono;

import node.Node;
import org.json.JSONObject;

public class SimHardwareController extends HardwareController {
	public SimHardwareController(Self self) {
		super(self);
	}
	
	public void setVirtualEnvironment(VirtualEnvironment ve){
		this.ve = ve;
	}
	
	public VirtualEnvironment getVirtualEnvironment(){
		return ve;
	}
	
	@Override
	public void send(JSONObject parcel, Node next){
		HardwareController next_hc = getVirtualEnvironment().lookupSelf(next.getAddress()).getHardwareController();
		
		if(next_hc != null && next_hc instanceof SimHardwareController){
			next_hc = (SimHardwareController) next_hc;
			next_hc.receive(parcel);
		}
	}
	
	private VirtualEnvironment ve;
}
