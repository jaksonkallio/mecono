package mecono;

import node.BadProtocolException;
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
	public void send(JSONObject parcel, Node next) throws BadProtocolException {
		HardwareController next_hc = getVirtualEnvironment().lookupSelf(next.getAddress()).getHardwareController();
		
		if(next_hc == null){
			throw new BadProtocolException("Next simulated hardware controller unknown");
		}

		if(!(next_hc instanceof SimHardwareController)){
			throw new BadProtocolException("Next hardware network controller is not simulated");
		}

		next_hc = (SimHardwareController) next_hc;
		next_hc.receive(parcel);
	}
	
	private VirtualEnvironment ve;
}
