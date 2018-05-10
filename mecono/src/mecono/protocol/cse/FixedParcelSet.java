package mecono.protocol.cse;

import java.util.ArrayList;
import mecono.parceling.DestinationParcel;

/**
 *
 * @author jak
 */
public interface FixedParcelSet {
	// Success is the percentage of parcels that were sent successfully with a response
	public double getSuccessRate();
	
	// Average ping, only counting the most ideal paths to each node that have 1+ successes
	public long avgPing();
	
	// Percentage of parcels that are waiting for the right conditions
	public double getWaitRate();

	public void setParcelSet(ArrayList<DestinationParcel> parcels);

	public ArrayList<DestinationParcel> getParcelSet();
}
