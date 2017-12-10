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

	public void setTarget(RemoteNode target) {
		this.target = target;
	}

	public RemoteNode getTarget() {
		return target;
	}
	
	public ParcelType getParcelType() {
		return ParcelType.FIND;
	}
	
	@Override
	public boolean consultWhenPathUnknown(){
		return false;
	}
	
	private RemoteNode target;
}
