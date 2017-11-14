package mecono;

/**
 *
 * @author jak
 */
public class FindParcel extends DestinationParcel {

	public FindParcel(Mailbox mailbox) {
		super(mailbox);
	}

	@Override
	public boolean equals(Object o) {
		FindParcel other = (FindParcel) o;
		return other.getTarget() == this.getTarget() && super.equals(other);
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

	private RemoteNode target;
}
