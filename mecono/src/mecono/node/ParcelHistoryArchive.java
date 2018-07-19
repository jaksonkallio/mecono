package mecono.node;

import java.util.LinkedList;
import java.util.Queue;
import mecono.parceling.DestinationParcel;
import mecono.parceling.ParcelType;
import mecono.parceling.ResponseParcel;


// Contains metadata about the parcels that were successfully sent into the network.
public class ParcelHistoryArchive {
	
	public void addParcelHistoryItem(DestinationParcel parcel){
		parcel_history.offer(parcel);
		trimOld();
	}
	
	public void markParcelResponded(String parcel_id, ResponseParcel response){
		for(DestinationParcel parcel_item : parcel_history){
			if(parcel_item.getUniqueID().equals(parcel_id)){
				parcel_item.setResponse(response);
				break;
			}
		}
	}
	
	public double getCount(boolean has_response, ParcelType parcel_type){
		int count = 0;
		
		for(DestinationParcel parcel_item : parcel_history){
			if(parcel_item.hasResponse() == has_response && parcel_item.getParcelType() == parcel_type){
				count++;
			}
		}
		
		return count;
	}
	
	private void trimOld(){
		while(parcel_history.size() > HISTORY_LIMIT){
			parcel_history.poll();
		}
	}
	
	// Number of previous parcels to keep
	public final int HISTORY_LIMIT = 1000;
	
	// Parcel history item container
	Queue<DestinationParcel> parcel_history = new LinkedList<>();
}
