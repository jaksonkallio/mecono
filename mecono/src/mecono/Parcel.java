package mecono;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author jak
 */
public abstract class Parcel {
	
	/**
	 * Which node originated this parcel, supposedly.
	 * @return RemoteNode Originator node object.
	 */
	public RemoteNode getOriginator(){
		return (RemoteNode) path.getStop(0);
	}
	
	public void setPath(Path path){
		this.path = path;
	}
	
	/**
	 * Gets the next node in the path.
	 * @return 
	 */
	public RemoteNode getNextNode(){
		return null;
	}
	
	public String serialize(){
		return null;
	}
	
	public static Parcel unserialize(String ser_parcel, SelfNode relative_self) throws BadProtocolException, UnknownResponsibilityException {
		/*
		`[...]` denotes encrypted payload that only destination may access.
		
		pathhistory,[destination,parceltype,originator,content,signature(destination+originator+content)]
		 */
		Parcel received_parcel = null;
		List<String> pieces = Arrays.asList(ser_parcel.split(","));
		Path path = Path.unserialize(pieces.get(0), relative_self);

		if (pieces.get(1).equals(relative_self.getAddress())) {			
			// We are the destination
			ParcelType parcel_type = DestinationParcel.unserializePalletType(pieces.get(2));
			
			switch(parcel_type){
				case PING:
					received_parcel = new PingParcel();
				case PING_RESPONSE:
					received_parcel = new PingResponseParcel();
				case FIND:
					received_parcel = new FindParcel();
				case FIND_RESPONSE:
					received_parcel = new FindResponseParcel();
				case DATA:
					received_parcel = new DataParcel();
				case DATA_RECEIPT:
					received_parcel = new DataReceiptParcel();
				case UNKNOWN:
					throw new UnknownResponsibilityException("Parcel type not recognized.");
			}
			
			received_parcel.setPath(path);
		} else {
			// We are not the destination

			// If selfnode is the second to last node in the path history (last node in history would be the next node) then parcel is in the right place
			if (!path.getStop(path.getPathLength() - 2).equals(relative_self)) {
				throw new UnknownResponsibilityException("SelfNode isn't meant to have this parcel at this point in the path.");
			}

			//return new ForeignParcel(path_history, pieces.get(1));
		}
		
		return received_parcel;
	}
	
	protected Path path_history;
	protected Path path;
}
