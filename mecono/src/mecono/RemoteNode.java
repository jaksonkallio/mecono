package mecono;

/**
 *
 * @author jak
 */
public class RemoteNode implements Node {
    
	public RemoteNode(String address){
		this.address = address;
	}
	
	public String getAddress() {
		return address;
	}
	
	public String getLabel() {
		return label;
	}
	
    protected String address;
	protected String label;
}