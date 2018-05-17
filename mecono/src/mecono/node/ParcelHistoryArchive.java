package mecono.node;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import mecono.parceling.ParcelType;


// Contains metadata about the parcels that were successfully sent into the network.
public class ParcelHistoryArchive {
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
