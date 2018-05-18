package mecono.node;

import java.util.LinkedList;
import java.util.Queue;
import mecono.parceling.ParcelType;


// Contains metadata about the parcels that were successfully sent into the network.
public class ParcelHistoryArchive {
	
	public void addParcelHistoryItem(String parcel_id, ParcelType parcel_type){
		parcel_history.offer(new ParcelHistoryItem(parcel_id, parcel_type));
		trimOld();
	}
	
	public void markParcelResponded(String parcel_id){
		for(ParcelHistoryItem parcel_item : parcel_history){
			if(parcel_item.parcel_id.equals(parcel_id)){
				parcel_item.has_response = true;
				break;
			}
		}
	}
	
	public double getCount(boolean has_response, ParcelType parcel_type){
		int count = 0;
		
		for(ParcelHistoryItem parcel_item : parcel_history){
			if(parcel_item.has_response == has_response && parcel_item.parcel_type == parcel_type){
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
	Queue<ParcelHistoryItem> parcel_history = new LinkedList<>();
	
	// Each parcel history item, has meta data.
	private class ParcelHistoryItem {
		public ParcelHistoryItem(String parcel_id, ParcelType parcel_type){
			this.parcel_id = parcel_id;
			this.parcel_type = parcel_type;
		}
	
		public final String parcel_id;
		public boolean has_response = false;
		public final ParcelType parcel_type;
	}
}
