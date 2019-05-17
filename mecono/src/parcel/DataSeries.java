package parcel;

import java.util.ArrayList;
import java.util.List;
import mecono.Self;

public class DataSeries {
	public DataSeries(Self self){
		this.self = self;
		completed = new ArrayList<>();
		series_partitions = new ArrayList<>();
	}
	
	public void setByteData(byte[] bytes){
		int byte_index = 0;
		byte[] partition_bytes = null;
		
		while(byte_index < bytes.length){
			if(byte_index % BYTES_PER_DATA_PARCEL == 0){
				if(byte_index > 0){
					addContentPartition(partition_bytes);
				}
				
				partition_bytes = new byte[BYTES_PER_DATA_PARCEL];
			}
			
			partition_bytes[byte_index % BYTES_PER_DATA_PARCEL] = bytes[byte_index];
		}
		
		if(byte_index % BYTES_PER_DATA_PARCEL != 0 && partition_bytes != null){
			addContentPartition(partition_bytes);
		}
	}
	
	private void addContentPartition(byte[] partition_bytes){
		series_partitions.add(partition_bytes);
	}
	
	public int getSeriesCount(){
		return series_partitions.size();
	}
	
	public void setCompleted(int serial){
		if(serial < 0 || serial >= getSeriesCount()){
			return;
		}
		
		boolean extended_end = false;
		boolean extended_range = false;
		CompletionRange prev = null;
		
		for(int i = 0; i < completed.size(); i++){
			CompletionRange r = completed.get(i);
			
			if((r.end + 1) == serial){
				r.end++;
				prev = r;
				extended_end = true;
				extended_range = true;
				continue;
			}
			
			if((r.start - 1) == serial){
				if(extended_end){
					prev.end = r.end;
					completed.remove(i);
				}else{
					r.start--;
				}

				extended_range = true;
				break;
			}
		}
		
		if(!extended_range){
			CompletionRange new_range = new CompletionRange();
			new_range.start = serial;
			new_range.end = serial;
			completed.add(new_range);
		}
	}
	
	public double getProgress(){
		return getCompleteCount() / getSeriesCount();
	}
	
	public int getCompleteCount(){
		int complete_count = 0;
		
		for(CompletionRange r : completed){
			complete_count += r.end - r.start + 1;
		}
		
		return complete_count;
	}
	
	public int getIncompleteCount(){
		return getSeriesCount() - getCompleteCount();
	}
	
	public int getDataSeriesID(){
		if(data_series_id == 0){
			setDataSeriesID(self.rng.nextInt());
		}
		
		return getDataSeriesID();
	}
	
	public void setDataSeriesID(int data_series_id){
		this.data_series_id = data_series_id;
	}
	
	public static final int BYTES_PER_DATA_PARCEL = 2048;
	
	private class CompletionRange {
		public int start;
		public int end;
	}
	
	private final List<CompletionRange> completed;
	private List<byte[]> series_partitions;
	private final Self self;
	private int data_series_id;
}
