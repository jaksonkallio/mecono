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
		pathhistory,[destination,parceltype,officialpath,content,signature(destination+originator+content)]
		 */
		List<String> pieces = Arrays.asList(ser_parcel.split(","));
		// This is the path given to us by the path_history segment of the serialized parcel.
		Path informal_path = Path.unserialize(pieces.get(0), relative_self);

		if (pieces.get(1).equals(relative_self.getAddress())) {			
			// We are the destination
			
			DestinationParcel received_parcel = null;
			ParcelType parcel_type = DestinationParcel.unserializePalletType(pieces.get(2));
			// This is the path given to us (untamperable) by the originator.
			Path formal_path = Path.unserialize(pieces.get(3), relative_self);
			String content = pieces.get(4);
			String signature = pieces.get(5);
			
			switch(parcel_type){
				case PING:
					received_parcel = new PingParcel();
					break;
				case PING_RESPONSE:
					received_parcel = new PingResponseParcel();
					break;
				case FIND:
					received_parcel = new FindParcel();
					break;
				case FIND_RESPONSE:
					received_parcel = new FindResponseParcel();
					break;
				case DATA:
					received_parcel = new DataParcel();
					break;
				case DATA_RECEIPT:
					received_parcel = new DataReceiptParcel();
					break;
				default:
					throw new UnknownResponsibilityException("Parcel type not recognized.");
			}
			
			received_parcel.setPath(formal_path);
			received_parcel.setContent(content);
			
			return received_parcel;
		} else {
			// We are not the destination
			
			// Get the payload
			String payload = pieces.get(1);
			
			// If selfnode is the second to last node in the path history (last node in history would be the next node) then parcel is in the right place
			if (!informal_path.getStop(informal_path.getPathLength() - 2).equals(relative_self)) {
				throw new UnknownResponsibilityException("SelfNode isn't meant to have this parcel at this point in the path.");
			}
			
			if(pieces.size() != 2){
				throw new BadProtocolException("Foreign parcel has an illegal number ("+pieces.size()+") of pieces to parse.");
			}
			
			if(payload.length() < 5){
				// TODO: Do a better job of verifying that the encrypted payload is legit
				throw new BadProtocolException("The encrypted payload is corrupt.");
			}
			
			return new ForeignParcel(informal_path, payload);
		}
	}
	
	protected Path path_history;
	protected Path path;
}
