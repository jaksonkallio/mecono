package parcel;

import mecono.Self;
import org.json.JSONObject;

public class Data extends Trigger {
	public Data(Self self){
		super(self);
	}
	
	@Override
	public ParcelType getParcelType(){
		return ParcelType.DATA;
	}
	
	@Override
	public boolean isDuplicate(Parcel o){
		if(o instanceof Data){
			Data other = (Data) o;
			
			if(this.getMessage().equals(other.getMessage())){
				return super.isDuplicate(o);
			}
		}
		
		return false;
	}
	
	@Override
    public JSONObject serialize(){
        JSONObject parcel_json = super.serialize();
		JSONObject content_json = new JSONObject();
		
		content_json.put("message", getMessage());
		content_json.put("series_identifier", getSeriesIdentifier());
		content_json.put("series_position", getSeriesPosition());
		content_json.put("series_count", getSeriesCount());
		
		parcel_json.put("content", content_json);
		
		System.out.println(parcel_json.toString());
		
		return parcel_json;
    }
	
	public void setMessage(String message){
		this.message = message;
	}
	
	public int getSeriesIdentifier(){
		return 0;
	}
	
	public int getSeriesPosition(){
		return 0;
	}
	
	public int getSeriesCount(){
		return 1;
	}
	
	public String getMessage(){
		return message;
	}
	
	private String message;
}
