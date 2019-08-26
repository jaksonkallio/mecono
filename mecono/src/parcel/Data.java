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
		JSONObject content_json = parcel_json.getJSONObject("content");
		
		content_json.put("payload", getPayloadHex());
		content_json.put("port", getPort());
		content_json.put("series_identifier", getSeriesID());
		content_json.put("series_position", getSeriesPosition());
		content_json.put("series_count", getSeriesCount());
		
		return parcel_json;
    }
	
	public void setMessage(String message){
		this.message = message;
	}
	
	public int getSeriesID(){
		return series_id;
	}
	
	public void setSeriesID(int series_id){
		this.series_id = series_id;
	}
	
	public void setSeriesPosition(int serial){
		this.serial = serial;
	}
	
	public int getSeriesPosition(){
		return serial;
	}
	
	public int getSeriesCount(){
		return series_count;
	}
	
	public void setSeriesCount(int series_count){
		this.series_count = series_count;
	}
	
	public String getMessage(){
		return message;
	}
	
	public byte[] getPayload(){
		return payload;
	}
	
	public String getPayloadHex(){
		StringBuilder hex = new StringBuilder();
		
		for (byte b : getPayload()) {
			hex.append(String.format("%02X ", b));
		}
		
		return hex.toString();
	}
	
	public void setPayload(byte[] payload){
		this.payload = payload;
	}
	
	public int getPort(){
		return port;
	}
	
	public void setPort(int port){
		this.port = port;
	}
	
	private String message;
	private byte[] payload = new byte[0];
	private int serial;
	private int series_count;
	private int series_id;
	private int port;
}
