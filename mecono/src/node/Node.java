package node;

public class Node {
	public Node(Address address){
		this.address = address;
	}
	
	public static Node generate(){
		
	}
	
	public String getAddressString(){
		return address.getString() + '!' + coords.x + ',' + coords.y;
	}
	
	public void setLabel(String label){
		this.label = label;
	}
	
	public void setCoords(GeoCoord coords){
		this.coords = coords;
	}
	
	private String label;
	private final Address address;
	private GeoCoord coords;
}
