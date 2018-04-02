package mecono.parceling.types;

import mecono.node.Mailbox;
import mecono.node.RemoteNode;
import mecono.parceling.DestinationParcel;
import mecono.parceling.ParcelType;

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
	public String toString(){
		String target_address = "";
		if(getTarget() == null){
			target_address = "NA";
		}else{
			target_address = getTarget().getAddress();
		}
		
		return super.toString() + "[FindTarget: " + target_address + "]";
	}

	public void setTarget(RemoteNode target) {
		this.target = target;
	}

	public RemoteNode getTarget() {
		return target;
	}
	
	@Override
	public ParcelType getParcelType() {
		return ParcelType.FIND;
	}
	
	@Override
	public boolean consultWhenPathUnknown(){
		return false;
	}
	
	private RemoteNode target;
}
