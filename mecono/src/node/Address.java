package node;

public class Address {
	public Address(String address){
		this.address = address;
	}
	
	public String getString(){
		return address;
	}
	
	private final String address;
}
