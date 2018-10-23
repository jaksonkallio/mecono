package mecono.node;

import java.util.LinkedList;
import java.util.Queue;
import mecono.parceling.Handshake;
import mecono.parceling.MissingParcelDetailsException;
import mecono.parceling.PayloadType;

// Contains metadata about the parcels that were successfully sent into the network
public class ParcelHistoryStats {

	public void addParcelHistoryItem(Handshake handshake) throws MissingParcelDetailsException {
		ParcelHistoryEntry entry = new ParcelHistoryEntry();
		entry.type = handshake.getTriggerParcel().getParcelType();
		entry.turnaround = handshake.getPing();
		entry.time = handshake.getTriggerParcel().getTimeCreated();
		entry.has_response = handshake.hasResponse();
		parcel_history.offer(entry);
		trimOld();
	}

	public double getCount(boolean has_response, PayloadType parcel_type) {
		int count = 0;

		for (ParcelHistoryEntry entry : parcel_history) {
			if (entry.has_response == has_response && entry.type == parcel_type) {
				count++;
			}
		}

		return count;
	}

	private void trimOld() {
		while (parcel_history.size() > HISTORY_LIMIT) {
			parcel_history.poll();
		}
	}

	private class ParcelHistoryEntry {
		public PayloadType type;
		public long turnaround; // Time between send and response
		public boolean has_response;
		public long time; // Epoch second
	}
	
	// Number of previous parcels to keep
	public final int HISTORY_LIMIT = 1000;
	
	// Parcel history item container
	Queue<ParcelHistoryEntry> parcel_history = new LinkedList<>();
}
